package com.ticketblock.controller;

import com.ticketblock.dto.Request.AuthenticationRequest;
import com.ticketblock.dto.Request.RegisterRequest;
import com.ticketblock.dto.Response.AuthenticationResponse;
import com.ticketblock.entity.Role;
import com.ticketblock.exception.InvalidRoleException;
import com.ticketblock.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    //valid serve a validare i dati nel dto con le regole previste nel dto
    //Se qualche regola non è rispettata Spring blocca l’esecuzione del metodo e restituisce un 400 Bad Request con i messaggi di errore delle annotazioni.
    //request body legge il body della richiesta http e mappa i dati del json sull'oggetto RegisterRequest

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        isRoleValid(request.getRole());
        return ResponseEntity.ok(authenticationService.register(request));
    }

    private void isRoleValid(@NotNull(message = "Role is required") String role) {
        try {
            Role role1 = Role.valueOf(role);
            if (role1.equals(Role.ADMIN)) {
                throw new InvalidRoleException();
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }







}
