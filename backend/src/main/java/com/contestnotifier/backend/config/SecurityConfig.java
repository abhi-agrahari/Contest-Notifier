package com.contestnotifier.backend.config;

import com.contestnotifier.backend.security.CustomOAuth2UserService;
import com.contestnotifier.backend.security.JwtAuthenticationFilter;
import com.contestnotifier.backend.security.JwtAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final CustomOAuth2UserService customOAuth2UserService;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

        public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                                                  JwtAuthenticationFilter jwtAuthenticationFilter,
                                                  JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {
                http
                // enable CORS with default configuration
                .cors(Customizer.withDefaults())
                // disable CSRF
                .csrf(csrf -> csrf.disable())
                // handle unauthorized requests
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/contests/**").permitAll()
                        .requestMatchers("/api/preferences/**", "/api/user/**", "/api/rating/**", "/api/recommendation").authenticated()
                        .anyRequest().permitAll())
                // OAuth2 login configuration with Google
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(jwtAuthenticationSuccessHandler)
                        .failureUrl(frontendUrl + "/login?error=true"))
                // logout configuration
                .logout(logout -> logout
                        .logoutSuccessUrl(frontendUrl)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "JWT"));

                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(List.of("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
