package com.contestnotifier.backend.dto;

import lombok.Data;

@Data
public class PreferenceRequestDTO {
    private String platform;
    private boolean enabled;
    private int notifyBeforeMinutes;
}