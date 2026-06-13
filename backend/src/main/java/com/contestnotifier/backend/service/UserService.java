package com.contestnotifier.backend.service;

import com.contestnotifier.backend.entity.User;
import com.contestnotifier.backend.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getUserFromPrincipal(OAuth2User principal) {
        if (principal == null) return null;
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String googleId = principal.getAttribute("sub");
        return findOrCreateUser(email, name, googleId);
    }

    public User findOrCreateUser(String email, String name, String googleId) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = User.builder()
                .email(email)
                .name(name != null ? name : email.split("@")[0])
                .googleId(googleId)
                .timezone("IST")
                .build();

        return userRepository.save(newUser);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
