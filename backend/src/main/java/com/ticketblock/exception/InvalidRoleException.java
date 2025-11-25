package com.ticketblock.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("Role must be one of [ROLE_USER, ROLE_ADMIN]");
    }
}
