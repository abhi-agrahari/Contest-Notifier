package com.contestnotifier.backend.repository;

import com.contestnotifier.backend.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findByPlatformAndEnabledTrue(String platform);
}
