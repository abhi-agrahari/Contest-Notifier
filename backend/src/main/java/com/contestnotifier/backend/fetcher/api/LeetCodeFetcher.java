package com.contestnotifier.backend.fetcher.api;

import com.contestnotifier.backend.entity.Contest;
import com.contestnotifier.backend.fetcher.ContestFetcher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class LeetCodeFetcher implements ContestFetcher {

    private final RestTemplate restTemplate;

    public LeetCodeFetcher(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Contest> fetchContests() {

        List<Contest> contests =
                new ArrayList<>();

        try {

            String query = """
            {
              "query":"query { allContests { title titleSlug startTime duration } }"
            }
            """;

            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON
            );
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set("Referer", "https://leetcode.com/contest/");

            HttpEntity<String> request =
                    new HttpEntity<>(query, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            "https://leetcode.com/graphql",
                            request,
                            String.class
                    );

            JSONObject object =
                    new JSONObject(response.getBody());

            JSONArray array =
                    object.getJSONObject("data")
                            .getJSONArray("allContests");

            for (int i = 0; i < array.length(); i++) {

                JSONObject item =
                        array.getJSONObject(i);

                long startTimeSeconds = item.getLong("startTime");
                if (startTimeSeconds < Instant.now().getEpochSecond()) {
                    continue;
                }

                Contest contest =
                        Contest.builder()
                                .platform("LeetCode")
                                .contestId(
                                        item.getString("titleSlug")
                                )
                                .name(
                                        item.getString("title")
                                )
                                .startTime(
                                        LocalDateTime.ofInstant(
                                                Instant.ofEpochSecond(
                                                        item.getLong(
                                                                "startTime"
                                                        )
                                                ),
                                                ZoneId.systemDefault()
                                        )
                                )
                                .duration(
                                        item.getLong("duration") / 60
                                )
                                .url(
                                        "https://leetcode.com/contest/"
                                                + item.getString(
                                                "titleSlug"
                                        )
                                )
                                .lastUpdated(
                                        LocalDateTime.now()
                                )
                                .build();

                contests.add(contest);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return contests;
    }
}
