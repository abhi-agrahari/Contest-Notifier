package com.contestnotifier.backend.repository;

import com.contestnotifier.backend.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    Optional<Contest> findByPlatformAndContestId(String platform, String contestId);
    List<Contest> findByPlatformInOrderByStartTimeAsc(List<String> platforms);
    List<Contest> findAllByOrderByStartTimeAsc();
    
    @Modifying
    @Transactional
    // query to delete past contests
    @Query(value = "DELETE FROM contest WHERE start_time IS NULL OR DATE_ADD(start_time, INTERVAL duration MINUTE) <= NOW()", nativeQuery = true)
    void deleteOldContests();
}