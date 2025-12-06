package com.ticketblock.controller;

import com.ticketblock.dto.Request.PurchaseTicketRequest;
import com.ticketblock.dto.Response.PurchaseTicketResponse;
import com.ticketblock.dto.Response.TicketDto;
import com.ticketblock.dto.Response.UserDto;
import com.ticketblock.service.TicketService;
import com.ticketblock.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {


    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }


}








