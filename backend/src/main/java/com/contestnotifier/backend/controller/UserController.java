package com.contestnotifier.backend.controller;

import com.contestnotifier.backend.dto.HandleRequestDTO;
import com.contestnotifier.backend.entity.User;
import com.contestnotifier.backend.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        return userService.getUserFromPrincipal(principal);
    }

    @GetMapping("/login")
    public User login(@AuthenticationPrincipal OAuth2User principal) {
        return userService.getUserFromPrincipal(principal);
    }

    @PostMapping("/handles")
    public User updateHandles(@RequestBody HandleRequestDTO request, @AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getUserFromPrincipal(principal);
        user.setLeetcodeHandle(request.getLeetcodeHandle());
        user.setCodeforcesHandle(request.getCodeforcesHandle());
        return userService.saveUser(user);
    }
}