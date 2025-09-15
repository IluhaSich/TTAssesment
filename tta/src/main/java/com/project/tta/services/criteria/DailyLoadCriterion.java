package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.project.tta.services.criteria.BasicCriteria.getLessonQuantity;
/**
 * Класс для оценки по степени загрузки студента.
 */
@Component
public class DailyLoadCriterion implements EvaluationCriterion {
    private final Map<Setting, Map<String, Integer>> penaltyRules = Map.of(
    Setting.BACHELOR, Map.of(
                    "<28", -2,
                    "28-40", 0,
                    ">40", -3),
    Setting.BACHELOR_SENIOR, Map.of(
                    "<24", -2,
                    "24-36", 0,
                    ">36", -3),
    Setting.MASTER, Map.of(
                    "<12", -1,
                    "12-20", 0,
                    ">20", -3)
    );

    private static final Logger log = LoggerFactory.getLogger(DailyLoadCriterion.class);
    @Override
    public String getName() {
        return "Daily Load";
    }
    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        int totalLessons = getLessonQuantity(timeTable);
        Map<String, Integer> rules = penaltyRules.get(setting);

        for (Map.Entry<String, Integer> entry : rules.entrySet()) {
            String range = entry.getKey().trim(); // убираем пробелы
            if (matchesRange(totalLessons, range)) {
                int result = entry.getValue();
                System.out.println(totalLessons);
                log.info("Matched range {} => result {}", range, result);
                return result;
            }
        }
        return 0;
    }

    private boolean matchesRange(int value, String range) {
        range = range.trim();
        if (range.startsWith("<")) {
            int upper = Integer.parseInt(range.substring(1));
            return value < upper;
        } else if (range.startsWith(">")) {
            int lower = Integer.parseInt(range.substring(1));
            return value > lower;
        } else if (range.contains("-")) {
            String[] parts = range.split("-");
            int lower = Integer.parseInt(parts[0].trim());
            int upper = Integer.parseInt(parts[1].trim());
            return value >= lower && value <= upper;
        }
        return false;
    }
}
