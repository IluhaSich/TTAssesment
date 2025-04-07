package com.project.tta.controllers;

import com.project.tta.models.Group;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.TimeTable;
import com.project.tta.services.TimeTableParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TimeTableController {
    private static final Logger log = LoggerFactory.getLogger(TimeTableController.class);
    private final TimeTableParser timeTableParser;
    private final EvaluationService evaluationService;

    public TimeTableController(TimeTableParser timeTableParser, EvaluationService evaluationService) {
        this.timeTableParser = timeTableParser;
        this.evaluationService = evaluationService;
    }
//    @GetMapping("/links")
//    public List<GroupLink> getLinks() {
//        List<GroupLink> groupLinkList = null;
//        try {
//            groupLinkList = timeTableParser.getLinks();
//        } catch (IOException e) {
//            log.error("IOException when getLinks:" + e);
//        }
//        return groupLinkList;
//    }

    @GetMapping("/links/{link}")
    public String getTimeTable(@PathVariable String link) {
        link = "/timetable/" + link;
        String result = "";
        try {
            result = timeTableParser.printTimeTable(timeTableParser.getTimeTable(link).getTimeTable());
        }catch (IOException e){
            log.error("IOException when getTimeTable with link = "+link + " :" + e);
        }
        return result;
    }

    @GetMapping("/grade/{link}")
    public String getGrade(@PathVariable String link) {
        link = "/timetable/" + link;
        Group result = null;
        try {
            var timeTable = timeTableParser.getTimeTable(link);
            result = evaluationService.evaluateTimeTable(timeTable);
        }catch (IOException e){
            log.error("IOException when getGrade with link = "+link + " :" + e);
        }
        return result.getTTEvaluation().getCriterionEvaluationList().stream()
                .map(e -> String.format(
                        "{\"%s\" with grade=%d}",
                        e.getCriterionName(),
                        e.getScore()
                ))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
