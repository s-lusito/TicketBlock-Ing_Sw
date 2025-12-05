package com.ticketblock.service;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.enumeration.TicketStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TicketEventListener {

    private final EventService eventService;

    public TicketEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void onTicketPurchased(Event event) {
        eventService.updateStatusIfSoldOut(event );
    }
}
