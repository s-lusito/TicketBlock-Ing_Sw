package com.ticketblock.ApplicationEvent;

import com.ticketblock.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

public class TicketPurchasedEvent extends TicketSaleEvent {

    public TicketPurchasedEvent(Object source, Event event) {
        super(source,event);
    }

}
