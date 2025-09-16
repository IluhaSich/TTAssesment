package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.project.tta.services.criteria.BasicCriteria.dayIsFree;
import static com.project.tta.services.criteria.BasicCriteria.isBlank;

@Component
public class LessonStartTimeCriterion implements EvaluationCriterion {
    private Map<Setting, Map<Integer, Integer>> penaltyRules = Map.of(
            Setting.BACHELOR, Map.of(
                    1, 0,
                    2, -1,
                    3, -2,
                    4, -3),
            Setting.BACHELOR_SENIOR, Map.of(
                    1, -2,
                    2, -1,
                    3, 0,
                    4, -1),
            Setting.MASTER, Map.of(
                    1, -5,
                    2, -2,
                    3, -1,
                    4, 0)
    );
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    @Override
    public String getName() {
        return "Время начала пар";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        Map<Integer, Integer> rules = penaltyRules.getOrDefault(setting, Map.of());
        AtomicInteger totalPenalty = new AtomicInteger(0);
        int result = Arrays.stream(timeTable)
                .filter(dayTable -> !dayIsFree(dayTable))
                .mapToInt(dayTable -> {
                    int firstLectureIndex = -1;
                    for (int i = 0; i < dayTable.length; i++) {
                        if (isBlank(dayTable[i])) {
                            firstLectureIndex = i;
                            break;
                        }
                    }

                    if (firstLectureIndex == -1) {
                        log.warn("День прошёл фильтр !dayIsFree, но оказался пустым — ошибка в логике");
                        return 0;
                    }

                    int period = firstLectureIndex + 1;
                    int penalty = rules.getOrDefault(period, 0);
                    totalPenalty.addAndGet(penalty);

//                    log.info("Day starts with => {}, result => {}", period, penalty);

                    return penalty;
                })
                .sum();

        log.info("Final result => {}", result);
        return result;
    }
}
