package com.contestnotifier.backend.service;

import com.contestnotifier.backend.dto.RatingDTO;
import com.contestnotifier.backend.entity.Contest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RatingService ratingService;
    private final ContestService contestService;
    private final GeminiService geminiService;

    public String recommend(String cfHandle, String lcUsername) {

        if (cfHandle == null || cfHandle.isEmpty() || lcUsername == null || lcUsername.isEmpty()) {
            return "{\"recommended\": [], \"message\": \"Please set your Codeforces and LeetCode handles in settings for personalized recommendations.\"}";
        }

        RatingDTO cf = ratingService.getCodeforcesRating(cfHandle);


        RatingDTO lc = ratingService.getLeetCodeRating(lcUsername);

        List<Contest> contests = contestService.getAllContests();

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are an expert competitive programming mentor.

                Recommend ONLY from the upcoming contests provided.
                
                And Recommend ONLY 3 to 4 contest.

                User Details:

                """);

        prompt.append("Codeforces Rating : ")
                .append(cf.getCurrentRating())
                .append("\n");

        prompt.append("LeetCode Rating : ")
                .append(lc.getCurrentRating())
                .append("\n\n");

        prompt.append("Recent Codeforces Ratings : ")
                .append(cf.getLastFiveRatings())
                .append("\n");

        prompt.append("Recent LeetCode Ratings : ")
                .append(lc.getLastFiveRatings())
                .append("\n\n");

        prompt.append("Upcoming Contests:\n");

        for (Contest contest : contests) {

            prompt.append("- ")
                    .append(contest.getName())
                    .append(" | ")
                    .append(contest.getPlatform())
                    .append(" | ")
                    .append(contest.getStartTime())
                    .append("\n");
        }

        prompt.append("""

                Recommend the best contests.

                Return JSON only.

                Format:

                {
                  "recommended":[
                    {
                      "contest":"Contest Name",
                      "reason":"..."
                    }
                  ]
                }
                """);

        String response = geminiService.getRecommendation(prompt.toString());
        
        // Clean up markdown code blocks if present
        if (response.startsWith("```json")) {
            response = response.substring(7);
        } else if (response.startsWith("```")) {
            response = response.substring(3);
        }
        
        if (response.endsWith("```")) {
            response = response.substring(0, response.length() - 3);
        }
        
        return response.trim();

    }

}