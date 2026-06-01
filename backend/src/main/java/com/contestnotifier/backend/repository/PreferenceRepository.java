package com.contestnotifier.backend.repository;

import com.contestnotifier.backend.entity.NotificationPreference;
import com.contestnotifier.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findByUser(User user);
    Optional<NotificationPreference> findByUserAndPlatform(User user, String platform);
    void deleteByUserAndPlatform(User user, String platform);
}
