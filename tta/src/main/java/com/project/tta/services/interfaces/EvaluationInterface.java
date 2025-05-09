package com.project.tta.services.interfaces;

/**
 * Интерфейс оценки расписания по различным критериям.
 */
import com.project.tta.models.Group;
import com.project.tta.services.TimeTable;

import java.util.Map;

public interface EvaluationInterface {
    /**
     * Оценивает расписание по всем критериям.<br><br>
     * В зависимости от курса (младший/старший) применяются разные критерии оценки.<br>
     *
     * @param timeTable массив с расписанием
     * @return Объeкт класс Group
     */
    Group evaluateTimeTable(TimeTable timeTable);

    /**
     * Оценивает расписание по наличию окон.<br><br>
     * Если в дне нет окон — +3 балла.<br>
     * Если окна есть, но не более одного — +1 балла.<br>
     * Более одного окна в дне не влияет на оценку.<br>
     *
     * @param table массив с расписанием
     * @param params дополнительные параметры для оценки
     * @return Карта с названием критерия и начисленными баллами
     */
    Map<String, Integer> evaluateGaps(String[][] table, Map<String, Integer> params);

    /**
     * Оценивает расписание по количеству учебных дней в неделю.<br><br>
     * Оптимально — 4 учебных дня → +5 баллов.<br>
     * Стандартно — 5 учебных дней → +3 балла.<br>
     *
     * @param table массив с расписанием
     * @param params дополнительные параметры для оценки
     * @return Карта с названием критерия и начисленными баллами
     */
    Map<String, Integer> evaluateStudyDays(String[][] table, Map<String, Integer> params);

    /**
     * Оценивает расписание по равномерности нагрузки по дням недели.<br><br>
     * CV < 0.1 → расписание равномерное → +5 баллов.<br>
     * 0.1 ≤ CV < 0.2 → небольшие отклонения → +3 балла.<br>
     * 0.2 ≤ CV < 0.4 → допустимые колебания → +2 балла.<br>
     * CV < 0.8 → менее равномерное расписание → +1 балл.<br>
     *
     * @param table массив с расписанием
     * @param params дополнительные параметры для оценки
     * @return Карта с названием критерия и начисленными баллами
     */
    Map<String, Integer> evaluateLoadBalance(String[][] table,  Map<String, Integer> params);

    /**
     * Оценивает расписание по общему числу пар за две недели.<br><br>
     * 20–30 пар → оптимальный объем → +5 баллов.<br>
     * < 20 пар → умеренная нагрузка → +3 балла.<br>
     * > 30 пар → интенсивная учебная нагрузка → +2 балла.<br>
     *
     * @param table массив с расписанием
     * @param params дополнительные параметры для оценки
     * @return Карта с названием критерия и начисленными баллами
     */
    Map<String, Integer> evaluateDailyLoad(String[][] table, Map<String, Integer> params);

    /**
     * Оценивает расписание по времени начала занятий.<br><br>
     * Младший курс: пары начинаются(3 и более раз в неделю) с первой — +3 балла, со второй-третьей — +2 балла.<br>
     * Старший курс: пары начинаются(3 и более раз в неделю) с четвертой или позже — +3 балла, со третьей — +2 балла.<br>
     *
     * @param table массив с расписанием
     * @param senior false — младший курс, true — старший курс
     * @param params дополнительные параметры для оценки
     * @return Карта с названием критерия и начисленными баллами
     */
    Map<String, Integer> evaluateLessonStartTime(String[][] table, boolean senior, Map<String, Integer> params);

    /**
     * Оценивает расписание по времени окончания занятий.<br><br>
     * Младший курс: пары заканчиваются до 13:10 — +3 балла, до 17:00 — +2 балла.<br>
     * Старший курс: пары заканчиваются после 18:30 — +3 балла, после 16:00 — +2 балла.<br>
     *
     * @param table массив с расписанием
     * @param senior false — младший курс, true — старший курс
     * @param params дополнительные параметры для оценки
     * @return Карта с названием критерия и начисленными баллами
     */
    Map<String, Integer> evaluateLessonEndTime(String[][] table, boolean senior, Map<String, Integer> params);

    /**
     * Оценивает расписание по распределению выходных.<br><br>
     * Если выходные идут подряд (например, суббота и воскресенье) — +3 балла.<br>
     * Если выходные разбросаны (например, среда и воскресенье) — +1 балла.<br>
     *
     * @param table массив с расписанием
     * @param params дополнительные параметры для оценки
     * @return Карта с названием критерия и начисленными баллами
     */
    Map<String, Integer> evaluateWeekendDistribution(String[][] table, Map<String, Integer> params);

    /**
     * Оценивает расписание по наличию большого перерыва между парами.<br><br>
     * Если все пары подряд без длинного перерыва — +3 балла.<br>
     * Если есть большой перерыв — +2 балла (Только для младших курсов).<br>
     *
     * @param table массив с расписанием
     * @param senior false — младший курс, true — старший курс
     * @param params дополнительные параметры для оценки
     * @return Карта с названием критерия и начисленными баллами
     */
    Map<String, Integer> evaluateForHavingLongBreak(String[][] table, boolean senior, Map<String, Integer> params);
}