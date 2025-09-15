package com.project.tta.ttparser;

import com.project.tta.models.Group;
import com.project.tta.services.*;
import com.project.tta.services.parser.TimeTableParser;
import com.project.tta.services.parser.GroupParserService;
import com.project.tta.models.Setting;
import com.project.tta.services.criteria.DailyLoadCriterion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.project.tta.services.criteria.BasicCriteria.getLessonQuantity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ParserTests {
    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupParserService groupParserService;

    DailyLoadCriterion dailyLoadCriterion = new DailyLoadCriterion();
    TimeTableParser timeTableParser = new TimeTableParser();

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
