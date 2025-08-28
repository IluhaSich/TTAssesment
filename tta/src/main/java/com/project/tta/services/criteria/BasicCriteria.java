package com.project.tta.services.criteria;

import java.util.Arrays;

public class BasicCriteria {
    /**
     * @param dayTable расписание на день
     * @return Возвращает количество пар в определенный день
     */
    public static int getLessonQuantity(String[] dayTable) {
        return (int) Arrays.stream(dayTable).filter(BasicCriteria::isBlank).count();
    }

    /**
     * @param timeTable полное расписание (на две недели)
     * @return Возвращает количество всех пар
     */
    public static int getLessonQuantity(String[][] timeTable) {
        return Arrays.stream(timeTable).mapToInt(BasicCriteria::getLessonQuantity).sum();
    }

    /**
     * @param dayTable расписание на день
     * @return Возвращает true если в этот день нет пар
     */
    public static boolean dayIsFree(String[] dayTable) {
        if (dayTable == null) {
            return true;
        }
        return Arrays.stream(dayTable).noneMatch(BasicCriteria::isBlank);
    }

    /**
     * @param timeTable полное расписание (на две недели)
     * @return количество свободных дней
     */
    public static int getFreeDaysQuantity(String[][] timeTable) {
        return (int) Arrays.stream(timeTable).filter(BasicCriteria::dayIsFree).count();
    }

    /**
     * Делает проверку: str != null && !str.isEmpty();
     *
     * @param str строка с информацией о паре
     * @return true если строка не пуста
     */

    public static boolean isBlank(String str) {
        return str != null && !str.isEmpty();
    }
}
