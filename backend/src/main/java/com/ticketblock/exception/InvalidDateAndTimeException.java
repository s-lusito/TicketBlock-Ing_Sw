package com.ticketblock.exception;

/**
 * Exception thrown when invalid date or time values are provided.
 * 
 * This exception is used when event dates, times, or sale start dates
 * do not meet the required constraints, such as dates in the past or
 * end times before start times.
 */
public class InvalidDateAndTimeException extends AppException {
    public InvalidDateAndTimeException(String message, String userMessage) {
        super(message, userMessage);
    }

    public InvalidDateAndTimeException(String message) {
        super(message);
    }
}
