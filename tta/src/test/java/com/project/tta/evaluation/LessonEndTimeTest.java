package com.project.tta.evaluation;

import com.project.tta.models.Setting;
import com.project.tta.services.parser.TimeTableParser;
import com.project.tta.services.criteria.LessonEndTimeCriterion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class LessonEndTimeTest {
    LessonEndTimeCriterion lessonEndTimeCriterion = new LessonEndTimeCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();

    @Test
    void test() throws IOException {
        System.out.println(lessonEndTimeCriterion.evaluate(timeTableParser.getTimeTable("189103").getTimeTable(), Setting.BACHELOR));

    }
}
