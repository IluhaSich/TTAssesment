package com.project.tta.models;

import java.util.Map;

public class EvaluationResult {
    private int finalScore;
    private Map<String, Integer> details;

    public EvaluationResult(int finalScore, Map<String, Integer> details) {
        this.finalScore = finalScore;
        this.details = details;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public Map<String, Integer> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Integer> details) {
        this.details = details;
    }
}
