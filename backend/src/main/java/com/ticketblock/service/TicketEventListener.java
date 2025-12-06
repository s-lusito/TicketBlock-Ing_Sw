package com.ticketblock.service;

import com.ticketblock.ApplicationEvent.TicketPurchasedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Questo consente di creare una nuova transazione diversa rispetto a quella originale
    public void onTicketPurchased(TicketPurchasedEvent ticketPurchasedEvent) {
        eventService.updateStatusIfSoldOut(ticketPurchasedEvent.getEvent());
    }
}
