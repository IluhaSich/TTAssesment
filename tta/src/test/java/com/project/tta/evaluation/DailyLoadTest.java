package com.project.tta.evaluation;

import com.project.tta.models.Setting;
import com.project.tta.services.TimeTableParser;
import com.project.tta.services.criteria.DailyLoadCriterion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DailyLoadTest {
    DailyLoadCriterion dailyLoadCriterion = new DailyLoadCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();

    @Test
    void test() throws IOException {
        System.out.println(dailyLoadCriterion.evaluate(timeTableParser.getTimeTable("189103").getTimeTable(), Setting.BACHELOR));
        System.out.println(dailyLoadCriterion.evaluate(timeTableParser.getTimeTable("189112").getTimeTable(), Setting.BACHELOR_SENIOR));
        System.out.println(dailyLoadCriterion.evaluate(timeTableParser.getTimeTable("190001").getTimeTable(), Setting.MASTER));
        assertEquals(0,dailyLoadCriterion.evaluate(timeTableParser.getTimeTable("189103").getTimeTable(), Setting.BACHELOR));//33 пар
        assertEquals(0,dailyLoadCriterion.evaluate(timeTableParser.getTimeTable("189112").getTimeTable(), Setting.BACHELOR_SENIOR));//30 пар
        assertEquals(-1,dailyLoadCriterion.evaluate(timeTableParser.getTimeTable("190001").getTimeTable(), Setting.MASTER));//16 пар

    }
}
