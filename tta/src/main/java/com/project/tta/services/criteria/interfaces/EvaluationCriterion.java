package com.project.tta.services.criteria.interfaces;

import com.project.tta.models.Setting;

public interface EvaluationCriterion {
    String getName();
    int evaluate(String[][] timeTable, Setting setting);
}
