package com.project.tta.evaluation;

import com.project.tta.models.Setting;
import com.project.tta.services.criteria.AverageDiviationCriterion;
import com.project.tta.services.parser.TimeTableParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class AverageDiviationTest {
    AverageDiviationCriterion averageDiviationCriterion = new AverageDiviationCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();

    @Test
    void test() throws IOException {
        System.out.println(averageDiviationCriterion.evaluate(timeTableParser.getTimeTable("189103"), Setting.BACHELOR));
        System.out.println(averageDiviationCriterion.evaluate(timeTableParser.getTimeTable("189112"), Setting.BACHELOR_SENIOR));
        System.out.println(averageDiviationCriterion.evaluate(timeTableParser.getTimeTable("190001"), Setting.MASTER));}
}
