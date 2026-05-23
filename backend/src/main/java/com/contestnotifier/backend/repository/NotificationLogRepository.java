package com.contestnotifier.backend.repository;

import com.contestnotifier.backend.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    boolean existsByUserIdAndContestId(Long userId, Long contestId);
}
