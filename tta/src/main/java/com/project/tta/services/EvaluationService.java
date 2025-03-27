package com.project.tta.services;

import com.project.tta.services.interfaces.EvaluationInterface;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

@Service
public class EvaluationService implements EvaluationInterface {
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    private final TimeTableParser timeTableParser;

    public EvaluationService(TimeTableParser timeTableParser) {
        this.timeTableParser = timeTableParser;
    }

//
//    public int evaluateTable(String[][] table) {
//        if (table == null) {
//            //TODO: throw new ...
//            return -666;
//        }
//        int grade = 0;
//        grade += evaluateByWindowsInDay(table);
//        grade += evaluateByWindowsWeek(table);
//
//        return grade;
//    }
//
//    public int evaluateByWindowsInDay(String[][] table) {
//        // Оценивает количество пар в день и возвращает оценку
//        // TODO: Вообще должен определять окна в расписании а не количество пар в день
//        int grade = 0;
//        int lessonQuantity = 0;
//        for (String[] tableDay : table) {
//            lessonQuantity = lessonQuantityPerDay(tableDay);
//        }
//        switch (lessonQuantity) {
//            case 8: {
//                grade -= 15; //8 пар = ад
//                break;
//            }
//            case 7: { // 7 пар
//                grade -= 10;
//                break;
//            }
//            case 6, 5: { // 6,5 пар
//                grade -= 7;
//                break;
//            }
//            case 4, 3: { // 4, 3 пары
//                grade += 2;
//                break;
//            }
//            case 2: { // 2 пары
//                grade -= 1;
//                break;
//            }
//            case 1: { // 1 пара
//                grade -= 3;
//            }
//        }
//
//        return grade;
//    }
//
//    public int evaluateByWindowsWeek(String[][] table) {
//        // Оценивает количество свободных дней и возвращает оценку
//        boolean[] weekLiberty = new boolean[12];
//        int grade = 0;
//        for (int i = 0; i < table[0].length; i++) {
//            weekLiberty[i] = dayIsFree(table[i]);
//        }
//        int libertyQuantity = 0;
//        for (boolean dayLiberty : weekLiberty) { // Определяет количество свободных дней
//            if (dayLiberty) libertyQuantity++;
//        }
//        switch (libertyQuantity) {
//            case 11: { // 2 субботы
//                grade -= 10;
//                break;
//            }
//            case 10: { // 1 суббота
//                grade -= 5;
//                break;
//            }
//            case 8, 7: { // 4 дня учебы
//                grade += 3;
//                break;
//            }
//            case 6, 4, 3, 2, 1: { // до 6 пар в 2ух неделях
//                grade += 10;
//                break;
//            }
//        }
//
//        return grade;
//    }
//
//


    @Override
    public int evaluateTimeTable(String[][] table) {
        if (table == null) {
            //TODO: throw new ...
            return -666;
        }
        int grade = 0;
        grade += evaluateGaps(table);
        grade += evaluateStudyDays(table);
        grade += evaluateDailyLoad(table);
        grade += evaluateLessonStartTime(table);
        grade += evaluateLessonEndTime(table);
        grade += evaluateWeekendDistribution(table);
        grade += evaluateForHavingLongBreak(table);
        return grade;
    }

    @Override
    public int evaluateGaps(String[][] table) {
        return 0;
    }

    @Override
    public int evaluateStudyDays(String[][] table) {

        for (String[] day : table) {
            if (dayIsFree(day)) ;
        }
        return 0;
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
        log.info("evaluate by weekend distribution and return {}", result);
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
        log.info("evaluate by weekend distribution and return {}", result);
        return result;
    }

        @Override
        public int evaluateWeekendDistribution(String[][] table){
            return 0;
        }

        @Override
        public int evaluateForHavingLongBreak(String[][] table){
            int daysWithBreaks = 0;

            for (String[] day : table) {
                if (dayIsFree(day)) continue;

                boolean hasLongBreak = false;

                for (int i = 0; i < day.length - 1; i++) {
                    String currentEnd = day[i].split("-")[1];
                    String nextStart = day[i+1].split("-")[0];

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
            log.info("evaluate by weekend distribution and return {}", result);
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
    private static int getLessonQuantity (String[]dayTable){
        return (int) Arrays.stream(dayTable).filter(str -> !str.equals("nothing")).count();
    }
    /**
     * @param dayTable полное расписание (на две недели)
     * @return Возвращает количество всех пар
     */
    private static int getLessonQuantity (String[][]dayTable){
        return (int) Arrays.stream(dayTable).mapToInt(EvaluationService::getLessonQuantity).sum();
    }

    /**
     * @param dayTable расписание на день
     * @return Возвращает true если в этот день нет пар
     */
    private static boolean dayIsFree (String[]  dayTable){
        return Arrays.stream(dayTable).allMatch(str -> str.equals("nothing"));
    }

    /**
     * Возвращает количество свободных дней
     * @param timeTable полное расписание (на две недели)
     * @return количество свободных дней
     */
    private static int getFreeDaysQuantity (String[][]timeTable){
        return (int) Arrays.stream(timeTable).filter(EvaluationService::dayIsFree).count();
    }
}
