package com.contestnotifier.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"platform", "contestId"})
})
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platform;

    private String contestId;

    private String name;

    private LocalDateTime startTime;

    private Long duration;

    private String url;

    private LocalDateTime lastUpdated;
}