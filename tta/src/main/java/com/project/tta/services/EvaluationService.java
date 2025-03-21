package com.project.tta.services;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EvaluationService {
    private final TimeTableParser timeTableParser;

    public EvaluationService(TimeTableParser timeTableParser) {
        this.timeTableParser = timeTableParser;
    }

    public int evaluateTable(String[][] table) {
        if (table == null) {
            //TODO: throw new ...
            return -666;
        }
        int grade = 0;
        grade += evaluateByWindowsInDay(table);
        grade += evaluateByWindowsWeek(table);

        return grade;
    }

    public int evaluateByWindowsInDay(String[][] table) {
        // Оценивает количество пар в день и возвращает оценку
        // TODO: Вообще должен определять окна в расписании а не количество пар в день
        int grade = 0;
        int lessonQuantity = 0;
        for (String[] tableDay : table) {
            lessonQuantity = lessonQuantityPerDay(tableDay);
        }
        switch (lessonQuantity) {
            case 8: {
                grade -= 15; //8 пар = ад
                break;
            }
            case 7: { // 7 пар
                grade -= 10;
                break;
            }
            case 6, 5: { // 6,5 пар
                grade -= 7;
                break;
            }
            case 4, 3: { // 4, 3 пары
                grade += 2;
                break;
            }
            case 2: { // 2 пары
                grade -= 1;
                break;
            }
            case 1: { // 1 пара
                grade -= 3;
            }
        }

        return grade;
    }

    public int evaluateByWindowsWeek(String[][] table) {
        // Оценивает количество свободных дней и возвращает оценку
        boolean[] weekLiberty = new boolean[12];
        int grade = 0;
        for (int i = 0; i < table[0].length; i++) {
            weekLiberty[i] = dayIsFree(table[i]);
        }
        int libertyQuantity = 0;
        for (boolean dayLiberty : weekLiberty) { // Определяет количество свободных дней
            if (dayLiberty) libertyQuantity++;
        }
        switch (libertyQuantity) {
            case 11: { // 2 субботы
                grade -= 10;
                break;
            }
            case 10: { // 1 суббота
                grade -= 5;
                break;
            }
            case 8, 7: { // 4 дня учебы
                grade += 3;
                break;
            }
            case 6, 4, 3, 2, 1: { // до 6 пар в 2ух неделях
                grade += 10;
                break;
            }
        }

        return grade;
    }

    public boolean dayIsFree(String[] dayTable) {
        //Возвращает true если день без пар
        boolean free = true;
        for (String lesson : dayTable) {
            if (lesson != null  && !lesson.equals("nothing")) {
                free = false;
                break;
            }
        }
        return free;
    }

    public int lessonQuantityPerDay(String[] dayTable) {
        //Возвращает true если день без пар
        int lQuantity = 0;
        for (String lesson : dayTable) {
            if (lesson != null && !lesson.equals("nothing")) lQuantity += 1;
        }
        return lQuantity;
    }
}
