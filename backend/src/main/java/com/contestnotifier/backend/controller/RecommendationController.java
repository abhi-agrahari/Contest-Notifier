package com.contestnotifier.backend.controller;

import com.contestnotifier.backend.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendation")
@CrossOrigin(origins = "http://localhost:5173")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService){
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public String recommend(@RequestParam String codeforces, @RequestParam String leetcode) {

        return recommendationService.recommend(
                codeforces,
                leetcode
        );
    }

}