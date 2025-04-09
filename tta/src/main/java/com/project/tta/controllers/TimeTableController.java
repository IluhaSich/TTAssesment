package com.project.tta.controllers;

import com.project.tta.models.Group;
import com.project.tta.services.EvaluationService;
import com.project.tta.services.TTAService;
import com.project.tta.services.TimeTableParser;
import com.project.tta.viewModels.AllGradesViewModel;
import com.project.tta.viewModels.CriterionsModelView;
import com.project.tta.viewModels.GroupViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        }catch (IOException e){
            log.error("IOException when getTimeTable with link = "+link + " :" + e);
        }
        return result;
    }

    //TODO: сделать добавление по названию группы (второй контроллер)
    @GetMapping("/grades/{link}")
    public String getGrade(@PathVariable String link, Model model) {
        Group group = null;
        if (ttaService.existByLink(link)){
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
                        .map( c -> new CriterionsModelView(c.getCriterionName(),c.getScore()))
                        .toList());
        model.addAttribute("model", g);
        return "group";
    }

    @GetMapping("/grades/")
    public String getGrades(Model model){
        var groups = ttaService.findAll();
        var allGroupList = groups.stream().map(
                g -> new GroupViewModel(
                        g.getName(),
                        g.getLink(),
                        g.getTTEvaluation().getTotal_grade(),
                        g.getTTEvaluation().getCriterionEvaluationList().stream().map(
                                t -> new CriterionsModelView(t.getCriterionName(),t.getScore())).toList()
                        )).toList();
        var g = new AllGradesViewModel(allGroupList,null,null);
        model.addAttribute("model", g);
        return "group-list";
    }
}
