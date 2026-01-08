package com.ticketblock.applicationEvent;

import com.ticketblock.entity.Event;

public class TicketPurchasedEvent extends TicketSaleEvent {

    public TicketPurchasedEvent(Object source, Event event) {
        super(source,event);
    }

}
