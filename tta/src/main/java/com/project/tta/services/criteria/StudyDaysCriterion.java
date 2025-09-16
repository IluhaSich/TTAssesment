package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

import static com.project.tta.services.criteria.BasicCriteria.*;

@Component
public class StudyDaysCriterion implements EvaluationCriterion {
    private Map<Setting, Map<Integer, Integer>> penaltyRules = Map.of(
            Setting.BACHELOR, Map.of(
                    3, -2,
                    4, -1,
                    5, 0,
                    6, -2),
            Setting.BACHELOR_SENIOR, Map.of(
                    3, -2,
                    4, 0,
                    5, 0,
                    6, -2),
            Setting.MASTER, Map.of(
                    1, -1,
                    2, -1,
                    3, -1,
                    4, 0,
                    5, 0,
                    6, -1)
    );
    private static final Logger log = LoggerFactory.getLogger(StudyDaysCriterion.class);

    @Override
    public String getName() {
        return "Количество учебных дней в неделю";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        Map<Integer, Integer> rules = penaltyRules.getOrDefault(setting, Map.of());

        log.info("quantity of study days in week for => {}", setting);

        int result = 0;

        String[][] week1 = Arrays.copyOfRange(timeTable, 0, 6);
        String[][] week2 = Arrays.copyOfRange(timeTable, 6, 12);

        for (int weekNum = 1; weekNum <= 2; weekNum++) {
            String[][] week = (weekNum == 1) ? week1 : week2;

            log.info("Неделя {}: анализ дней", weekNum);
            for (int i = 0; i < week.length; i++) {
                String[] day = week[i];
                boolean free = dayIsFree(day);
                int nonEmptyCount = (int) Arrays.stream(day)
                        .filter(BasicCriteria::isBlank)
                        .count();
//                log.info("  День {}: занято пар = {}, свободен = {}",
//                        i + 1, nonEmptyCount, free);
            }

            int studyDaysInWeek = (int) Arrays.stream(week)
                    .filter(day -> !dayIsFree(day))
                    .count();

            int penalty = rules.getOrDefault(studyDaysInWeek, 0);
            result += penalty;

//            log.info("week {} => {} study days, result => {}", weekNum, studyDaysInWeek, penalty);
        }

        log.info("Final result => {}",result);
        return result;
    }
}
