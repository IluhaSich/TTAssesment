package com.project.tta.services.parser;

import com.project.tta.models.Setting;
import com.project.tta.models.TTEvaluation;
import com.project.tta.services.TimeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class TimeTableParser {
    static final String HOME_PATH = "https://www.miit.ru";
    private static final Logger log = LoggerFactory.getLogger(TimeTableParser.class);

//    public void getLinks(EvaluationService evaluationService, TTAService ttaService) throws IOException, ExecutionException, InterruptedException {
//        Document doc = Jsoup.connect(HOME_PATH + "/timetable/").get();
//        Elements groups = doc.getElementsByClass("timetable-url d-inline-block");
//        for (Element group : groups) {
//            Element aAttrs = group.selectFirst("a");
//            if (!aAttrs.attr("href").equals("#")) {
//                var linkArr = aAttrs.attr("href").split("/");
//                String link = linkArr[linkArr.length - 1];
//                if (!ttaService.existByLink(link)) {
////                    ExecutorService executor = Executors.newSingleThreadExecutor();
////                    Callable<TimeTable> task = () -> getTimeTable(link);
////                    Future<TimeTable> future = executor.submit(task);
////                    evaluationService.evaluateTimeTable(future.get());
////                    executor.shutdown();
//
//                    evaluationService.evaluateTimeTable(getTimeTable(link));
//                }
//                //TODO: update!
//
//            } else {
//                Elements sameNameGroups = group.getElementsByClass("dropdown-menu")
//                        .select("a");
//                for (Element g : sameNameGroups) {
//                    var linkArr = g.attr("href").split("/");
//                    String link = linkArr[linkArr.length - 1];
//                    if (!ttaService.existByLink(link)) {

    /// /                        ExecutorService executor = Executors.newSingleThreadExecutor();
    /// /                        Callable<TimeTable> task = () -> getTimeTable(link);
    /// /                        Future<TimeTable> future = executor.submit(task);
    /// /                        evaluationService.evaluateTimeTable(future.get());
    /// /                        executor.shutdown();
//
//                        evaluationService.evaluateTimeTable(getTimeTable(link));
//                    }
//                    //TODO: update!
//                }
//            }
//        }
//        //600 записей за 46 секунд
//
//    }//парсинг за 6 минут
    public String getGroupName(Document document) throws IOException {
        var h1 = document.selectFirst("h1").text().split(" ");
        String name = h1[h1.length - 1];
        return name;
    }

    public Integer getCourse(String groupName) throws IOException {
        return Integer.parseInt(groupName.split("-")[1].substring(0, 1));
    }

    public boolean isEmpty(Document document) {
        var a = document.selectFirst("section").text().contains("Информация о расписании отсутствует");
        return false;
    }

    private Document fetchDocumentWithRetry(String url, int maxRetries, int timeoutMillis) throws IOException {
        IOException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return Jsoup.connect(url)
                        .timeout(timeoutMillis)
                        .get();
            } catch (IOException e) {
                lastException = e;
                log.warn("Попытка {} из {} не удалась при запросе {}: {}", attempt, maxRetries, url, e.getMessage());
                try {
                    // пауза между попытками (например, 2 секунды)
                    Thread.sleep(2000L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Ожидание между retry было прервано", ie);
                }
            }
        }

        throw new IOException("Не удалось получить документ после " + maxRetries + " попыток", lastException);
    }


    public String[][] getTimeTable(String link) throws IOException {
        int n = 12;
        int m = 8;
        Document doc = fetchDocumentWithRetry(HOME_PATH + "/timetable/" + link, 3, 10_000);


        if (isEmpty(doc)) {
            throw new RuntimeException("HTML page is not containing time table");
        }
        if (isPageEmpty(doc)) {
            return null;
        }

        String[][] timeTable = new String[n][m];
        String[][] firstWeek = getWeekTimetable(doc, "#week-1");
        String[][] secondWeek = getWeekTimetable(doc, "#week-2");

        if (firstWeek == null) {
            firstWeek = new String[6][8];
        }
        if (secondWeek == null) {
            secondWeek = new String[6][8];
        }

        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < m; j++) {
                timeTable[i][j] = firstWeek[i][j];
            }
        }

        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < m; j++) {
                timeTable[i + n / 2][j] = secondWeek[i][j];
            }
        }

        return timeTable;
    }


//    public TimeTable getTimeTable(String link) throws IOException {
//        int n = 12;
//        int m = 8;
//        Document doc = Jsoup.connect(HOME_PATH + "/timetable/" + link).get();
//        if (isEmpty(doc)) {
//            throw new RuntimeException("HTML page is not containing time table");
//        }
//        if (isPageEmpty(doc)) {
//            return null;
//        }
//
//        String[][] timeTable = new String[n][m];
//        String[][] firstWeek = getWeekTimetable(doc, "#week-1");
//        String[][] secondWeek = getWeekTimetable(doc, "#week-2");
//        if (firstWeek == null && secondWeek == null) return null;
//        for (int i = 0; i < n / 2; i++) {
//            for (int j = 0; j < m; j++) {
//                timeTable[i][j] = firstWeek[i][j];
//            }
//        }
//
//        for (int i = 0; i < n / 2; i++) {
//            for (int j = 0; j < m; j++) {
//                timeTable[i + n / 2][j] = secondWeek[i][j];
//            }
//        }
//
//        var name = getGroupName(doc);
//        var course = getCourse(name);
//        return new TimeTable(name, link, course, timeTable);
//    }

    private String[][] getWeekTimetable(Document doc, String weekNum) {
        int n = 6;
        int m = 8;
        String[][] timeTable = new String[n][m];
        Elements elements;
        try {
            elements = doc.selectFirst(weekNum).getElementsByTag("tr").select("td");
            String text = elements.text();

            if (text.contains("08:00 — 08:45")
                    || text.contains("08:55 — 09:40")
                    || text.contains("10:00 — 10:45")
                    || text.contains("10:45 — 11:10")
                    || text.contains("11:50 — 12:35")
                    || text.contains("13:20 — 14:05")
                    || text.contains("14:15 — 15:00")
                    || text.contains("15:10 — 15:55")
                    || text.contains("16:15 — 17:00")
                    || text.contains("17:10 — 17:55")
            ) return null;


        } catch (NullPointerException e) {
            return timeTable;
        }

        Elements week = doc.selectFirst(weekNum).getElementsByTag("tr").select("th");

        int weekStarts = getStartDayIndex(week);
        int weekEnds = getEndDayIndex(week);

        int currentIndex = 0;
        int startLesson = Integer.parseInt(elements.getFirst().text().substring(0, 1));


        for (int lesson = startLesson - 1; lesson < 8; lesson++) {
            if (currentIndex < elements.size() && elements.get(currentIndex).hasClass("text-right")) {
                currentIndex++;
            }
            for (int day = weekStarts; day <= weekEnds; day++) {
                if (currentIndex < elements.size()) {
                    Element row = elements.get(currentIndex);
                    if (row.hasClass("timetable__grid-day")) {
                        timeTable[day][lesson] = row.text();
                    }
                    currentIndex++;
                }
            }
        }
        return timeTable;
    }

    private int getStartDayIndex(Elements headers) {
        List<String> days = Arrays.asList("понедельник", "вторник", "среда", "четверг", "пятница", "суббота"); //ПОН = 0, ...
        for (int i = 0; i < headers.size(); i++) {
            String day = headers.get(i).text().toLowerCase();
            for (int j = 0; j < days.size(); j++) {
                if (day.contains(days.get(j))) {
                    return j;
                }
            }
        }
        return 0;
    }

    private int getEndDayIndex(Elements headers) {
        List<String> days = Arrays.asList("понедельник", "вторник", "среда", "четверг", "пятница", "суббота");
        for (int i = headers.size() - 1; i >= 0; i--) {
            String day = headers.get(i).text().toLowerCase();
            for (int j = days.size() - 1; j >= 0; j--) {
                if (day.contains(days.get(j))) {
                    return j;
                }
            }
        }
        return 5;
    }

    private boolean isPageEmpty(Document document) {
        // 1. Проверка: "Информация о расписании отсутствует"
        Element content = document.selectFirst("div.container > p");
        boolean noSchedule = content != null && content.text().contains("Информация о расписании отсутствует");
        // 1.5. Проверка: "Расписание отсутствует"
        boolean noSchedule2 = content != null && content.text().contains("Расписание отсутствует");

        // 2. Проверка: "Отобразить прошедшие события"
        Element toggle = document.selectFirst(".timetable__toggle-past-days-display");
        boolean onlyPastEvents = toggle != null && toggle.text().contains("Отобразить прошедшие события");

        // 3. Проверка: все вкладки — "Сессия" или "Разовое"
        Elements tabs = document.select("ul.nav-tabs > li.nav-item");
        boolean allIrrelevantTypes = false;
        if (!tabs.isEmpty()) {
            allIrrelevantTypes = tabs.stream()
                    .allMatch(tab -> {
                        Element small = tab.selectFirst("small");
                        return small != null && (small.text().contains("Сессия") || small.text().contains("Разовое"));
                    });
        }

        return noSchedule || noSchedule2 || onlyPastEvents || allIrrelevantTypes;
    }


    public static String printTimeTable(String[][] timeTable) {
        if (timeTable == null) {
            System.out.println("Расписание пусто");
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String[] j : timeTable) {
            for (String i : j) {
                if (i != null && !i.isEmpty()) {
                    stringBuilder.append(i);
                    stringBuilder.append("\n");
                } else {
                    stringBuilder.append("nothing");
                    stringBuilder.append("\n");
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
