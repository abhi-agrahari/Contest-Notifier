package com.contestnotifier.backend.service;

import com.contestnotifier.backend.entity.Contest;
import com.contestnotifier.backend.fetcher.api.CodeforcesFetcher;
import com.contestnotifier.backend.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestService {

    private final ContestRepository contestRepository;

    private final CodeforcesFetcher codeforcesFetcher;

    public ContestService(ContestRepository contestRepository, CodeforcesFetcher codeforcesFetcher){
        this.contestRepository = contestRepository;
        this.codeforcesFetcher = codeforcesFetcher;
    }

    public void refreshContests() {

        List<Contest> contests =
                codeforcesFetcher.fetchContests();

        contestRepository.saveAll(contests);
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }
}
