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
public class AtCoderFetcher implements ContestFetcher {

    @Override
    public List<Contest> fetchContests() {

        List<Contest> contests = new ArrayList<>();

        try {

            Document document = Jsoup.connect(
                                    "https://atcoder.jp/contests/"
                            )
                            .userAgent("Mozilla/5.0")
                            .get();

            Elements rows = document.select(
                            "#contest-table-upcoming tbody tr"
                    );

            for (Element row : rows) {

                Elements columns = row.select("td");

                if (columns.size() < 2) {
                    continue;
                }

                String name = columns.get(1).text();

                String url = "https://atcoder.jp"
                                + columns.get(1)
                                .select("a")
                                .attr("href");

                Contest contest = Contest.builder()
                                .platform("AtCoder")
                                .contestId(name)
                                .name(name)
                                .url(url)
                                .duration(120L)
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
