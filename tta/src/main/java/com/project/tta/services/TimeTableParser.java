package com.project.tta.services;

import com.project.tta.models.GroupLink;
import org.springframework.stereotype.Service;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeTableParser {
    static final String HOME_PATH = "https://www.miit.ru";
    public List<GroupLink> getLinks() throws IOException {
        List<GroupLink> links = new ArrayList<>();
        Document doc = Jsoup.connect(HOME_PATH + "/timetable").get();
        Elements groups = doc.getElementsByClass("timetable-url d-inline-block");
        for (Element group : groups) {
            Element aAttrs = group.selectFirst("a");
            if (!aAttrs.attr("href").equals("#")) {
                links.add(new GroupLink(
                        group.select("a").text(),
                        aAttrs.attr("href")));
            } else {
                Elements sameNameGroups = group.getElementsByClass("dropdown-menu")
                        .select("a");
                for (Element g : sameNameGroups) {
                    links.add(new GroupLink(
                            g.text()
                            , g.attr("href")));
                }
            }
        }
        return links;
    }

     public String[][] getTimeTable(String link) throws IOException {
        int n = 12;
        int m = 8;
        Document doc = Jsoup.connect(HOME_PATH + link).get();
        String[][] timeTable = new String[n][m];
        String[][] firstWeek = getWeekTimetable(doc, "#week-1");
        String[][] secondWeek = getWeekTimetable(doc, "#week-2");
        if (firstWeek == null && secondWeek == null) return null;
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < m - 1; j++) {
                timeTable[i][j] = firstWeek[i][j];
            }
        }

        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < m - 1; j++) {
                timeTable[i + n / 2][j] = secondWeek[i][j];
            }
        }
        return timeTable;
    }

    private String[][] getWeekTimetable(Document doc, String weekNum) throws IOException {
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

        if (!week.getLast().text().toLowerCase().contains("суббота")) {
            n--;
        }

        int q = (Integer.parseInt(elements.getFirst().text().substring(0, 1)) - 1) * n;

        for (Element row : elements) {
            if (!row.hasClass("text-right")) {
                timeTable[q % n][(int) Math.floor(q / n)] = row.text();
//                System.out.println(q % n + " " + Math.floor(q / n) + " " + row.text());
                q++;
            }
        }
        return timeTable;
    }
    public String printTimeTable(String[][] timeTable) {
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
        }
        return stringBuilder.toString();
    }
}
