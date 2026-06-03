package com.contestnotifier.backend.controller;

import com.contestnotifier.backend.entity.Contest;
import com.contestnotifier.backend.service.ContestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestController {

    private final ContestService contestService;

    public ContestController(ContestService contestService){
        this.contestService = contestService;
    }

    @GetMapping
    public List<Contest> getContests() {
        return contestService.getAllContests();
    }

    @PostMapping("/refresh")
    public String refreshContests() {

        contestService.refreshContests();

        return "Contests refreshed";
    }

    @GetMapping("/health")
    public String checkHealth(){
        return "health is ok";
    }
}