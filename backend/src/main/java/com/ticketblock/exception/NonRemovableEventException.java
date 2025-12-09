package com.ticketblock.exception;

import lombok.Getter;

@Getter
public class NonRemovableEventException extends AppException {
    public NonRemovableEventException(String message, String userMessage) {
        super(message, userMessage);
    }

    public NonRemovableEventException(String message) {
        super(message);
    }
}
