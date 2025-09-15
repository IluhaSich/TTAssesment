package com.project.tta.evaluation;

import com.project.tta.models.Setting;
import com.project.tta.services.criteria.GapsCriterion;
import com.project.tta.services.parser.TimeTableParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class GapsTest {
    GapsCriterion gapsCriterion = new GapsCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();

    @Test
    void test() throws IOException {
        System.out.println(gapsCriterion.evaluate(timeTableParser.getTimeTable("189103").getTimeTable(), Setting.BACHELOR));
        System.out.println(gapsCriterion.evaluate(timeTableParser.getTimeTable("189112").getTimeTable(), Setting.BACHELOR_SENIOR));
        System.out.println(gapsCriterion.evaluate(timeTableParser.getTimeTable("190001").getTimeTable(), Setting.MASTER));

    }
}
