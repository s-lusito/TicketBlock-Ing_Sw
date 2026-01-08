package com.ticketblock.applicationEvent;

import com.ticketblock.entity.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class TicketSaleEvent extends ApplicationEvent {
    protected final Event event;

    public TicketSaleEvent(Object source, Event event) {
        super(source);
        this.event = event;
    }
}
