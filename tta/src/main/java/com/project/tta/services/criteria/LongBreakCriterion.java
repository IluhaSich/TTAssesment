package com.project.tta.services.criteria;

import com.project.tta.models.Setting;
import com.project.tta.models.StudentProfile;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;

public class LongBreakCriterion implements EvaluationCriterion {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public int evaluate(String[][] timeTable, Setting setting) {
        return 0;
    }
}
