package com.contestnotifier.backend.repository;

import com.contestnotifier.backend.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository extends JpaRepository<Contest, Long> {
}