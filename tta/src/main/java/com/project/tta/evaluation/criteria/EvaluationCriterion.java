package com.project.tta.evaluation.criteria;

import com.project.tta.models.Setting;

public interface EvaluationCriterion {
    String getName();
    int evaluate(String[][] table, Setting setting);
}
