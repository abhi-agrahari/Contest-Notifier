package com.contestnotifier.backend.dto;

import lombok.Data;

@Data
public class PreferenceResponseDTO {
    private Long id;
    private String platform;
    private boolean enabled;
    private int notifyBeforeMinutes;
    private String userEmail;

    public PreferenceResponseDTO(Long id, String platform, boolean enabled, int notifyBeforeMinutes, String userEmail) {
        this.id = id;
        this.platform = platform;
        this.enabled = enabled;
        this.notifyBeforeMinutes = notifyBeforeMinutes;
        this.userEmail = userEmail;
    }
}