package com.project.tta.controllers;

import com.project.tta.models.Group;
import com.project.tta.services.GroupService;
import com.project.tta.services.parser.GroupParserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupsController {
    private final GroupParserService groupParserService;
    private final GroupService groupService;

    public GroupsController(GroupParserService groupParserService, GroupService groupService) {
        this.groupParserService = groupParserService;
        this.groupService = groupService;
    }

//    @GetMapping("/groups/{link}")
//    public String getTimeTable(@PathVariable String link) {
//        link = "/timetable/" + link;
//        String result = "";
//        try {
//            result = timeTableParser.printTimeTable(timeTableParser.getTimeTable(link).getTimeTable());
//        } catch (IOException e) {
//            log.error("IOException when getTimeTable with link = " + link + " :" + e);
//        }
//        return result;
//    }

    @GetMapping("/save/all")
    public String parseAndSaveGroups() throws IOException {
        groupService.saveGroupsFromWeb();
        return "ready";
    }

    @GetMapping("/get")
    public List<Group> getTimeTable() throws IOException {
        return groupParserService.getGroupsFromWeb();
    }
}
