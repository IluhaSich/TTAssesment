package com.project.tta.services;

import com.project.tta.models.CriterionEvaluation;
import com.project.tta.models.Group;
import com.project.tta.models.TTEvaluation;
import com.project.tta.repositories.CriterionEvaluationRepository;
import com.project.tta.repositories.GroupRepository;
import com.project.tta.repositories.TTEvaluationRepository;
import com.project.tta.services.interfaces.EvaluationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

@Service
@Deprecated
public class EvaluationService implements EvaluationInterface {
    private final CriterionEvaluationRepository criterionEvaluationRepository;
    private final GroupRepository groupRepository;
    private final TTEvaluationRepository ttEvaluationRepository;

    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    private final TimeTableParser timeTableParser;

    public EvaluationService(CriterionEvaluationRepository criterionEvaluationRepository, GroupRepository groupRepository, TTEvaluationRepository ttEvaluationRepository, TimeTableParser timeTableParser) {
        this.criterionEvaluationRepository = criterionEvaluationRepository;
        this.groupRepository = groupRepository;
        this.ttEvaluationRepository = ttEvaluationRepository;
        this.timeTableParser = timeTableParser;
    }

    @Override
    @Deprecated
    public Group evaluateTimeTable(TimeTable timeTable) {
        //* @param senior false — младший курс, true — старший курс
        if (timeTable == null) return null;
//        if (timeTable == null) throw new RuntimeException("timeTable == null");

        boolean senior = timeTable.getCourse() > 2;
        var table = timeTable.getTimeTable();
        if (table == null) {
            log.error("Time table in evaluateTimeTable method input is null. Throw RuntimeException");
            throw new RuntimeException("time table is null");
        }
        var map = new HashMap<String, Integer>(9);
        evaluateGaps(table, map);
        evaluateStudyDays(table, map);
        evaluateLoadBalance(table, map);
        evaluateDailyLoad(table, map);
        evaluateLessonStartTime(table, senior, map);
        evaluateLessonEndTime(table, senior, map);
        evaluateWeekendDistribution(table, map);
        evaluateForHavingLongBreak(table, senior, map);

        var keys = map.keySet().toArray();
        var values = map.values().toArray();
        int sum = Arrays.stream(map.values().toArray(new Integer[0])).mapToInt(i -> i).sum();

        var group = new Group(
                timeTable.getName(),
                null,
                timeTable.getLink(),
                timeTable.getCourse(),
                null);
        var ttEvalluation = new TTEvaluation(
                group,
                sum,
                LocalDateTime.now(),
                null);
        group.setTTEvaluation(ttEvalluation);

        List<CriterionEvaluation> criterionEvaluation = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            criterionEvaluation.add(
                    new CriterionEvaluation(
                            ttEvalluation,
                            keys[i].toString(),
                            (int) values[i]));
        }
        ttEvalluation.setCriterionEvaluationList(criterionEvaluation);
        log.info("Sum of grades in {} is {}", timeTable.getName(), sum);
        groupRepository.save(group);
        ttEvaluationRepository.save(ttEvalluation);
        criterionEvaluation.stream().map(criterionEvaluationRepository::save);
        log.info("Group - {}, was fully save in db with all grades", timeTable.getName());
        return group;
    }

    @Override
    @Deprecated
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
        params.put("Оценка по наличию окон", result);
        return params;
    }

    @Override
    @Deprecated
    public Map<String, Integer> evaluateStudyDays(String[][] table, Map<String, Integer> params) {
        int studyDays = table.length - getFreeDaysQuantity(table);
        int result = switch (studyDays) {
            case 10 -> 3;
            case 9,8 -> 5;
            default -> 0;
        };
        log.info("evaluate by study days = {} and return {}", studyDays, result);
        params.put("Оценка по количеству учебных дней", result);
        return params;
    }

    @Override
    @Deprecated
    public Map<String, Integer> evaluateLoadBalance(String[][] table, Map<String, Integer> params) {
        var quantityArr = Arrays.stream(table).map(EvaluationService::getLessonQuantity).toList();
        int length = quantityArr.size();
        double u = quantityArr.stream().mapToDouble(i -> i).sum() / length; // Среднее значение
        double o = Math.sqrt(
                quantityArr.stream().mapToDouble(i -> Math.pow(i - u, 2)).sum()
                        / (length - 1)); // Стандартное отклонение
        double cv = o / u; // Коэффициент вариации
        int result = 0;
        if (cv < 0.1) result = 5;
        if (cv < 0.2) result = 3;
        if (cv < 0.4) result = 2;
        if (cv < 0.8) result = 1;
        log.info("evaluate by load balance with CV = {} and return {}",
                Math.floor(cv * 100) / 100, result);
        params.put("Оценка по равномерности нагрузки", result);
        return params;
    }

    @Override
    @Deprecated
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
        params.put("Оценка по общему числу пар", result);
        return params;
    }

    @Override
    @Deprecated
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
        params.put("Оценка по времени начала занятий", result);
        return params;
    }

    @Override
    @Deprecated
    public Map<String, Integer> evaluateLessonEndTime(String[][] table, boolean senior, Map<String, Integer> params) {
        int result = 0;
        var dayEnds = Arrays.stream(table)
                .filter(dayTable -> !dayIsFree(dayTable))
                .map(dayTable -> {
                    for (int i = dayTable.length - 1; i >= 0; i--) {
                        if (isBlank(dayTable[i])) {
                            return i;
                        }
                    }
                    return -1;
                }).toList();
//        System.out.println(dayEnds);
        if (senior) {
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
        params.put("Оценка по времени окончания занятий", result);
        return params;
    }

    @Override
    @Deprecated
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
        params.put("Оценка по распределению выходных", result);
        return params;
    }


    @Override
    @Deprecated
    public Map<String, Integer> evaluateForHavingLongBreak(String[][] table, boolean senior, Map<String, Integer> params) {
        int result = 0;
        int count = 0;
        for (String[] dayTable : table) {
            if (dayIsFree(dayTable)) continue;

            if (!senior && !isBlank(dayTable[2]) && !isBlank(dayTable[3])) {
                result = 2;
                break;
            }
            boolean endsByThird = Arrays.stream(dayTable).limit(3).noneMatch(EvaluationService::isBlank);
            boolean startsFromFourth = Arrays.stream(dayTable).limit(3).allMatch(EvaluationService::isBlank)
                    && Arrays.stream(dayTable).skip(3).anyMatch(str -> !isBlank(str));
            if (endsByThird || startsFromFourth) {
                count += 1;
            }
        }
        if (count >= 3) {
            result = 3;
        }
        log.info("evaluate by having long break and return {}", result);
        params.put("Оценка по наличию большого перерыва",result);
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

//    public static void main(String[] args) throws IOException {
//        TimeTableParser timeTableParser1 = new TimeTableParser();
//        EvaluationService eva = new EvaluationService(timeTableParser1);
//        var t = timeTableParser1.getTimeTable("/timetable/189115");
////        System.out.println(timeTableParser1.printTimeTable(t));
//        eva.evaluateTimeTable(t);
//        System.out.println();
//        eva.evaluateTimeTable(t);
//
////        eva.evaluateLessonStartTime(t);
////        System.out.println(timeTableParser1.printTimeTable(t));
//    }
}
