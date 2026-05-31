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

                if (columns.size() < 3) {
                    continue;
                }

                String timeStr = columns.get(0).select("time").text();
                LocalDateTime startTime = null;
                try {
                   startTime = LocalDateTime.parse(timeStr.substring(0, 19).replace(" ", "T"));
                } catch (Exception e) {
                   startTime = LocalDateTime.now().plusDays(1);
                }

                String name = columns.get(1).text();
                String url = "https://atcoder.jp" + columns.get(1).select("a").attr("href");

                String durationStr = columns.get(2).text();
                long durationMinutes = 120;
                try {
                    String[] parts = durationStr.split(":");
                    durationMinutes = Long.parseLong(parts[0]) * 60 + Long.parseLong(parts[1]);
                } catch (Exception e) {}

                Contest contest = Contest.builder()
                                .platform("AtCoder")
                                .contestId(name)
                                .name(name)
                                .url(url)
                                .startTime(startTime)
                                .duration(durationMinutes)
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
