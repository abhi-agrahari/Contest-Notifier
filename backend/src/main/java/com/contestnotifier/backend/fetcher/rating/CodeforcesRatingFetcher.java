package com.contestnotifier.backend.fetcher.rating;

import com.contestnotifier.backend.dto.Platform;
import com.contestnotifier.backend.dto.RatingDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class CodeforcesRatingFetcher {

    private final RestTemplate restTemplate = new RestTemplate();

    public RatingDTO fetch(String handle) {

        RatingDTO dto = new RatingDTO();

        dto.setPlatform(Platform.CODEFORCES);
        dto.setUsername(handle);

        // getting user info
        String userInfoUrl =
                "https://codeforces.com/api/user.info?handles=" + handle;

        String userResponse =
                restTemplate.getForObject(userInfoUrl, String.class);

        JSONObject userJson = new JSONObject(userResponse);

        if (!userJson.getString("status").equals("OK")) {
            throw new RuntimeException("Invalid Codeforces handle");
        }

        JSONObject user =
                userJson.getJSONArray("result").getJSONObject(0);

        dto.setCurrentRating(user.optInt("rating", 0));
        dto.setMaxRating(user.optInt("maxRating", 0));
        dto.setRank(user.optString("rank", "Unrated"));
        dto.setMaxRank(user.optString("maxRank", "Unrated"));

        // getting rating history
        String ratingUrl =
                "https://codeforces.com/api/user.rating?handle=" + handle;

        String ratingResponse =
                restTemplate.getForObject(ratingUrl, String.class);

        JSONObject ratingJson = new JSONObject(ratingResponse);

        List<Integer> ratings = new ArrayList<>();
        List<String> contests = new ArrayList<>();

        if (ratingJson.getString("status").equals("OK")) {

            JSONArray history = ratingJson.getJSONArray("result");

            int start = Math.max(0, history.length() - 5);

            for (int i = start; i < history.length(); i++) {

                JSONObject contest = history.getJSONObject(i);

                ratings.add(contest.getInt("newRating"));
                contests.add(contest.getString("contestName"));
            }
        }

        dto.setLastFiveRatings(ratings);
        dto.setLastFiveContests(contests);

        return dto;
    }
}