package com.ticketblock.service;

import com.ticketblock.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class SecurityService {


    public User getLoggedInUser(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new RuntimeException("Authentication principal is not of type User, found: " + principal.getClass().getName());
    }

}
