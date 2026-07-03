package com.contestnotifier.backend.controller;

import com.contestnotifier.backend.entity.User;
import com.contestnotifier.backend.service.RecommendationService;
import com.contestnotifier.backend.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendation")
@CrossOrigin(origins = "http://localhost:5173")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    public RecommendationController(RecommendationService recommendationService, UserService userService){
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String recommend(@AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getUserFromPrincipal(principal);
        return recommendationService.recommend(
                user.getCodeforcesHandle(),
                user.getLeetcodeHandle()
        );
    }

}