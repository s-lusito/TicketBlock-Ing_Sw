package com.ticketblock.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private String userMessage;

    public AppException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    public AppException(String message) { // setta lo stesso messaggio per dev e user
        super(message);
        this.userMessage = message;
    }


}
