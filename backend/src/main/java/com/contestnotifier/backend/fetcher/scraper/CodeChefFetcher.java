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

            Elements futureTable = document.select("h3:contains(Future Contests) + .table-responsive table tbody tr");
            if (futureTable.isEmpty()) {
                futureTable = document.select("table:first-of-type tbody tr");
            }

            for (Element row : futureTable) {

                Elements columns = row.select("td");

                if (columns.size() < 3) {
                    continue;
                }

                String code = columns.get(0).text();
                String name = columns.get(1).text();
                String startStr = columns.get(2).text();

                LocalDateTime startTime = null;
                try {
                    startTime = LocalDateTime.now().plusDays(7);
                } catch (Exception e) {}

                Contest contest = Contest.builder()
                                .platform("CodeChef")
                                .contestId(code)
                                .name(name)
                                .startTime(startTime)
                                .duration(120L)
                                .url("https://www.codechef.com/" + code)
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