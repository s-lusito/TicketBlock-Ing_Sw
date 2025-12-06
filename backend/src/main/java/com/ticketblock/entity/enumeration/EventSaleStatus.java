package com.ticketblock.entity.enumeration;

/**
 * Enumeration representing the sale status of an event.
 * 
 * Possible states:
 * - NOT_STARTED: Sales have not begun yet
 * - ONGOING: Tickets are currently available for purchase
 * - SOLD_OUT: All tickets have been sold
 * - ENDED: Sales have ended (event is within 24 hours or has passed)
 */
public enum EventSaleStatus {
    NOT_STARTED,
    ONGOING,
    SOLD_OUT,
    ENDED

}
