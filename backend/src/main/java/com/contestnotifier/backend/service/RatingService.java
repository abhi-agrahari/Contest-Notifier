package com.contestnotifier.backend.service;

import com.contestnotifier.backend.dto.RatingDTO;
import com.contestnotifier.backend.fetcher.rating.CodeforcesRatingFetcher;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final CodeforcesRatingFetcher codeforcesRatingFetcher;

    public RatingService(CodeforcesRatingFetcher codeforcesRatingFetcher) {
        this.codeforcesRatingFetcher = codeforcesRatingFetcher;
    }

    public RatingDTO getCodeforcesRating(String handle) {
        return codeforcesRatingFetcher.fetch(handle);
    }
}