package com.contestnotifier.backend.service;

import com.contestnotifier.backend.dto.RatingDTO;
import com.contestnotifier.backend.fetcher.rating.CodeforcesRatingFetcher;
import com.contestnotifier.backend.fetcher.rating.LeetCodeRatingFetcher;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final CodeforcesRatingFetcher codeforcesRatingFetcher;
    private final LeetCodeRatingFetcher leetCodeRatingFetcher;

    public RatingService(CodeforcesRatingFetcher codeforcesRatingFetcher, LeetCodeRatingFetcher leetCodeRatingFetcher) {
        this.codeforcesRatingFetcher = codeforcesRatingFetcher;
        this.leetCodeRatingFetcher = leetCodeRatingFetcher;
    }

    public RatingDTO getCodeforcesRating(String handle) {
        return codeforcesRatingFetcher.fetch(handle);
    }

    public RatingDTO getLeetCodeRating(String username) {
        return leetCodeRatingFetcher.fetch(username);
    }
}