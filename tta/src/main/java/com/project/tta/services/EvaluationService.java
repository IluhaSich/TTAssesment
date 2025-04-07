package com.project.tta.services;

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
        var map = new HashMap<String, Integer>(9);
//        var tt = new TtGrade();
//
//        tt.addGrade(evaluateGaps(table, map));
//        tt.addGrade(evaluateStudyDays(table, map));
//        tt.addGrade(evaluateLoadBalance(table, map));
//        tt.addGrade(evaluateDailyLoad(table, map));
//        tt.addGrade(evaluateLessonStartTime(table, senior, map));
//        tt.addGrade(evaluateLessonEndTime(table,senior,map));
//        tt.addGrade(evaluateWeekendDistribution(table, map));
//        tt.addGrade(evaluateForHavingLongBreak(table,senior,map));
        log.info("return result from evaluation service : {}", grade);
        System.out.println(map);
        return grade;
    }

    @Override
    public Map<String, Integer> evaluateGaps(String[][] table, Map<String, Integer> params) {
        int result = 0;
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
                if (gap == 1) result += 1;
            }
        }
        log.info("evaluate by gap and return {}", result);
        params.put("Evaluation by gaps", result);
        return params;
    }

    @Override
    public Map<String, Integer> evaluateStudyDays(String[][] table, Map<String, Integer> params) {
        int studyDays = table.length - getFreeDaysQuantity(table);
        int result = switch (studyDays) {
            case 10 -> 3;
            case 9,8 -> 5;
            default -> 0;
        };
        log.info("evaluate by study days = {} and return {}", studyDays, result);
        params.put("Evaluation by study days",result);
        return params;
    }

    @Override
    public Map<String, Integer> evaluateLoadBalance(String[][] table, Map<String, Integer> params) {
        var quantityArr = Arrays.stream(table).map(EvaluationService::getLessonQuantity).toList();
        int length = quantityArr.size();
        double u = quantityArr.stream().mapToDouble(i -> i).sum() / length; // Среднее значение
        double o = Math.sqrt(
                quantityArr.stream().mapToDouble(i -> Math.pow(i - u, 2)).sum()
                        / (length - 1)); // Стандартное отклонение
        double cv = o / u; // Коэффициент вариации
        int result = -5;
        if (cv < 0.1) result = 5;
        if (cv < 0.2) result = 3;
        if (cv < 0.4) result = 2;
        if (cv < 0.8) result = 1;
        log.info("evaluate by load balance with CV = {} and return {}",
                Math.floor(cv * 100) / 100, result);
        params.put("Evaluation by load balance", result);
        return params;
    }

    @Override
    public Map<String, Integer> evaluateDailyLoad(String[][] table, Map<String, Integer> params) {
        int totalLessons = getLessonQuantity(table);

        int result;
        if (totalLessons >= 20 && totalLessons <= 30) {
            result = 5;
        } else if (totalLessons < 20) {
            result = 3;
        } else {
            result = 2;
        }
        log.info("evaluate by daily load and return {}", result);
        params.put("Evaluation by daily load",result);
        return params;
    }

    @Override
    public Map<String, Integer> evaluateLessonStartTime(String[][] table, boolean senior, Map<String, Integer> params) {
        int result = 0;
        var dayStarts = Arrays.stream(table)
                .filter(Predicate.not(EvaluationService::dayIsFree))
                .map(arr -> Arrays.stream(arr)
                        .takeWhile(Predicate.not(EvaluationService::isBlank))
                        .count())
                .toList();
//        System.out.println(dayStarts);
        if (senior) {
            if (dayStarts.stream().filter(t -> t == 0).count() >= 3) {
                result = 3;
            } else {
                long q = dayStarts.stream().filter(t -> t == 0).count()
                        + dayStarts.stream().filter(t -> t == 1).count();
                if (q >= 3) {
                    result = 2;
                }
            }
        } else {
            if (dayStarts.stream().filter(t -> t >= 4).count() >= 3) {
                result = 3;
            } else {
                long q = dayStarts.stream().filter(t -> t >= 4).count()
                        + dayStarts.stream().filter(t -> t == 3).count();
                if (q >= 3) {
                    result = 2;
                }
            }
        }
        log.info("evaluate by lessons start time and return {}", result);
        params.put("Evaluation by lessons start time",result);
        return params;
    }

    @Override
    public Map<String, Integer> evaluateLessonEndTime(String[][] table, boolean senior, Map<String, Integer> params) {
        int result = 0;
        var dayEnds = Arrays.stream(table)
                .filter(dayTable -> !dayIsFree(dayTable))
                .map(dayTable -> {
                    for (int i = dayTable.length - 1; i >= 0; i--) {
                        if (isBlank(dayTable[i])) {
                            return i;
                        }
                    } return -1; }).toList();
//        System.out.println(dayEnds);
        if (!senior) {
            if (dayEnds.stream().filter(index -> index >= 5).count() >= 3) {
                result = 3;
            } else {
                long countFiveOrLater = dayEnds.stream().filter(index -> index >= 5).count()
                        + dayEnds.stream().filter(index -> index == 4).count();
                if (countFiveOrLater >= 3) {
                    result = 2;
                }
            }
        } else {
            if (dayEnds.stream().filter(index -> index <= 2).count() >= 3) {
                result = 3;
            } else {
                long countFiveOrEarlier = dayEnds.stream().filter(index -> index <= 2).count()
                        + dayEnds.stream().filter(index -> index == 3).count()
                        + dayEnds.stream().filter(index -> index == 4).count();
                if (countFiveOrEarlier >= 3) {
                    result = 2;
                }
            }
        }
        log.info("evaluate by lessons end time and return {}", result);
        params.put("Evaluation by lessons end time", result);
        return params;
    }

    @Override
    public Map<String, Integer> evaluateWeekendDistribution(String[][] table, Map<String, Integer> params) {
        int result = 1;
        if (dayIsFree(table[5]) || dayIsFree(table[6])
                && dayIsFree(table[12]) || dayIsFree(table[7])) result = 3;
        for (int i = 1; i <= table.length - 2; i++) {
            if (dayIsFree(table[i])) {
                if (dayIsFree(table[i + 1])) result = 3;
            }
        }
        log.info("evaluate by weekend distribution and return {}", result);
        params.put("Evaluation by weekend distribution",result);
        return params;
    }


    //TODO: Дает слишком много баллов!
    @Override
    public Map<String, Integer> evaluateForHavingLongBreak(String[][] table, boolean senior, Map<String, Integer> params) {
        int result = 0;
        for (String[] dayTable : table) {
            if (dayIsFree(dayTable)) continue;

            if (!senior && !isBlank(dayTable[2]) && !isBlank(dayTable[3])) {
                result += 2;
                break;
            }
            boolean endsByThird = Arrays.stream(dayTable).limit(3).noneMatch(EvaluationService::isBlank);
            boolean startsFromFourth = Arrays.stream(dayTable).limit(3).allMatch(EvaluationService::isBlank)
                    && Arrays.stream(dayTable).skip(3).anyMatch(str -> !isBlank(str));
            if (endsByThird || startsFromFourth) {
                result += 3;
            }
        }

        log.info("evaluate by having long break and return {}", result);
        params.put("Evaluation by having long break",result);
        return params;
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
//        System.out.println(timeTableParser1.printTimeTable(t));
        eva.evaluateTimeTable(t, false);
        System.out.println();
        eva.evaluateTimeTable(t, true);

//        eva.evaluateLessonStartTime(t);
//        System.out.println(timeTableParser1.printTimeTable(t));
    }
}
