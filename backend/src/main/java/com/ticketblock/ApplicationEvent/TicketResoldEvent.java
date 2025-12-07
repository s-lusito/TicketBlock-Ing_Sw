package com.ticketblock.ApplicationEvent;

import com.ticketblock.entity.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TicketResoldEvent extends ApplicationEvent {
    private final Event event;

    public TicketResoldEvent(Object source, Event event) {
        super(source);
        this.event = event;
    }

}
