package com.project.tta.controllers;

import com.project.tta.dtos.GroupTotalScore;
import com.project.tta.models.CriterionEvaluation;
import com.project.tta.models.Group;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.InstituteMapper;
import com.project.tta.services.TTAService;
import com.project.tta.services.TimeTableParser;
import com.project.tta.viewModels.AllGradesViewModel;
import com.project.tta.viewModels.AllRecordsViewModel;
import com.project.tta.viewModels.CriterionsModelView;
import com.project.tta.viewModels.GroupViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class TimeTableController {
    private static final Logger log = LoggerFactory.getLogger(TimeTableController.class);
    private final TimeTableParser timeTableParser;
    private final EvaluationService evaluationService;
    private final TTAService ttaService;

    public TimeTableController(TimeTableParser timeTableParser, EvaluationService evaluationService, TTAService ttaService) {
        this.timeTableParser = timeTableParser;
        this.evaluationService = evaluationService;
        this.ttaService = ttaService;
    }

    @GetMapping("/links/{link}")
    public String getTimeTable(@PathVariable String link) {
        link = "/timetable/" + link;
        String result = "";
        try {
            result = timeTableParser.printTimeTable(timeTableParser.getTimeTable(link).getTimeTable());
        } catch (IOException e) {
            log.error("IOException when getTimeTable with link = " + link + " :" + e);
        }
        return result;
    }

    //TODO: сделать добавление по названию группы (второй контроллер)
    @GetMapping("/grades/{link}")
    public String getGrade(@PathVariable String link, Model model) {
        Group group = null;
        if (ttaService.existByLink(link)) {
            group = ttaService.findByLink(link);
        } else {
            try {
                var timeTable = timeTableParser.getTimeTable(link);
                group = evaluationService.evaluateTimeTable(timeTable);
            } catch (IOException e) {
                log.error("IOException when getGrade with link = " + link + " :" + e);
            }
        }

        var g = new GroupViewModel(
                group.getName(),
                group.getLink(),
                group.getTTEvaluation().getTotal_grade(),
                group.getTTEvaluation().getCriterionEvaluationList().stream()
                        .map(c -> new CriterionsModelView(c.getCriterionName(), c.getScore()))
                        .toList());
        model.addAttribute("model", g);
        return "group";
    }

    @GetMapping("/add_groups/")
    public void addGroups() {
        try {
            timeTableParser.getLinks(evaluationService, ttaService);
        } catch (IOException e) {
            log.error("IOException when add_groups : "+ e);
        }
    }

//    @GetMapping("/grades/")
//    public String getGrades(Model model) {
//        var groups = ttaService.findAll();
//        var allGroupList = groups.stream().map(
//                g -> new GroupViewModel(
//                        g.getName(),
//                        g.getLink(),
//                        g.getTTEvaluation().getTotal_grade(),
//                        g.getTTEvaluation().getCriterionEvaluationList().stream().map(
//                                t -> new CriterionsModelView(t.getCriterionName(), t.getScore())).toList()
//                )).toList();
//        var g = new AllGradesViewModel(allGroupList, null, null);
//        model.addAttribute("model", g);
//        return "group-list";
//    }

    @GetMapping("/grades/all")
    public String getAllRecords(
            @RequestParam(required = false) String groupNameFilter,
            @RequestParam(required = false) String criterionNameFilter,
            @RequestParam(required = false) String instituteFilter,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") int size,
            Model model) {

//        List<Group> groups = ttaService.findAllGroups();
//        List<CriterionEvaluation> criteria = ttaService.findAllCriteria();
//
//        Set<String> institutes = groups.stream()
//                .map(group -> InstituteMapper.getInstituteByGroupName(group.getName()))
//                .filter(s -> !s.isEmpty())
//                .collect(Collectors.toSet());
//
//        List<Group> filteredGroups = ttaService.findGroupsByInstituteAndFilters(instituteFilter, groupNameFilter);
//
//        List<CriterionEvaluation> filteredCriteria = ttaService.findCriteriaByFilter(criterionNameFilter);
        List<Group> groups = ttaService.findAllGroupsFilteredByInstitute(instituteFilter);

        List<CriterionEvaluation> criteria = ttaService.findAllCriteria();

        Set<String> institutes = groups.stream()
                .map(group -> InstituteMapper.getInstituteByGroupName(group.getName()))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        List<Group> filteredGroups = ttaService.findGroupsByInstituteAndFilters(instituteFilter, groupNameFilter);

        List<CriterionEvaluation> filteredCriteria = ttaService.findCriteriaByFilter(criterionNameFilter);


        var groupCriteriaScores = filteredGroups.stream()
                .flatMap(group -> {
                    if (group.getTTEvaluation() == null) {
                        return Stream.empty();
                    }
                    return group.getTTEvaluation().getCriterionEvaluationList().stream()
                            .filter(criterion -> {
                                if (filteredCriteria.isEmpty()) {
                                    return true;
                                }
                                return filteredCriteria.stream()
                                        .anyMatch(c -> c.getCriterionName().equalsIgnoreCase(criterion.getCriterionName()));
                            })
                            .map(criterion -> new AllRecordsViewModel(
                                    group.getName(),
                                    group.getLink(),
                                    criterion.getCriterionName(),
                                    criterion.getScore()
                            ));
                })
                .toList();

        if ("groupName".equalsIgnoreCase(sortBy)) {
            groupCriteriaScores = "asc".equalsIgnoreCase(sortOrder) ?
                    groupCriteriaScores.stream().sorted(Comparator.comparing(AllRecordsViewModel::getGroupName)).toList() :
                    groupCriteriaScores.stream().sorted(Comparator.comparing(AllRecordsViewModel::getGroupName).reversed()).toList();
        } else if ("criterionName".equalsIgnoreCase(sortBy)) {
            groupCriteriaScores = "asc".equalsIgnoreCase(sortOrder) ?
                    groupCriteriaScores.stream().sorted(Comparator.comparing(AllRecordsViewModel::getCriterionName)).toList() :
                    groupCriteriaScores.stream().sorted(Comparator.comparing(AllRecordsViewModel::getCriterionName).reversed()).toList();
        } else if ("score".equalsIgnoreCase(sortBy)) {
            groupCriteriaScores = "asc".equalsIgnoreCase(sortOrder) ?
                    groupCriteriaScores.stream().sorted(Comparator.comparing(AllRecordsViewModel::getScore)).toList() :
                    groupCriteriaScores.stream().sorted(Comparator.comparing(AllRecordsViewModel::getScore).reversed()).toList();
        }

        int totalItems = groupCriteriaScores.size();
        List<AllRecordsViewModel> paginatedData = groupCriteriaScores.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();

        model.addAttribute("groupCriteriaScores", paginatedData);
        model.addAttribute("pageCount", Math.ceil((double) totalItems / size));
        model.addAttribute("currentPage", page);
        model.addAttribute("allGroups", groups);
        model.addAttribute("allCriteria", criteria);
        model.addAttribute("allInstitutes", institutes);
        model.addAttribute("selectedInstitute", instituteFilter);
        model.addAttribute("selectedGroup", groupNameFilter);
        model.addAttribute("selectedCriterion", criterionNameFilter);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "all_records";
    }

    @GetMapping("/grades/total")
    public String getTotalResults(
            @RequestParam(required = false, defaultValue = "groupName") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") int size,
            Model model) {

        List<GroupTotalScore> totalScores = ttaService.calculateTotalScores();

        if ("groupName".equalsIgnoreCase(sortBy)) {
            totalScores = "asc".equalsIgnoreCase(sortOrder) ?
                    totalScores.stream().sorted(Comparator.comparing(GroupTotalScore::getGroupName)).toList() :
                    totalScores.stream().sorted(Comparator.comparing(GroupTotalScore::getGroupName).reversed()).toList();
        } else if ("score".equalsIgnoreCase(sortBy)) {
            totalScores = "asc".equalsIgnoreCase(sortOrder) ?
                    totalScores.stream().sorted(Comparator.comparingDouble(GroupTotalScore::getTotalScore)).toList() :
                    totalScores.stream().sorted(Comparator.comparingDouble(GroupTotalScore::getTotalScore).reversed()).toList();
        }

        int totalItems = totalScores.size();
        List<GroupTotalScore> paginatedData = totalScores.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();

        model.addAttribute("totalScores", paginatedData);
        model.addAttribute("pageCount", Math.ceil((double) totalItems / size));
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "total_results";
    }

    @GetMapping("/grades/all-groups")
    @ResponseBody
    public List<Group> getAllGroups() {
        return ttaService.findAllGroups();
    }
}
