package com.ticketblock.exception;

import lombok.Getter;

@Getter
public class BlockchainException extends AppException {
    public BlockchainException(String message, String userMessage) {
        super(message, userMessage);
    }

    public BlockchainException(String message) {
        super(message);
    }
}
