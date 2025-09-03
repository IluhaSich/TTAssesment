package com.project.tta.ttparser;

import com.project.tta.services.parser.TimeTableParser;
import com.project.tta.services.criteria.DailyLoadCriterion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.project.tta.services.criteria.BasicCriteria.getLessonQuantity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ParserTests {
    DailyLoadCriterion dailyLoadCriterion = new DailyLoadCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();

    @Test
    void shouldParseAllLessons() throws IOException {
         assertEquals(16,getLessonQuantity(timeTableParser.getTimeTable("190001").getTimeTable()));
         assertEquals(14,getLessonQuantity(timeTableParser.getTimeTable("193083").getTimeTable()));
         assertEquals(37,getLessonQuantity(timeTableParser.getTimeTable("193560").getTimeTable()));
         assertEquals(14, getLessonQuantity(timeTableParser.getTimeTable("193083").getTimeTable()));
    }

    @Test
    void test() throws IOException, ExecutionException, InterruptedException {
//       timeTableParser.getLinks();

    }
}
