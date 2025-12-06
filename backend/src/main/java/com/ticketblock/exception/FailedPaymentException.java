package com.ticketblock.exception;

import lombok.Getter;

/**
 * Exception thrown when a payment transaction fails.
 * 
 * This exception is used when the payment processing system returns
 * a failure status during ticket purchase transactions.
 */
@Getter
public class FailedPaymentException extends AppException {
    public FailedPaymentException(String message, String userMessage) {
        super(message, userMessage);
    }

    public FailedPaymentException(String message) {
        super(message);
    }
}
