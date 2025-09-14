package com.project.tta.ttparser;

import com.project.tta.models.Setting;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.GroupParserService;
import com.project.tta.services.TimeTable;
import com.project.tta.services.TimeTableParser;
import com.project.tta.services.criteria.DailyLoadCriterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.project.tta.services.TimeTableParser.printTimeTable;
import static com.project.tta.services.criteria.BasicCriteria.getLessonQuantity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ParserTests {
    DailyLoadCriterion dailyLoadCriterion = new DailyLoadCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();
    GroupParserService groupParserService = new GroupParserService();

    @Test
    void shouldParseAllLessons() throws IOException {
        //Расписание могло поменять и может возникнуть ошибка поэтому закомменчу
//        assertEquals(16, getLessonQuantity(timeTableParser.getTimeTable("190001").getTimeTable()));
//        assertEquals(14, getLessonQuantity(timeTableParser.getTimeTable("193083").getTimeTable()));
//        assertEquals(36, getLessonQuantity(timeTableParser.getTimeTable("193560").getTimeTable()));
//        assertEquals(14, getLessonQuantity(timeTableParser.getTimeTable("193083").getTimeTable()));
    }

    @Test
    void test() throws IOException, ExecutionException, InterruptedException {
        groupParserService.getGroupsFromWeb();
    }


}
