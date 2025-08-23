package com.project.tta.services.criteria;

import com.project.tta.models.StudentProfile;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.interfaces.EvaluationCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.project.tta.services.criteria.BasicCriteria.getLessonQuantity;

public class DailyLoadCriterion implements EvaluationCriterion {

    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);

    @Override
    public String getName() {
        return "Daily Load";
    }

    @Override
    public int evaluate(String[][] timeTable, StudentProfile profile) {
        int totalLessons = getLessonQuantity(timeTable);

        int result;
        if (totalLessons >= 20 && totalLessons <= 30) {
            result = -5;
        } else if (totalLessons < 20) {
            result = -3;
        } else {
            result = -2;
        }
        log.info("evaluate by daily load and return {}", result);
        return result;
    }
}
