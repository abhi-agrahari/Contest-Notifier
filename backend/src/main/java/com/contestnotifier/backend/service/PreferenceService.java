package com.contestnotifier.backend.service;

import com.contestnotifier.backend.dto.PreferenceRequestDTO;
import com.contestnotifier.backend.dto.PreferenceResponseDTO;
import com.contestnotifier.backend.entity.NotificationPreference;
import com.contestnotifier.backend.entity.User;
import com.contestnotifier.backend.fetcher.ContestFetcher;
import com.contestnotifier.backend.repository.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final List<ContestFetcher> fetchers;

    public PreferenceService(PreferenceRepository preferenceRepository, List<ContestFetcher> fetchers){
        this.preferenceRepository = preferenceRepository;
        this.fetchers = fetchers;
    }

    public List<String> getSupportedPlatforms() {
        List<String> platforms = new ArrayList<>();

        for (ContestFetcher fetcher : fetchers) {
            String className = fetcher.getClass().getSimpleName();
            String platformName = className.replace("Fetcher", "");

            if (!platforms.contains(platformName)) {
                platforms.add(platformName);
            }
        }

        return platforms;
    }

    public PreferenceResponseDTO savePreference(User user, PreferenceRequestDTO request) {
        NotificationPreference pref = preferenceRepository
                .findByUserAndPlatform(user, request.getPlatform())
                .orElse(new NotificationPreference());

        pref.setUser(user);
        pref.setPlatform(request.getPlatform());
        pref.setEnabled(request.isEnabled());
        pref.setNotifyBeforeMinutes(request.getNotifyBeforeMinutes());

        NotificationPreference saved = preferenceRepository.save(pref);

        return new PreferenceResponseDTO(
                saved.getId(),
                saved.getPlatform(),
                saved.isEnabled(),
                saved.getNotifyBeforeMinutes(),
                user.getEmail()
        );
    }

    public List<PreferenceResponseDTO> getUserPreferences(User user) {
        List<NotificationPreference> savedPreferences = preferenceRepository.findByUser(user);
        Map<String, NotificationPreference> prefMap = savedPreferences.stream()
                .collect(Collectors.toMap(NotificationPreference::getPlatform, p -> p));

        List<String> supportedPlatforms = getSupportedPlatforms();
        List<PreferenceResponseDTO> result = new ArrayList<>();

        for (String platform : supportedPlatforms) {
            NotificationPreference pref = prefMap.get(platform);
            if (pref != null) {
                result.add(new PreferenceResponseDTO(
                        pref.getId(),
                        pref.getPlatform(),
                        pref.isEnabled(),
                        pref.getNotifyBeforeMinutes(),
                        user.getEmail()
                ));
            } else {
                result.add(new PreferenceResponseDTO(
                        null,
                        platform,
                        false,
                        30,
                        user.getEmail()
                ));
            }
        }

        return result;
    }
}
