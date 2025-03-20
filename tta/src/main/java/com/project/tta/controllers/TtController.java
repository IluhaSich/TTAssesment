package com.project.tta.controllers;

import com.project.tta.dtos.TtGradeDto;
import com.project.tta.forms.TtGradeForm;
import com.project.tta.models.TtGrade;
import com.project.tta.services.TtService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tt")
public class TtController {

    private TtService ttService;

    public TtController(TtService ttService) {
        this.ttService = ttService;
    }

    @GetMapping("/getAllGrades")
    public List<TtGradeDto> getAllGrades() {
        return ttService.getAllGrades();
    }

    @PostMapping("/createGrade")
    public TtGradeDto createGrade(@RequestBody TtGradeForm form) {
        TtGrade grade = new TtGrade();
        grade.setName(form.getName());
        grade.setGrade(form.getGrade());
        return ttService.save(grade);
    }

}
