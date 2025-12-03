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

/* SecurityConfiguration
 - Definisce SecurityFilterChain:
   - CSRF disabilitato (si usa JWT, non cookie di sessione).
   - WHITE_LIST_URL: rotte pubbliche (es. /api/v1/auth/**, swagger, docs).
   - Protezione: /api/v1/management/** riservata a utenti con authority "ADMIN"; tutte le altre richiedono autenticazione.
   - SessionManagement: SessionCreationPolicy.STATELESS (nessuna sessione server-side, JWT stateless).
   - Aggiunge JwtAuthenticationFilter prima di UsernamePasswordAuthenticationFilter per autenticare richieste via token.
   - Logout: URL /api/v1/auth/logout che pulisce il SecurityContext.
*/


/**
 * SecurityConfiguration is a configuration class that sets up the security settings
 * for the application using Spring Security.
 *
 * Responsibilities and Features:
 * - Disables Cross-Site Request Forgery (CSRF) protection as JWT is being used for authentication.
 * - Configures a set of public routes (whitelisted URLs) that are accessible without authentication.
 * - Defines authorization rules for specific endpoints, such as restricting access to certain resources
 *   based on user roles (e.g., "ADMIN").
 * - Enforces a stateless session management policy due to JWT usage.
 * - Integrates the custom JwtAuthenticationFilter to validate JWT tokens in HTTP requests before the
 *   standard Spring Security filters are applied.
 * - Configures a logout endpoint to clear the SecurityContext upon logout.
 *
 * Components:
 * - JwtAuthenticationFilter: Custom filter that extracts and validates JWT tokens from incoming requests.
 *
 * Beans:
 * - securityFilterChain: Defines the SecurityFilterChain, which sets up the security rules
 *   and integrates the JWT authentication mechanism for the application.
 */
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

                        // Protegge endpoint di management per ADMIN
                        .requestMatchers("/api/v1/management/**").hasAuthority("ADMIN")

                        // Protegge endpoint di gestione biglietti per USER e ADMIN
                        .requestMatchers("/api/v1/tickets/**").hasAnyAuthority("USER,ADMIN")

                        // Protegge endpoint di gestione eventi per ORGANIZER e ADMIN
                        .requestMatchers(HttpMethod.GET,"/api/v1/events/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/events/**").hasAnyAuthority("ORGANIZER", "ADMIN")

                        // Tutte le altre richieste richiedono autenticazione
                        .anyRequest().authenticated()
                )

                // Gestione sessione: stateless perché usiamo JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Aggiunge il filtro JWT prima del filtro standard di Spring per username/password, in modo che si verifichi prima la presenza del token
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
