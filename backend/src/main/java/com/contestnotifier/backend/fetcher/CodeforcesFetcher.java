package com.contestnotifier.backend.fetcher;

import com.contestnotifier.backend.entity.Contest;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CodeforcesFetcher
        implements ContestFetcher {

    private final RestTemplate restTemplate =
            new RestTemplate();

    @Override
    public List<Contest> fetchContests() {

        List<Contest> contests =
                new ArrayList<>();

        try {

            String response =
                    restTemplate.getForObject(
                            "https://codeforces.com/api/contest.list",
                            String.class
                    );

            JSONObject object =
                    new JSONObject(response);

            JSONArray result =
                    object.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {

                JSONObject item =
                        result.getJSONObject(i);

                if (!item.getString("phase")
                        .equals("BEFORE")) {
                    continue;
                }

                Contest contest = Contest.builder()
                        .platform("Codeforces")
                        .contestId(
                                String.valueOf(item.getInt("id"))
                        )
                        .name(item.getString("name"))
                        .startTime(
                                LocalDateTime.ofInstant(
                                        Instant.ofEpochSecond(
                                                item.getLong(
                                                        "startTimeSeconds"
                                                )
                                        ),
                                        ZoneId.systemDefault()
                                )
                        )
                        .duration(
                                item.getLong("durationSeconds") / 60
                        )
                        .url("https://codeforces.com")
                        .lastUpdated(LocalDateTime.now())
                        .build();

                contests.add(contest);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}