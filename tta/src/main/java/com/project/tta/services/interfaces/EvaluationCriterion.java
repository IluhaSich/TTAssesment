package com.project.tta.services.interfaces;

import com.project.tta.models.StudentProfile;
import com.project.tta.services.TimeTable;

import java.util.Map;

public interface EvaluationCriterion {
    String getName();
    int evaluate(String[][] timeTable, StudentProfile profile);
}
