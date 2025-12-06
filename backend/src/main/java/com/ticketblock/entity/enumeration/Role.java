package com.ticketblock.entity.enumeration;

/**
 * Enumeration representing user roles in the ticketing system.
 * 
 * Possible roles:
 * - USER: Regular user who can purchase tickets
 * - ORGANIZER: User who can create and manage events
 * - ADMIN: System administrator with full privileges
 */
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
