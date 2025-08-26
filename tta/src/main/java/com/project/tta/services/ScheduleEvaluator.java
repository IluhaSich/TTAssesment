package com.project.tta.services;

import com.project.tta.models.EvaluationResult;
import com.project.tta.models.Setting;
import com.project.tta.models.StudentProfile;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleEvaluator {
    private final List<EvaluationCriterion> criteria;

    public ScheduleEvaluator(List<EvaluationCriterion> criteria) {
        this.criteria = criteria;
    }

    public EvaluationResult evaluate(TimeTable timeTable, Setting setting, List<String> chosenCriteria) {
        int baseScore = 100;
        Map<String, Integer> criterionScores = new HashMap<>();

        for (EvaluationCriterion criterion : criteria) {
            if (chosenCriteria.contains(criterion.getName())) {
                int penalty = criterion.evaluate(timeTable.getTimeTable(), setting);
                criterionScores.put(criterion.getName(), penalty);
                baseScore += penalty;
            }
        }
        return new EvaluationResult(baseScore, criterionScores);
    }
}
