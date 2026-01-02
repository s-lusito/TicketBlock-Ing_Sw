package com.ticketblock.service;

import java.util.HashMap;
import java.util.Map;
import com.ticketblock.dto.Request.AuthenticationRequest;
import com.ticketblock.dto.Request.RegisterRequest;
import com.ticketblock.dto.Response.AuthenticationResponse;
import com.ticketblock.entity.User;
import com.ticketblock.entity.Wallet;
import com.ticketblock.entity.enumeration.Role;
import com.ticketblock.repository.UserRepository;
import com.ticketblock.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WalletRepository walletRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())); // autentico
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found")); // prelevo dal db

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole());

        var jwt = jwtService.generateToken(extraClaims, user);
        return AuthenticationResponse.builder().token(jwt).build();

    }

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) { // se la mail è già presente
            throw new IllegalArgumentException("User is already registered");
        }
        if (walletRepository.countWalletsByFreeTrue() == 0) {
            throw new RuntimeException("No wallet available");
        }

        Role role = Role.valueOf(request.getRole());

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(role)
                .build();
        userRepository.save(user);

        if (role.equals(Role.USER)) {
            Wallet wallet = walletRepository.findFirstByFreeTrue();
            wallet.setFree(false);
            wallet.setUser(user);
            walletRepository.save(wallet);
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole());

        var jwt = jwtService.generateToken(extraClaims, user);
        return AuthenticationResponse.builder().token(jwt).build();
    }
}
