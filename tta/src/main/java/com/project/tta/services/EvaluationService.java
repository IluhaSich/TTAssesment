package com.project.tta.services;

import com.project.tta.services.interfaces.EvaluationInterface;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class EvaluationService implements EvaluationInterface {
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
        return 0;
    }

    @Override
    public int evaluateLessonStartTime(String[][] table) {
        return 0;
    }

    @Override
    public int evaluateLessonEndTime(String[][] table) {
        return 0;
    }

    @Override
    public int evaluateWeekendDistribution(String[][] table) {
        return 0;
    }

    @Override
    public int evaluateForHavingLongBreak(String[][] table) {
        return 0;
    }

    /**
     * @param dayTable расписание на день
     * @return Возвращает количество пар в определенный день
     */
    private static int getLessonQuantity(String[] dayTable) {
        return (int) Arrays.stream(dayTable).filter(str -> !str.equals("nothing")).count();
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
        return Arrays.stream(dayTable).allMatch(str -> str.equals("nothing"));
    }

    /**
     * @param timeTable полное расписание (на две недели)
     * @return количество свободных дней
     */
    private static int getFreeDaysQuantity(String[][] timeTable) {
        return (int) Arrays.stream(timeTable).filter(EvaluationService::dayIsFree).count();
    }
}
