package com.ticketblock.exception;

import lombok.Getter;

@Getter
public class FailedPaymentException extends AppException {
    public FailedPaymentException(String message, String userMessage) {
        super(message, userMessage);
    }

    public FailedPaymentException(String message) {
        super(message);
    }
}
