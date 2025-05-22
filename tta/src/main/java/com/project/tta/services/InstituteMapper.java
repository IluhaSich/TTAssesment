package com.project.tta.services;

import java.util.HashMap;
import java.util.Map;

public class InstituteMapper {
    private static final Map<Character, String> instituteMapping = new HashMap<>();

    static {
        instituteMapping.put('У', "ИУЦТ");
        instituteMapping.put('С', "ИПСС");
        instituteMapping.put('В', "АВТ");
        instituteMapping.put('А', "АГА");
        instituteMapping.put('Д', "АДХ");
        instituteMapping.put('Ш', "ВИШ");
        instituteMapping.put('О', "ИМТК");
        instituteMapping.put('Т', "ИТТСУ");
        instituteMapping.put('Э', "ИЭФ");
        instituteMapping.put('П', "Академия ВСМ");
        instituteMapping.put('Ю', "ЮИ");
    }

    public static char getInstituteInitial(String instituteName) {
        for (Map.Entry<Character, String> entry : instituteMapping.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(instituteName)) {
                return entry.getKey();
            }
        }
        return '\u0000';
    }

    public static String getInstituteByGroupName(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return "";
        }
        char firstChar = groupName.charAt(0);
        return instituteMapping.getOrDefault(firstChar, "");
    }
}
