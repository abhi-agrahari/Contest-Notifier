package com.contestnotifier.backend.service;

import com.contestnotifier.backend.entity.Contest;
import com.contestnotifier.backend.fetcher.ContestFetcher;
import com.contestnotifier.backend.repository.ContestRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContestService {

    private final ContestRepository contestRepository;

    private final List<ContestFetcher> fetchers;

    public ContestService(ContestRepository contestRepository, List<ContestFetcher> fetchers) {
        this.contestRepository = contestRepository;
        this.fetchers = fetchers;
    }

    // refresh upcoming contests
    @Scheduled(cron = "0 0 1 * * ?")
    public void refreshContests() {
        LocalDateTime now = LocalDateTime.now();
        for (ContestFetcher fetcher : fetchers) {
            try {
                List<Contest> contests = fetcher.fetchContests();
                System.out.println("Fetched " + contests.size() + " contests from " + getPlatformName(fetcher));
                
                for (Contest contest : contests) {
                    if (contest.getStartTime() != null && contest.getStartTime().isAfter(now)) {
                        saveOrUpdate(contest);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error fetching contests from " + getPlatformName(fetcher) + ": " + e.getMessage());
            }
        }
    }

    // save and update contest in database
    private void saveOrUpdate(Contest contest) {
        contestRepository.findByPlatformAndContestId(contest.getPlatform(), contest.getContestId())
            .ifPresentOrElse(
                existing -> {
                    existing.setName(contest.getName());
                    existing.setStartTime(contest.getStartTime());
                    existing.setDuration(contest.getDuration());
                    existing.setUrl(contest.getUrl());
                    existing.setLastUpdated(LocalDateTime.now());
                    contestRepository.save(existing);
                },
                () -> contestRepository.save(contest)
            );
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAllByOrderByStartTimeAsc();
    }

    public List<Contest> getContestsByPlatforms(List<String> platforms) {
        if (platforms == null || platforms.isEmpty()) {
            return getAllContests();
        }
        return contestRepository.findByPlatformInOrderByStartTimeAsc(platforms);
    }

    // get all platform names
    private String getPlatformName(ContestFetcher fetcher) {
        String className = fetcher.getClass().getSimpleName();
        return className.replace("Fetcher", "");
    }

    // delete all old contests from database
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupOldContests() {
        contestRepository.deleteOldContests();
    }
}
