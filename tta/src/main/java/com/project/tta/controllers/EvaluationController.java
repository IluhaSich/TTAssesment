package com.project.tta.controllers;

import com.project.tta.services.ScheduleEvaluator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {
    private final ScheduleEvaluator scheduleEvaluator;

    public EvaluationController(ScheduleEvaluator scheduleEvaluator) {
        this.scheduleEvaluator = scheduleEvaluator;
    }

    @GetMapping("/")
    public void evaluate(){
        scheduleEvaluator.evaluateAndSaveAll();
    }
}
