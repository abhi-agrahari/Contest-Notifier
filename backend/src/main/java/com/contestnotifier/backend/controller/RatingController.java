package com.contestnotifier.backend.controller;

import com.contestnotifier.backend.dto.RatingDTO;
import com.contestnotifier.backend.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
@CrossOrigin(origins = "http://localhost:5173")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/codeforces/{handle}")
    public ResponseEntity<RatingDTO> getCodeforcesRating(
            @PathVariable String handle) {

        RatingDTO rating = ratingService.getCodeforcesRating(handle);

        return ResponseEntity.ok(rating);
    }
}