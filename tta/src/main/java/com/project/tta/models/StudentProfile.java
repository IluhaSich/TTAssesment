package com.project.tta.models;

import java.util.Map;

public class StudentProfile {
    private Map<String, Map<Integer, Integer>> penaltyRules;
    // например: {"StartTimePreference" : {1: 0, 2: -1, 3: -2, 4: -5}}

    public int getStartTimePenalty(int pair) {
        return penaltyRules
                .getOrDefault("StartTimePreference", Map.of())
                .getOrDefault(pair, -10);
    }
}
