package com.ticketblock.ApplicationEvent;

import com.ticketblock.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

/**
 * Application event triggered when tickets are successfully purchased.
 * 
 * This event is published after a successful ticket purchase transaction
 * and is used to trigger post-purchase actions such as checking if the
 * event has sold out.
 */
@Getter
public class TicketPurchasedEvent extends ApplicationEvent {
    private final Event event;

    public TicketPurchasedEvent(Object source, Event event) {
        super(source);
        this.event = event;
    }

}
