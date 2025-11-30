package com.ticketblock.controller;

import com.ticketblock.dto.Request.AuthenticationRequest;
import com.ticketblock.dto.Request.RegisterRequest;
import com.ticketblock.dto.Response.AuthenticationResponse;
import com.ticketblock.entity.enumeration.Role;
import com.ticketblock.exception.InvalidRoleException;
import com.ticketblock.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        validateRole(request.getRole());
        return ResponseEntity.ok(authenticationService.register(request));
    }

    private void validateRole(String role) {
        try {
            Role requestedRole = Role.valueOf(role);
            if (requestedRole.equals(Role.ADMIN)) {
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


    //per fare il logout basta che il frontend elimina il token






}
