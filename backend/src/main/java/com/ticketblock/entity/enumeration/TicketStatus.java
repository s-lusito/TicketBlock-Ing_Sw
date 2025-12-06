package com.ticketblock.entity.enumeration;

/**
 * Enumeration representing the status of a ticket.
 * 
 * Possible states:
 * - AVAILABLE: Ticket is available for purchase
 * - SOLD: Ticket has been purchased by a user
 * - INVALIDATED: Ticket has been invalidated and cannot be used
 */
public enum TicketStatus {
    AVAILABLE,
    SOLD,
    INVALIDATED,
}
