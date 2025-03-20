package com.project.tta.controllers;

import com.project.tta.models.GroupLink;
import com.project.tta.services.TimeTableParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/parser")
public class TimeTableController {
    private static final Logger log = LoggerFactory.getLogger(TimeTableController.class);
    private final TimeTableParser timeTableParser;

    public TimeTableController(TimeTableParser timeTableParser) {
        this.timeTableParser = timeTableParser;
    }
    @GetMapping("/links")
    public List<GroupLink> getLinks() {
        List<GroupLink> groupLinkList = null;
        try {
            groupLinkList = timeTableParser.getLinks();
        } catch (IOException e) {
            log.error("IOException when getLinks:" + e);
        }
        return groupLinkList;
    }

    @GetMapping("/{link}")
    public String getTimeTable(@PathVariable String link) {
        link = "/timetable/" + link;
        String result = "";
        try {
            result = timeTableParser.printTimeTable(timeTableParser.getTimeTable(link));
        }catch (IOException e){
            log.error("IOException when getTimeTable with link = "+link + " :" + e);
        }
        return result;
    }
}
