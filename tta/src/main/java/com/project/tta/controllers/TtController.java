package com.project.tta.controllers;

import com.project.tta.services.TTAService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tt")
public class TtController {

    private final TTAService ttaService;

    public TtController(TTAService ttaService) {
        this.ttaService = ttaService;
    }


//    @GetMapping("/getAllGrades")
//    public List<TtGradeDto> getAllGrades() {
//        return ttaService.getAllGrades();
//    }

//    @PostMapping("/createGrade")
//    public TtGradeDto createGrade(@RequestBody TtGradeForm form) {
//        TtGrade grade = new TtGrade();
//        grade.setName(form.getName());
//        grade.setGrade(form.getGrade());
//        return ttService.save(grade);
//    }

}
