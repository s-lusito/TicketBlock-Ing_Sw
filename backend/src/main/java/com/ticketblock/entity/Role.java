package com.ticketblock.entity;

public enum Role {
    USER,
    ORGANIZER,
    ADMIN;

    public static boolean isValid(String role) {
        try {
            valueOf(role);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
