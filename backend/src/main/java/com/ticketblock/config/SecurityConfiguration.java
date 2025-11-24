package com.ticketblock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    private final JwtAuthenticationFilter jwtAuthFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disabilita CSRF perché stiamo usando JWT per l'autenticazione
                // CSRF serve principalmente per form-based login con cookie
                .csrf(AbstractHttpConfigurer::disable)

                // Configurazione delle autorizzazioni sulle rotte
                .authorizeHttpRequests(auth -> auth
                        // Rotte "whitelist" aperte a tutti (es. login, registrazione, API pubbliche)
                        .requestMatchers(WHITE_LIST_URL).permitAll()

                        // Esempio: proteggi endpoint di management per ADMIN
                        .requestMatchers("/api/v1/management/**").hasAuthority("ADMIN")

                        // Tutte le altre richieste richiedono autenticazione
                        .anyRequest().authenticated()
                )

                // Gestione sessione: stateless perché usiamo JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Aggiunge il filtro JWT prima del filtro standard di Spring per username/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Configurazione del logout
                .logout(logout -> logout
                        // URL per il logout
                        .logoutUrl("/api/v1/auth/logout")
                        // Pulizia del contesto di sicurezza al termine del logout
                        .logoutSuccessHandler((request, response, authentication) ->
                                SecurityContextHolder.clearContext())
                );

        // Costruisce il SecurityFilterChain finale
        return http.build();
    }


}
