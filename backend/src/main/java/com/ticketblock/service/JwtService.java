package com.ticketblock.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/* JwtService
 - Servizio per gestione JWT:
   - generateToken(...): costruisce un JWT con subject = username, issuedAt, expiration e firma HS256.
   - extractUsername / extractClaim: helper per leggere claims (dati) dal token.
   - isTokenValid: verifica che il token appartenga all'utente e non sia scaduto.
   - la costante di expiration è impostata con 1000*60*60*24 (24 ore)
*/

/**
 * Service for managing JSON Web Tokens (JWT). This class provides functionalities
 * to generate, validate, and extract data (claims) from JWTs.
 */
@Service
public class JwtService {
    private static final Integer EXPIRATION_TIME = 60 * 60 * 24 * 30;

    private static final String SECRET_KEY = "97455b1a39ee9b4154fb89450975da9803964501de7ffaa8e41022c232f2c3ea";

    /**
     * Extracts the username (subject) from a JWT token.
     * 
     * @param token the JWT token to extract username from
     * @return the username stored in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); //il subject è lo username
    }

    /**
     * Generates a JWT token for a user with default claims.
     * 
     * @param userDetails the user details to generate the token for
     * @return the generated JWT token as a string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }


    /**
     * Validates a JWT token against user details.
     * 
     * Checks if the token's username matches the provided user details and
     * that the token has not expired.
     * 
     * @param token the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generates a JWT token with custom claims.
     * 
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details to generate the token for
     * @return the generated JWT token as a string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //il token vale per 24h
                .signWith(getSignKey())
                .compact();

    }

    /**
     * Extracts a specific claim from a JWT token using a resolver function.
     * 
     * @param <T> the type of the claim to extract
     * @param token the JWT token to extract the claim from
     * @param claimsResolver function to resolve the specific claim from all claims
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey()) //la sign key serve per verificare che il messaggio non sia stato modificato
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
