package com.contestnotifier.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (token != null) {
            try {
                Claims claims = jwtService.getClaims(token);

                // build OAuth2User attributes from JWT claims
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("email", claims.getSubject());
                attributes.put("name", claims.get("name", String.class));
                attributes.put("sub", claims.get("googleId", String.class));
                // create OAuth2User object with the claims
                OAuth2User user = new DefaultOAuth2User(List.of(), attributes, "email");
                // set authentication in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            } catch (Exception ignored) {
                // invalid tokens are treated as unauthenticated requests.
            }
        }
        filterChain.doFilter(request, response);
    }

    // extract token from cookies
    private String getToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("JWT".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}