package com.project.tta.evaluation;

import com.project.tta.models.Setting;
import com.project.tta.services.TimeTableParser;
import com.project.tta.services.criteria.StudyDaysCriterion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class StudyDaysTest {
    StudyDaysCriterion studyDaysCriterion = new StudyDaysCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();

    @Test
    void test() throws IOException {
//        System.out.println(studyDaysCriterion.evaluate(timeTableParser.getTimeTable("189103").getTimeTable(), Setting.BACHELOR));
//        System.out.println(studyDaysCriterion.evaluate(timeTableParser.getTimeTable("189112").getTimeTable(), Setting.BACHELOR_SENIOR));
//        System.out.println(studyDaysCriterion.evaluate(timeTableParser.getTimeTable("190001").getTimeTable(), Setting.BACHELOR));
        System.out.println(studyDaysCriterion.evaluate(timeTableParser.getTimeTable("193630").getTimeTable(), Setting.BACHELOR_SENIOR));
    }
}

