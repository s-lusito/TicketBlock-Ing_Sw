package com.ticketblock.service;

import com.ticketblock.ApplicationEvent.TicketPurchasedEvent;
import com.ticketblock.entity.Event;
import com.ticketblock.entity.enumeration.TicketStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener for handling ticket purchase events.
 * 
 * This component listens for TicketPurchasedEvent and triggers post-purchase
 * actions such as checking if an event has sold out after the purchase
 * transaction has been committed.
 */
@Component
public class TicketEventListener {

    private final EventService eventService;

    public TicketEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Event handler triggered after a ticket purchase transaction is committed.
     * 
     * Checks if all tickets for the event are sold and updates the event status
     * to SOLD_OUT if necessary.
     * 
     * @param ticketPurchasedEvent the event containing information about the ticket purchase
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketPurchased(TicketPurchasedEvent ticketPurchasedEvent) {
        eventService.updateStatusIfSoldOut(ticketPurchasedEvent.getEvent());
    }
}
