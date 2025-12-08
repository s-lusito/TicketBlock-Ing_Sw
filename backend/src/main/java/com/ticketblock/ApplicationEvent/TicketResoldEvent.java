package com.ticketblock.ApplicationEvent;

import com.ticketblock.entity.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class TicketResoldEvent extends TicketSaleEvent {

    public TicketResoldEvent(Object source, Event event) {
        super(source, event);
    }


}
