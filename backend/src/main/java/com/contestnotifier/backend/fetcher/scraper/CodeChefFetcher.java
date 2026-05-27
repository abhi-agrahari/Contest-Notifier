package com.contestnotifier.backend.fetcher.scraper;

import com.contestnotifier.backend.entity.Contest;
import com.contestnotifier.backend.fetcher.ContestFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CodeChefFetcher implements ContestFetcher {

    @Override
    public List<Contest> fetchContests() {

        List<Contest> contests =
                new ArrayList<>();

        try {

            Document document = Jsoup.connect(
                                    "https://www.codechef.com/contests"
                            )
                            .userAgent("Mozilla/5.0")
                            .get();

            Elements rows = document.select("table tbody tr");

            for (Element row : rows) {

                Elements columns = row.select("td");

                if (columns.size() < 4) {
                    continue;
                }

                String code = columns.get(0).text();

                String name = columns.get(1).text();

                Contest contest = Contest.builder()
                                .platform("CodeChef")
                                .contestId(code)
                                .name(name)
                                .duration(120L)
                                .url(
                                        "https://www.codechef.com/contests"
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