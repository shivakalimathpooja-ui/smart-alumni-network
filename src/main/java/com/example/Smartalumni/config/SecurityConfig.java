package com.example.Smartalumni.config;

import com.example.Smartalumni.security.JwtFilter;
import com.example.Smartalumni.security.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomerUserDetailsService customerUserDetailsService;

    // ✅ Password encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customerUserDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ✅ Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ✅ Security Filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {
                })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ Register for event
                        .requestMatchers(HttpMethod.POST, "/api/events/register")
                        .hasAnyRole("STUDENT", "ALUMNI")

                        // ✅ GET → view events
                        .requestMatchers(HttpMethod.GET, "/api/events/**")
                        .hasAnyRole("STUDENT", "ALUMNI", "ADMIN")

                        // ✅ POST → create event
                        .requestMatchers(HttpMethod.POST, "/api/events/**")
                        .hasAnyRole("ALUMNI", "ADMIN")

                        // ✅ UPDATE event
                        .requestMatchers(HttpMethod.PUT, "/api/events/**")
                        .hasAnyRole("ALUMNI", "ADMIN")

                        // ✅ DELETE → remove event
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**")
                        .hasRole("ADMIN")

                        // ✅ View jobs
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**")
                        .hasAnyRole("STUDENT", "ALUMNI", "ADMIN")

                        // ✅ Specific
                        .requestMatchers(HttpMethod.POST, "/api/jobs/apply")
                        .hasRole("STUDENT")

                        // ✅ General
                        .requestMatchers(HttpMethod.POST, "/api/jobs")
                        .hasAnyRole("ALUMNI", "ADMIN")

                        // ✅ Update job
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**")
                        .hasAnyRole("ALUMNI", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/jobs/**")
                        .hasAnyRole("STUDENT", "ALUMNI", "ADMIN")

                        // ✅ Delete job
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**")
                        .hasRole("ADMIN")

                        // RSVP (all logged users)
                        .requestMatchers("/api/rsvp/**")
                        .hasAnyRole("STUDENT", "ALUMNI", "ADMIN")

                        .requestMatchers("/api/student/**").hasRole("STUDENT")

                        .requestMatchers("/api/alumni/**")
                        .hasAnyRole("STUDENT", "ALUMNI", "ADMIN")

                        .anyRequest().authenticated())

                .authenticationProvider(authenticationProvider()) // ✅ OK now
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}