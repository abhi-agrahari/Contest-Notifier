package com.contestnotifier.backend.fetcher.scraper;

import com.contestnotifier.backend.entity.Contest;
import com.contestnotifier.backend.fetcher.ContestFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HackerEarthFetcher implements ContestFetcher {

    @Override
    public List<Contest> fetchContests() {

        List<Contest> contests =
                new ArrayList<>();

        try {

            Document document =
                    Jsoup.connect(
                                    "https://www.hackerearth.com/challenges/"
                            )
                            .userAgent("Mozilla/5.0")
                            .get();

            System.out.println(document.title());

        } catch (Exception e) {

            e.printStackTrace();
        }

        return contests;
    }
}
