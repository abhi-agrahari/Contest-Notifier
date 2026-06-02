package com.contestnotifier.backend.controller;

import com.contestnotifier.backend.entity.User;
import com.contestnotifier.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        return userService.getUserFromPrincipal(principal);
    }

    @GetMapping("/login")
    public User login(@AuthenticationPrincipal OAuth2User principal) {
        return userService.getUserFromPrincipal(principal);
    }
}