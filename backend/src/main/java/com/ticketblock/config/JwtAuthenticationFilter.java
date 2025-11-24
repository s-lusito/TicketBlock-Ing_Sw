package com.ticketblock.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor // crea un costruttore con i campi final
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    /**
     * Questo metodo viene eseguito una volta per ogni richiesta HTTP grazie a OncePerRequestFilter.
     * Serve per intercettare la richiesta prima che arrivi al Controller e gestire l'autenticazione tramite JWT.
     *
     * Funzioni principali:
     *  1. Estrarre l'header "Authorization" dalla richiesta.
     *  2. Verificare se contiene un token JWT valido (formato: "Bearer <token>").
     *  3. Se il token è valido:
     *      - estrarre lo username dal token;
     *      - caricare i relativi UserDetails;
     *      - creare un oggetto di autenticazione;
     *      - inserirlo nel SecurityContext, così Spring sa che l'utente è autenticato.
     *  4. In ogni caso, chiamare filterChain.doFilter(...) per permettere il proseguimento
     *     della richiesta verso il prossimo filtro o verso il controller.
     *
     * Se non viene chiamato filterChain.doFilter(), la richiesta rimane bloccata e non arriva al controller.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7); //inizia dopo 7 caratteri
        userEmail = jwtService.extractUsername(jwt);
        //verifico se è già autenticato

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if(jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);

    }
}

