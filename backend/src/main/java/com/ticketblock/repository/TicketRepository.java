package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

}
