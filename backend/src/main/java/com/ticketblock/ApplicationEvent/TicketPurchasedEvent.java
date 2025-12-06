package com.ticketblock.ApplicationEvent;

import com.ticketblock.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
public class TicketPurchasedEvent extends ApplicationEvent {
    private final Event event;

    public TicketPurchasedEvent(Object source, Event event) {
        super(source);
        this.event = event;
    }

}
