package com.contestnotifier.backend.fetcher.rating;

import com.contestnotifier.backend.dto.Platform;
import com.contestnotifier.backend.dto.RatingDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeetCodeRatingFetcher {

    private final RestTemplate restTemplate;

    public LeetCodeRatingFetcher(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RatingDTO fetch(String username) {

        RatingDTO dto = new RatingDTO();

        dto.setPlatform(Platform.LEETCODE);
        dto.setUsername(username);

        String url = "https://leetcode.com/graphql";

        String query = """
                query userContestRankingInfo($username: String!) {
                  userContestRanking(username: $username) {
                    rating
                    attendedContestsCount
                  }

                  userContestRankingHistory(username: $username) {
                    attended
                    rating
                    contest {
                      title
                    }
                  }
                }
                """;

        JSONObject requestBody = new JSONObject();

        requestBody.put("query", query);

        JSONObject variables = new JSONObject();
        variables.put("username", username);

        requestBody.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity =
                new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        JSONObject json = new JSONObject(response.getBody());

        JSONObject data = json.getJSONObject("data");

        if (data.isNull("userContestRanking")) {
            throw new RuntimeException("Invalid LeetCode username or no contest history.");
        }

        JSONObject ranking = data.getJSONObject("userContestRanking");

        dto.setCurrentRating((int) ranking.getDouble("rating"));

        JSONArray history =
                data.getJSONArray("userContestRankingHistory");

        List<Integer> ratings = new ArrayList<>();
        List<String> contests = new ArrayList<>();

        int maxRating = 0;

        for (int i = 0; i < history.length(); i++) {

            JSONObject contest = history.getJSONObject(i);

            if (!contest.getBoolean("attended"))
                continue;

            int rating = (int) contest.getDouble("rating");

            ratings.add(rating);

            contests.add(
                    contest.getJSONObject("contest")
                            .getString("title")
            );

            maxRating = Math.max(maxRating, rating);
        }

        dto.setMaxRating(maxRating);

        int start = Math.max(0, ratings.size() - 5);

        dto.setLastFiveRatings(
                new ArrayList<>(ratings.subList(start, ratings.size()))
        );

        dto.setLastFiveContests(
                new ArrayList<>(contests.subList(start, contests.size()))
        );

        return dto;
    }
}