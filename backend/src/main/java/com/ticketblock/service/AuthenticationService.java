package com.ticketblock.service;

import com.ticketblock.dto.Request.AuthenticationRequest;
import com.ticketblock.dto.Request.RegisterRequest;
import com.ticketblock.dto.Response.AuthenticationResponse;
import com.ticketblock.entity.User;
import com.ticketblock.entity.enumeration.Role;
import com.ticketblock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling user authentication and registration.
 * 
 * This service manages user authentication using JWT tokens and handles
 * new user registration with password encoding. It validates user credentials
 * and generates JWT tokens for authenticated sessions.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())); //autentico
        var user =  userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new UsernameNotFoundException("User not found")); //prelevo dal db
        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwt).build();

    }

    public AuthenticationResponse register(RegisterRequest request) {

        if(userRepository.findByEmail(request.getEmail()).isPresent()){ //se la mail è già presente
            throw new IllegalArgumentException("User is already registered");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.valueOf(request.getRole()))
                .build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwt).build();
    }
}
