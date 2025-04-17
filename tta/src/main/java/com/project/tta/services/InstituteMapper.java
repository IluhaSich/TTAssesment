package com.project.tta.services;

import java.util.HashMap;
import java.util.Map;

public class InstituteMapper {
    private static final Map<Character, String> instituteMapping = new HashMap<>();

    static {
        instituteMapping.put('У', "ИУЦТ");
        instituteMapping.put('С', "ИПСС");
    }

    public static String getInstituteByGroupName(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return "";
        }
        char firstChar = groupName.charAt(0);
        return instituteMapping.getOrDefault(firstChar, "");
    }
}
