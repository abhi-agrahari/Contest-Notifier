package com.contestnotifier.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final JwtService jwtService;

    public JwtAuthenticationSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // extract OAuth2 user details from authentication object
        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        // create jwt token
        String token = jwtService.createToken(user.getAttribute("email"), user.getAttribute("name"),
                user.getAttribute("sub"));

        // build HTTP-only cookie containing the JWT token
        ResponseCookie cookie = ResponseCookie.from("JWT", token)
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge(86400)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
        response.sendRedirect(frontendUrl);
    }
}