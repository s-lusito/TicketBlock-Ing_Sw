package com.ticketblock.exception;

/**
 * Exception thrown when an invalid user role is specified.
 * 
 * This exception is used during user registration when a role other than
 * USER or ORGANIZER is provided, or when attempting to register as ADMIN.
 */
public class InvalidRoleException extends AppException {
    public InvalidRoleException() {
        super("Role must be one of [USER, ORGANIZER]","Not valid role");
    }
}
