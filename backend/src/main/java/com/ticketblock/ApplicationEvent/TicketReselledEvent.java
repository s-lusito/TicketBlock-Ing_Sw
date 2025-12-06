package com.ticketblock.ApplicationEvent;

import com.ticketblock.entity.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TicketReselledEvent extends ApplicationEvent {
    private final Event event;

    public TicketReselledEvent(Object source, Event event) {
        super(source);
        this.event = event;
    }

}
