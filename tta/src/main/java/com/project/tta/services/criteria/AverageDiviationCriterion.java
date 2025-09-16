package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.project.tta.services.criteria.BasicCriteria.dayIsFree;

@Component
public class AverageDiviationCriterion implements EvaluationCriterion {
    private Map<Setting, Map<String, Integer>> penaltyRules = Map.of(
            Setting.BACHELOR, Map.of(
                    "<0.1", 0,
                    "<0.2", -1,
                    "<0.4", -2,
                    "<0.8", -3),
            Setting.BACHELOR_SENIOR, Map.of(
                    "<0.1", 0,
                    "<0.2", -1,
                    "<0.4", -2,
                    "<0.8", -3),
            Setting.MASTER, Map.of(
                    "<0.1", 0,
                    "<0.2", 0,
                    "<0.4", 0,
                    "<0.8", -1)
    );
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);

    @Override
    public String getName() {
        return "Среднее отклонение";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        log.info("Оценка расписания по равномерности нагрузки (коэффициент вариации) для уровня: {}", setting);

        // Получаем правила штрафов по уровню
        Map<String, Integer> rules = penaltyRules.getOrDefault(setting, Map.of());

        // Собираем количество пар только по учебным дням
        List<Integer> lessonCounts = Arrays.stream(timeTable)
                .filter(day -> !dayIsFree(day))
                .map(BasicCriteria::getLessonQuantity)
                .toList();
        System.out.println(lessonCounts);

        // Если менее 2 учебных дней — нельзя посчитать CV → считаем идеальным
        if (lessonCounts.size() < 2) {
            int penalty = rules.getOrDefault("<0.1", 0);
            log.info("Недостаточно учебных дней ({}), применён штраф как за CV < 0.1: {}", lessonCounts.size(), penalty);
            return penalty;
        }

        // Среднее значение
        double mean = lessonCounts.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
        System.out.println(mean);

        // Избегаем деления на ноль
        if (mean == 0.0) {
            int penalty = rules.getOrDefault("<0.1", 0);
            log.info("Средняя нагрузка = 0, применён штраф как за CV < 0.1: {}", penalty);
            return penalty;
        }

        // Стандартное отклонение
        double variance = lessonCounts.stream()
                .mapToDouble(i -> Math.pow(i - mean, 2))
                .sum() / (lessonCounts.size() - 1);
        double stdDev = Math.sqrt(variance);
        System.out.println(stdDev);

        // Коэффициент вариации
        double cv = stdDev / mean;

        // Определяем диапазон
        String range;
        if (cv < 0.1) {
            range = "<0.1";
        } else if (cv < 0.2) {
            range = "<0.2";
        } else if (cv < 0.4) {
            range = "<0.4";
        } else {
            range = "<0.8"; // все остальные случаи (cv >= 0.4)
        }

        int penalty = rules.getOrDefault(range, -3); // дефолтный штраф

        log.info("Коэффициент вариации (CV) = {} → диапазон '{}', штраф: {}",
                cv, range, penalty);

        return penalty;
    }
}
