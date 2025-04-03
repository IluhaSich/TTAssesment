package com.project.tta.services;

import com.project.tta.models.TimeTableGrade;
import com.project.tta.services.interfaces.EvaluationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

@Service
public class EvaluationService implements EvaluationInterface {
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    private final TimeTableParser timeTableParser;

    public EvaluationService(TimeTableParser timeTableParser) {
        this.timeTableParser = timeTableParser;
    }

    @Override
    public int evaluateTimeTable(String[][] table, boolean senior) {
        if (table == null) {
            log.error("Time table in evaluateTimeTable method input is null. Throw RuntimeException");
            throw new RuntimeException("time table is null");
        }
        int grade = 0;
        var map = new HashMap<String,Integer>(9);
        var tt = new TimeTableGrade();
        tt.addGrade(evaluateGaps(table,map));
        tt.addGrade(evaluateStudyDays(table));
        tt.addGrade(evaluateLoadBalance(table));
        tt.addGrade(evaluateDailyLoad(table));
        tt.addGrade(evaluateLessonStartTime(table));
//        tt.addGrade(evaluateLessonEndTime(table));
        tt.addGrade(evaluateWeekendDistribution(table));
//        grade += evaluateForHavingLongBreak(table);
        log.info("return result from evaluation service : {}", grade);
        return grade;
    }

    @Override
    public Map<String, Integer> evaluateGaps(String[][] table, Map<String, Integer> params) {
        table = Arrays.stream(table)
                .filter(Predicate.not(EvaluationService::dayIsFree)).toArray(String[][]::new);
        for (String[] dayTable : table) {
            int gap = 0;
            var dayTableBool
                    = Arrays.stream(dayTable).map(EvaluationService::isBlank).toArray(Boolean[]::new);
            List<Integer> dayTableInt = new ArrayList<>();
            for (int i = 0; i < dayTableBool.length; i++) {
                if (dayTableBool[i]) {
                    dayTableInt.add(i);
                }
            }
            for (int i = 0; i < dayTableInt.size() - 1; i++) {
                gap = dayTableInt.get(i + 1) - dayTableInt.get(i) - 1;
            }
            if (gap == 0) {
                result += 3;
            } else {
                if (gap == 1) result -= 2;
                if (gap > 1) result -= 5;
            }
        }
        log.info("evaluate by gap and return {}", result);
        return Map.of();
    }

    @Override
    public Map<String, Integer> evaluateStudyDays(String[][] table, Map<String, Integer> params) {
        return Map.of();
    }

    @Override
    public Map<String, Integer> evaluateLoadBalance(String[][] table, Map<String, Integer> params) {
        return Map.of();
    }

    @Override
    public Map<String, Integer> evaluateDailyLoad(String[][] table, Map<String, Integer> params) {
        return Map.of();
    }

    @Override
    public Map<String, Integer> evaluateLessonStartTime(String[][] table, boolean senior, Map<String, Integer> params) {
        return Map.of();
    }

    @Override
    public Map<String, Integer> evaluateLessonEndTime(String[][] table, boolean senior, Map<String, Integer> params) {
        return Map.of();
    }

    @Override
    public Map<String, Integer> evaluateWeekendDistribution(String[][] table, Map<String, Integer> params) {
        return Map.of();
    }

    @Override
    public Map<String, Integer> evaluateForHavingLongBreak(String[][] table, boolean senior, Map<String, Integer> params) {
        return Map.of();
    }


    @Override
    public Map<String, Integer> evaluateStudyDays(String[][] table) {
        int studyDays = table.length - getFreeDaysQuantity(table);
        int result = switch (studyDays) {
            case 12, 11 -> -3;
            case 10, 9 -> 2;
            case 8 -> 5;
            default -> 0;
        };
        log.info("evaluate by study days = {} and return {}", studyDays, result);
        return result;
    }

    @Override
    public Map<String, Integer> evaluateLoadBalance(String[][] table) {
        var quantityArr = Arrays.stream(table).map(EvaluationService::getLessonQuantity).toList();
        int length = quantityArr.size();
        double u = quantityArr.stream().mapToDouble(i -> i).sum() / length; // Среднее значение
        double o = Math.sqrt(
                quantityArr.stream().mapToDouble(i -> Math.pow(i - u, 2)).sum()
                        / (length - 1)); // Стандартное отклонение
        double cv = o / u; // Коэффициент вариации
        int result = -5;
        if (cv < 0.1) result = 5;
        if (cv < 0.2) result = 2;
        if (cv < 0.4) result = 0;
        if (cv < 0.6) result = -3;
        log.info("evaluate by load balance with CV = {} and return {}",
                Math.floor(cv * 100) / 100, result);
        return result;
    }

    @Override
    public Map<String, Integer> evaluateDailyLoad(String[][] table) {
        int totalLessons = getLessonQuantity(table);
        int result;
        if (totalLessons < 20) {
            result = -3;
        } else if (totalLessons > 30) {
            result = -3;
        } else {
            result = 3;
        }
        log.info("evaluate by daily load and return {}", result);
        return result;
    }

    @Override
    public Map<String, Integer> evaluateLessonStartTime(String[][] table) {
        int result = 0;
        var dayStarts = Arrays.stream(table)
                .filter(Predicate.not(EvaluationService::dayIsFree))
                .map(arr -> Arrays.stream(arr)
                        .takeWhile(Predicate.not(EvaluationService::isBlank))
                        .count())
                .toList();
//        System.out.println(dayStarts);
        if (dayStarts.stream().filter(t -> t == 0).count() >= 3) {
            result -= 2;
        } else {
            result += 3;
        }
        if (dayStarts.stream().anyMatch(t -> t > 3)) result += 2;

        log.info("evaluate by lesson start time and return {}", result);
        return result;
    }

    @Override
    public Map<String, Integer> evaluateLessonEndTime(String[][] table) {
        int lateDays = 0;
        int earlyDays = 0;

        for (String[] day : table) {
            if (dayIsFree(day)) continue;

            int lastPairIndex = findLastPairIndex(day);

            if (lastPairIndex >= 5) {
                lateDays++;
            } else {
                earlyDays++;
            }
        }

        int result;
        if (lateDays > 6) {
            result = -3;
        } else if (earlyDays >= 6) {
            result = 3;
        } else {
            result = 0;
        }
        log.info("evaluate by lessonEndTime and return {}", result);
        return result;
    }

    private static int findLastPairIndex(String[] day) {
        for (int i = day.length - 1; i >= 0; i--) {
            if (!day[i].equals("nothing")) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Map<String, Integer> evaluateWeekendDistribution(String[][] table) {
        int result = -2;
        if (dayIsFree(table[5]) || dayIsFree(table[6])
                && dayIsFree(table[12]) || dayIsFree(table[7])) result = 3;
        for (int i = 1; i <= table.length - 2; i++) {
            if (dayIsFree(table[i])) {
                if (dayIsFree(table[i + 1])) result = 3;
            }
        }
        log.info("evaluate by weekend distribution and return {}", result);
        return result;
    }

    @Override
    public Map<String, Integer> evaluateForHavingLongBreak(String[][] table) {
        int result = 0;

        for (String[] day : table) {
            if (dayIsFree(day)) continue;

            if (hasBreakBetweenThirdAndFourthPair(day)) {
                result -= 2;
            }
            else if (allPairsConsecutive(day)) {
                result += 2;
            }
            else {
                result += 0;
            }
        }

        log.info("evaluate by having long break and return {}", result);
        return result;
    }

    private boolean hasBreakBetweenThirdAndFourthPair(String[] day) {
        boolean hasThirdPair = !day[2].equals("nothing");
        boolean hasFourthPair = !day[3].equals("nothing");

        return hasThirdPair && hasFourthPair;
    }

    private boolean allPairsConsecutive(String[] day) {
        boolean endsAtThirdPair = day[3].equals("nothing") &&
                day[4].equals("nothing") &&
                day[5].equals("nothing") &&
                day[6].equals("nothing") &&
                day[7].equals("nothing");

        boolean startsFromFourthPair = day[0].equals("nothing") &&
                day[1].equals("nothing") &&
                day[2].equals("nothing");

        return endsAtThirdPair || startsFromFourthPair;
    }

    /**
     * @param dayTable расписание на день
     * @return Возвращает количество пар в определенный день
     */
    private static int getLessonQuantity(String[] dayTable) {
        return (int) Arrays.stream(dayTable).filter(EvaluationService::isBlank).count();
    }

    /**
     * @param timeTable полное расписание (на две недели)
     * @return Возвращает количество всех пар
     */
    private static int getLessonQuantity(String[][] timeTable) {
        return Arrays.stream(timeTable).mapToInt(EvaluationService::getLessonQuantity).sum();
    }

    /**
     * @param dayTable расписание на день
     * @return Возвращает true если в этот день нет пар
     */
    private static boolean dayIsFree(String[] dayTable) {
        return Arrays.stream(dayTable).noneMatch(EvaluationService::isBlank);
    }

    /**
     * @param timeTable полное расписание (на две недели)
     * @return количество свободных дней
     */
    private static int getFreeDaysQuantity(String[][] timeTable) {
        return (int) Arrays.stream(timeTable).filter(EvaluationService::dayIsFree).count();
    }

    /**
     * Делает проверку: str != null && !str.isEmpty();
     *
     * @param str строка с информацией о паре
     * @return true если строка не пуста
     */

    private static boolean isBlank(String str) {
        return str != null && !str.isEmpty();
    }

    public static void main(String[] args) throws IOException {
        TimeTableParser timeTableParser1 = new TimeTableParser();
        EvaluationService eva = new EvaluationService(timeTableParser1);
        var t = timeTableParser1.getTimeTable("/timetable/189115");
        eva.evaluateTimeTable(t,false);
        eva.evaluateTimeTable(t,true);

//        eva.evaluateLessonStartTime(t);
//        System.out.println(timeTableParser1.printTimeTable(t));
    }
}
