package com.gamehub.arena.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Repository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }

    // TIPS: Configurazione principale della sicurezza.
    // Disabilitiamo CSRF perché usiamo JWT (stateless) invece delle classiche sessioni basate su cookie.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/games/**").permitAll()
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tournaments").hasAuthority("ROLE_ORGANIZER")
                        .requestMatchers("/api/tournaments/**").permitAll()
                        .requestMatchers("/api/teams/**").permitAll()
                        .requestMatchers("/api/matches/**").permitAll()
                        .requestMatchers("/api/reviews/**").permitAll()
                        .requestMatchers("/api/messages/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/reports/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                // TIPS: Impostiamo la gestione della sessione come STATELESS.
                // Il server non ricorderà l'utente tra una richiesta e l'altra; ogni richiesta deve contenere il token JWT.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    // TIPS: Configurazione CORS (Cross-Origin Resource Sharing).
    // Permette al frontend (localhost:4200) di comunicare col backend senza essere bloccato dal browser.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
