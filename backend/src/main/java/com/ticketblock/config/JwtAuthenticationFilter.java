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

/* JwtAuthenticationFilter
 - Estende OncePerRequestFilter: eseguito una volta per ogni richiesta HTTP.
 - Funzione:
   1. Legge header "Authorization" e verifica formato "Bearer <token>".
   2. Usando JwtService estrae lo username dal token.
   3. Se l'utente non è già autenticato carica UserDetails dal UserDetailsService.
   4. Se il token è valido crea un UsernamePasswordAuthenticationToken e lo imposta nel SecurityContext.
   5. Chiama sempre filterChain.doFilter(...) per proseguire la catena dei filtri.
 - Nota: il filtro viene aggiunto prima di UsernamePasswordAuthenticationFilter nella SecurityConfiguration.
 - Se il token è mancante o invalido la richiesta prosegue non autenticata.
*/

@Component
@RequiredArgsConstructor // crea un costruttore con i campi final
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); //passa al filtro successivo
            return;
        }
        jwt = authHeader.substring(7); //inizia dopo 7 caratteri
        userEmail = jwtService.extractUsername(jwt);

        //verifico se è già autenticato
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { //se l'utente è valido e non è già loggato
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if(jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); //rappresenta utente autenticato per spring
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); //Inserisce il token appena creato nel SecurityContext di Spring, rendendo l’utente autenticato per il resto della richiesta.
            }
        }
        filterChain.doFilter(request, response);

    }
}

