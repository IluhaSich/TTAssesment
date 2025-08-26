package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.models.StudentProfile;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.project.tta.services.criteria.BasicCriteria.getFreeDaysQuantity;

public class StudyDaysCriterion implements EvaluationCriterion {

    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);

    @Override
    public String getName() {
        return "quantity of study days";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        int studyDays = timeTable.length - getFreeDaysQuantity(timeTable);
        int result = switch (studyDays) {
            case 10 -> -3;
            case 9,8 -> -5;
            default -> 0;
        };
        log.info("evaluate by study days = {} and return {}", studyDays, result);
        return result;
    }
}
