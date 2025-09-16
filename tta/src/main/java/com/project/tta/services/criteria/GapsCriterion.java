package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.project.tta.services.criteria.BasicCriteria.isBlank;

@Component
public class GapsCriterion implements EvaluationCriterion {
    private Map<Setting, Map<Integer, Integer>> penaltyRules = Map.of(
            Setting.BACHELOR, Map.of(
                    0, 0,
                    1, -5,
                    2, -7,
                    3, -10,
                    4, -10,
                    5, -10,
                    6, -10
            ),
            Setting.BACHELOR_SENIOR, Map.of(
                    0, 0,
                    1, -5,
                    2, -7,
                    3, -10,
                    4, -10,
                    5, -10,
                    6, -10
            ),
            Setting.MASTER, Map.of(
                    0, 0,
                    1, -5,
                    2, -7,
                    3, -10,
                    4, -10,
                    5, -10,
                    6, -10
            )
    );
    private static final Logger log = LoggerFactory.getLogger(GapsCriterion.class);
    @Override
    public String getName() {
        return "Окна в расписании";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        int totalScore = 0;

        // Фильтруем пустые дни
        String[][] filteredTable = Arrays.stream(timeTable)
                .filter(Predicate.not(BasicCriteria::dayIsFree))
                .toArray(String[][]::new);

        for (String[] dayTable : filteredTable) {
            int maxGap = 0;

            // Переводим день в булев массив (true = пара пустая)
            Boolean[] dayTableBool = Arrays.stream(dayTable)
                    .map(BasicCriteria::isBlank)
                    .toArray(Boolean[]::new);

            // Индексы пустых пар
            List<Integer> emptyIndexes = new ArrayList<>();
            for (int i = 0; i < dayTableBool.length; i++) {
                if (dayTableBool[i]) {
                    emptyIndexes.add(i);
                }
            }

            // Считаем промежутки (gap) между пустыми парами
            for (int i = 0; i < emptyIndexes.size() - 1; i++) {
                int gap = emptyIndexes.get(i + 1) - emptyIndexes.get(i) - 1;
                if (gap > maxGap) {
                    maxGap = gap;
                }
            }

            // Применяем штрафы в зависимости от Setting
            Map<Integer, Integer> rules = penaltyRules.getOrDefault(setting, Map.of());
            int penalty = rules.getOrDefault(maxGap, -10); // если больше 6, берём -10
            totalScore += penalty;
        }

        log.info("Оценка по окнам ({}): {}", setting, totalScore);
        return totalScore;
    }


    private int countGapsInDay(String[] day) {
        boolean inGap = false;
        int gaps = 0;
        boolean hasStarted = false;

        for (String slot : day) {
            boolean isOccupied = isBlank(slot);

            if (isOccupied) {
                hasStarted = true;
                if (inGap) {
                    gaps++;
                    inGap = false;
                }
            } else {
                if (hasStarted && !inGap) {
                    inGap = true;
                }
            }
        }
        return gaps;
    }
}
