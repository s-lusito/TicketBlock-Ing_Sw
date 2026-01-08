package com.ticketblock.applicationEvent;

import com.ticketblock.entity.Event;

public class TicketResoldEvent extends TicketSaleEvent {

    public TicketResoldEvent(Object source, Event event) {
        super(source, event);
    }


}
