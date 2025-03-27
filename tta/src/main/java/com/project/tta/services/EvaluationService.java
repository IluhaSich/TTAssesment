package com.project.tta.services;

import com.project.tta.services.interfaces.EvaluationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class EvaluationService implements EvaluationInterface {
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    private final TimeTableParser timeTableParser;

    public EvaluationService(TimeTableParser timeTableParser) {
        this.timeTableParser = timeTableParser;
    }

    @Override
    public int evaluateTimeTable(String[][] table) {
        if (table == null) {
            //TODO: throw new ...
            return -666;
        }
        int grade = 0;
        grade += evaluateGaps(table);
        grade += evaluateStudyDays(table);
        grade += evaluateLoadBalance(table);
        grade += evaluateDailyLoad(table);
        grade += evaluateLessonStartTime(table);
//        grade += evaluateLessonEndTime(table);
        grade += evaluateWeekendDistribution(table);
//        grade += evaluateForHavingLongBreak(table);
        return grade;
    }

    @Override
    public int evaluateGaps(String[][] table) {
        int result = 0;
        for (String[] dayTable : table) {
            int gap = 0;
            var dayTableBool
                    = Arrays.stream(dayTable).map(EvaluationService::isBlank).toArray(Boolean[]::new);
            List<Integer> dayTableInt = new ArrayList<>();
            for (int i = 0; i < dayTableBool.length; i++) {
                if (dayTableBool[i]){
                    dayTableInt.add(i);
                }
            }
            for (int i = 0; i < dayTableInt.size() - 1; i++) {
                gap = dayTableInt.get(i + 1) - dayTableInt.get(i) - 1;
            }
            if (gap == 0) {
                result += 1;
            } else {
                if (gap == 1) result -= 2;
                if (gap > 1) result -= 5;
            }
        }
        log.info("evaluate by gap and return {}", result);
        return result;
    }

    @Override
    public int evaluateStudyDays(String[][] table) {
        int studyDays = (int) Arrays.stream(table).map(EvaluationService::dayIsFree).count();
        int result = switch (studyDays) {
            case 6 -> -3;
            case 5 -> 2;
            case 4 -> 5;
            default -> 0;
        };
        log.info("evaluate by study days {}", result);
        return result;
    }

    @Override
    public int evaluateLoadBalance(String[][] table) {
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
    public int evaluateDailyLoad(String[][] table) {
        if (table == null || table.length == 0) {
            return 0;
        }
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
    public int evaluateLessonStartTime(String[][] table) {
        return 0;
    }

    @Override
    public int evaluateLessonEndTime(String[][] table) {
        int lateDays = 0;
        int earlyDays = 0;

        for (String[] day : table) {
            if (dayIsFree(day)) continue;

            boolean hasLateClass = false;
            boolean allEarly = true;

            for (String lesson : day) {
                String endTime = lesson.split("-")[1];
                int hour = Integer.parseInt(endTime.split(":")[0]);

                if (hour >= 18) hasLateClass = true;
                if (hour >= 16) allEarly = false;
            }

            if (hasLateClass) lateDays++;
            if (allEarly) earlyDays++;
        }
        int result;
        if (lateDays > 3) {
            result = -3;
        } else if (earlyDays >= 3) {
            result = 3;
        } else {
            result = 0;
        }
        log.info("evaluate by lessonEndTime and return {}", result);
        return result;
    }

    @Override
    public int evaluateWeekendDistribution(String[][] table) {
        int result = -2;
        if (dayIsFree(table[5]) || dayIsFree(table[0])) result = 3;
        for (int i = 1; i <= table.length - 2; i++) {
            if (dayIsFree(table[i])) {
                if (dayIsFree(table[i + 1])) result = 3;
            }
        }
        log.info("evaluate by weekend distribution and return {}", result);
        return result;
    }

    @Override
    public int evaluateForHavingLongBreak(String[][] table) {
        int daysWithBreaks = 0;

        for (String[] day : table) {
            if (dayIsFree(day)) continue;

            boolean hasLongBreak = false;

            for (int i = 0; i < day.length - 1; i++) {
                String currentEnd = day[i].split("-")[1];
                String nextStart = day[i + 1].split("-")[0];

                int breakDuration = calculateBreakDuration(currentEnd, nextStart);

                if (breakDuration > 60) {
                    hasLongBreak = true;
                    break;
                }
            }

            if (hasLongBreak) {
                daysWithBreaks++;
            }
        }
        int result;
        if (daysWithBreaks == 0) {
            result = 2;
        } else {
            result = -2;
        }
        log.info("evaluate by having long break and return {}", result);
        return result;
    }

    private static int calculateBreakDuration(String endTime, String startTime) {
        String[] endParts = endTime.split(":");
        String[] startParts = startTime.split(":");

        int endHour = Integer.parseInt(endParts[0]);
        int endMin = Integer.parseInt(endParts[1]);
        int startHour = Integer.parseInt(startParts[0]);
        int startMin = Integer.parseInt(startParts[1]);

        return (startHour * 60 + startMin) - (endHour * 60 + endMin);
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
        return Arrays.stream(dayTable).allMatch(EvaluationService::isBlank);
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
        eva.evaluateTimeTable(t);
    }
}
