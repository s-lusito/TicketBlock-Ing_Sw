package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ticket purchase response.
 * 
 * This DTO is used to communicate the result of a ticket purchase
 * operation, indicating success status and providing a message
 * with details about the transaction.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTicketResponse {
    private Boolean success;
    private String message;
}
