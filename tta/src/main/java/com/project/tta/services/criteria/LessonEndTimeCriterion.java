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
public class LessonEndTimeCriterion implements EvaluationCriterion {
    private Map<Setting, Map<Integer, Integer>> penaltyRules = Map.of(
            Setting.BACHELOR, Map.of(
                    1, 0,
                    2, 0,
                    3, 0,
                    4, 0,
                    5, -1,
                    6, -1,
                    7, -2,
                    8, -3
            ),
            Setting.BACHELOR_SENIOR, Map.of(
                    1, -2,
                    2, -1,
                    3, -1,
                    4, -1,
                    5, 0,
                    6, 0,
                    7, 0,
                    8, 0
            ),
            Setting.MASTER, Map.of(
                    1, -3,
                    2, -2,
                    3, -1,
                    4, -1,
                    5, 0,
                    6, 0,
                    7, 0,
                    8, 0
            )
    );
    private static final Logger log = LoggerFactory.getLogger(LessonEndTimeCriterion.class);
    @Override
    public String getName() {
        return "Время окончания пар";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        Map<Integer, Integer> rules = penaltyRules.getOrDefault(setting, Map.of());
//        System.out.println(Arrays.deepToString(timeTable));
        AtomicInteger totalPenalty = new AtomicInteger(0);
        int result = Arrays.stream(timeTable)
                .filter(dayTable -> !dayIsFree(dayTable))
                .mapToInt(dayTable -> {
                    int lastLectureIndex = -1;
                    for (int i = dayTable.length - 1; i >= 0; i--) {
                        if (isBlank(dayTable[i])) {
                            lastLectureIndex = i;
                            break;
                        }
                    }

                    if (lastLectureIndex == -1) {
                        log.warn("День прошёл фильтр !dayIsFree, но оказался пустым — ошибка в логике");
                        return 0;
                    }

                    int period = lastLectureIndex + 1;
                    int penalty = rules.getOrDefault(period, 0);
                    totalPenalty.addAndGet(penalty);

//                    log.info("Day ends after => {}, result => {}", period, penalty);

                    return penalty;
                })
                .sum();

        log.info("Final result => {}", result);
        return result;
    }
}
