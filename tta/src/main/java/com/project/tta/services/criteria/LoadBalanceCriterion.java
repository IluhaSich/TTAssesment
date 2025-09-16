package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.project.tta.services.criteria.BasicCriteria.dayIsFree;
import static com.project.tta.services.criteria.BasicCriteria.getLessonQuantity;

@Component
public class LoadBalanceCriterion implements EvaluationCriterion {
    private Map<Setting, Map<Integer, Integer>> penaltyRules = Map.of(
            Setting.BACHELOR, Map.of(
                    1, -2,
                    2, -1,
                    3, 0,
                    4, 0,
                    5, 0,
                    6, -1,
                    7, -2,
                    8, -3),
            Setting.BACHELOR_SENIOR, Map.of(
                    1, -1,
                    2, 0,
                    3, 0,
                    4, 0,
                    5, -1,
                    6, -2,
                    7, -3,
                    8, -4),
            Setting.MASTER, Map.of(
                    1, 0,
                    2, 0,
                    3, -1,
                    4, -2,
                    5, -3,
                    6, -4,
                    7, -5,
                    8, -6)
    );
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    @Override
    public String getName() {
        return "Среднее количество пар";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        Map<Integer, Integer> rules = penaltyRules.getOrDefault(setting, Map.of());

        List<Integer> lessonCounts = new ArrayList<>();
        for (String[] day : timeTable) {
            if (!dayIsFree(day)) {
                lessonCounts.add(getLessonQuantity(day));
            }
        }

        if (lessonCounts.isEmpty()) {
            log.warn("Нет ни одного учебного дня за две недели");
            return 0;
        }

        double average = lessonCounts.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        int roundedAverage = (int) Math.round(average);

        int clampedValue = Math.max(1, Math.min(8, roundedAverage));

        int penalty = rules.getOrDefault(clampedValue, 0);

        log.info("Среднее количество пар в учебный день: {} → округлено до {}, штраф: {}",
                average, roundedAverage, penalty);

        return penalty;
    }
}
