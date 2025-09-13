package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

import static com.project.tta.services.criteria.BasicCriteria.dayIsFree;
import static com.project.tta.services.criteria.BasicCriteria.isBlank;

@Component
public class GapsCriterion implements EvaluationCriterion {
    private Map<Setting, Map<Integer, Integer>> penaltyRules = Map.of(
            Setting.BACHELOR, Map.of(
                    0, 0,
                    1, -1,
                    2, -3,
                    3, -5),
            Setting.BACHELOR_SENIOR, Map.of(
                    0, 0,
                    1, 0,
                    2, -2,
                    3, -4),
            Setting.MASTER, Map.of(
                    0, 0,
                    1, -2,
                    2, -4,
                    3, -6)
    );
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    @Override
    public String getName() {
        return "Timetable gaps";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        Map<Integer, Integer> rules = penaltyRules.getOrDefault(setting, Map.of());
        int totalPenalty = 0;

        String[][] week1 = Arrays.copyOfRange(timeTable, 0, 5);
        String[][] week2 = Arrays.copyOfRange(timeTable, 5, 10);

        for (int weekNum = 1; weekNum <= 2; weekNum++) {
            String[][] week = (weekNum == 1) ? week1 : week2;
            int weeklyGaps = 0;

            for (String[] day : week) {
                if (dayIsFree(day)) continue;
                weeklyGaps += countGapsInDay(day);
            }

            int penalty = rules.getOrDefault(weeklyGaps, 0);
            totalPenalty += penalty;

            log.info("Неделя {}: {} внутридневных окон, штраф: {}", weekNum, weeklyGaps, penalty);
        }

        log.info("Итоговый штраф по критерию 'внутридневные окна': {}", totalPenalty);
        return totalPenalty;
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
