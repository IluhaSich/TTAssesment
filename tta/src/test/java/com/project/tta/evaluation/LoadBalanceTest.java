package com.project.tta.evaluation;

import com.project.tta.models.Setting;
import com.project.tta.services.parser.TimeTableParser;
import com.project.tta.services.criteria.LoadBalanceCriterion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class LoadBalanceTest {
    LoadBalanceCriterion loadBalanceCriterion = new LoadBalanceCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();

    @Test
    void test() throws IOException {
//        System.out.println(loadBalanceCriterion.evaluate(timeTableParser.getTimeTable("189103").getTimeTable(), Setting.BACHELOR));
//        System.out.println(loadBalanceCriterion.evaluate(timeTableParser.getTimeTable("189112").getTimeTable(), Setting.BACHELOR_SENIOR));
//        System.out.println(loadBalanceCriterion.evaluate(timeTableParser.getTimeTable("190001").getTimeTable(), Setting.MASTER));
        System.out.println(loadBalanceCriterion.evaluate(timeTableParser.getTimeTable("193630"), Setting.BACHELOR_SENIOR));

    }
}
