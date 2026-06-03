package com.contestnotifier.backend.controller;

import com.contestnotifier.backend.dto.PreferenceRequestDTO;
import com.contestnotifier.backend.dto.PreferenceResponseDTO;
import com.contestnotifier.backend.entity.User;
import com.contestnotifier.backend.service.PreferenceService;
import com.contestnotifier.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;
    private final UserService userService;

    public PreferenceController(PreferenceService preferenceService, UserService userService){
        this.preferenceService = preferenceService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<PreferenceResponseDTO> addPreference(
            @RequestBody PreferenceRequestDTO request,
            @AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getUserFromPrincipal(principal);
        PreferenceResponseDTO response = preferenceService.savePreference(user, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<List<PreferenceResponseDTO>> updatePreferences(
            @RequestBody List<PreferenceRequestDTO> requests,
            @AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getUserFromPrincipal(principal);
        List<PreferenceResponseDTO> responses = new ArrayList<>();
        for (PreferenceRequestDTO request : requests) {
            responses.add(preferenceService.savePreference(user, request));
        }
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{platform}")
    public ResponseEntity<String> deletePreference(
            @PathVariable String platform,
            @AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getUserFromPrincipal(principal);
        preferenceService.deletePreference(user, platform);
        return ResponseEntity.ok("Deleted " + platform);
    }

    @GetMapping
    public ResponseEntity<List<PreferenceResponseDTO>> getPreferences(
            @AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getUserFromPrincipal(principal);
        List<PreferenceResponseDTO> preferences = preferenceService.getUserPreferences(user);
        return ResponseEntity.ok(preferences);
    }
}