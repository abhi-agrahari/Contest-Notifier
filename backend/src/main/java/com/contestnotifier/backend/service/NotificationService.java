package com.contestnotifier.backend.service;

import com.contestnotifier.backend.entity.Contest;
import com.contestnotifier.backend.entity.NotificationLog;
import com.contestnotifier.backend.entity.NotificationPreference;
import com.contestnotifier.backend.entity.User;
import com.contestnotifier.backend.repository.ContestRepository;
import com.contestnotifier.backend.repository.NotificationLogRepository;
import com.contestnotifier.backend.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

@Service
public class NotificationService {
    private final ContestRepository contestRepository;
    private final PreferenceRepository preferenceRepository;
    private final NotificationLogRepository logRepository;
    private final EmailService emailService;

    public NotificationService(ContestRepository contestRepository,
                               PreferenceRepository preferenceRepository,
                               NotificationLogRepository logRepository,
                               EmailService emailService
    ){
        this.contestRepository = contestRepository;
        this.preferenceRepository = preferenceRepository;
        this.logRepository = logRepository;
        this.emailService = emailService;
    }

    // scheduler to send email notifications before contest
    @Scheduled(cron = "0 * * * * *")
    public void processNotifications() {
        List<Contest> contests = contestRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Contest contest : contests) {
            if (contest.getStartTime() == null || contest.getStartTime().isBefore(now)) {
                continue;
            }

            List<NotificationPreference> preferences =
                    preferenceRepository.findByPlatformAndEnabledTrue(contest.getPlatform());

            for (NotificationPreference preference : preferences) {
                User user = preference.getUser();
                if (user == null || !user.isEmailNotificationsEnabled()) continue;

                long minutesLeft = Duration.between(now, contest.getStartTime()).toMinutes();

                // the scheduler is comparing range check keeps notifications reliable
                if (minutesLeft >= 0 && minutesLeft <= preference.getNotifyBeforeMinutes()) {
                    boolean alreadySent = logRepository.existsByUserIdAndContestId(user.getId(), contest.getId());

                    if (!alreadySent) {
                        emailService.sendEmail(
                                user.getEmail(),
                                "Upcoming Contest Reminder: " + contest.getName(),
                                "Hello " + user.getName() + ",\n\n" +
                                "The contest \"" + contest.getName() + "\" on " + contest.getPlatform() + 
                                " is starting in " + minutesLeft + " minutes!\n\n" +
                                "Link: " + contest.getUrl()
                        );

                        NotificationLog log = NotificationLog.builder()
                                .userId(user.getId())
                                .contestId(contest.getId())
                                .sentAt(LocalDateTime.now())
                                .build();
                        logRepository.save(log);
                    }
                }
            }
        }
    }
}

