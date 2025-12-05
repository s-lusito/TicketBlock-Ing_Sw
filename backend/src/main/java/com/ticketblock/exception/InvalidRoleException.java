package com.ticketblock.exception;

public class InvalidRoleException extends AppException {
    public InvalidRoleException() {
        super("Role must be one of [USER, ORGANIZER]","Not valid role");
    }
}
