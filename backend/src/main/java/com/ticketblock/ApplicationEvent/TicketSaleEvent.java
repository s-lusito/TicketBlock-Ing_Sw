package com.ticketblock.ApplicationEvent;

import com.ticketblock.entity.Event;
import org.springframework.context.ApplicationEvent;

public abstract class TicketSaleEvent extends ApplicationEvent {
    protected final Event event;

    public TicketSaleEvent(Object source, Event event) {
        super(source);
        this.event = event;
    }
}
