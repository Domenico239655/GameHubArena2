package com.gamehub.arena.controller;


import com.gamehub.arena.model.Report;
import com.gamehub.arena.service.ReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service){
        this.service = service;
    }

    @PostMapping
    public Report create(@RequestBody Report r){
        return service.create(r);
    }
}
