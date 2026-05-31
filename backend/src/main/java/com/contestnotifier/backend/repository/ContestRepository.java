package com.contestnotifier.backend.repository;

import com.contestnotifier.backend.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    Optional<Contest> findByPlatformAndContestId(String platform, String contestId);
}