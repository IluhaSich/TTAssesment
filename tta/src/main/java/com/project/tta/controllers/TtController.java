package com.project.tta.controllers;

import com.project.tta.dtos.TtGradeDto;
import com.project.tta.exeptions.AppException;
import com.project.tta.forms.TtGradeForm;
import com.project.tta.models.TtGrade;
import com.project.tta.services.TtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tt")
public class TtController {
    @Autowired
    private TtService ttService;

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

    @GetMapping("/deleteGrade")
    public Map<String, String> deleteGrade(@RequestParam(name = "id") String gradeId) {

        if (!ttService.existsById(gradeId)) {
            throw new AppException("Grade not found.");
        }

        ttService.deleteGrade(gradeId);
        HashMap<String, String> map = new HashMap<>();
        map.put("result", "all_ok");
        return map;
    }

}
