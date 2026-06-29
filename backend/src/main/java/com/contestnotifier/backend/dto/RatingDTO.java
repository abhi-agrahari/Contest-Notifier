package com.contestnotifier.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RatingDTO {

    private Platform platform;

    private String username;

    private Integer currentRating;

    private Integer maxRating;

    private List<Integer> lastFiveRatings;

    private List<String> lastFiveContests;
}