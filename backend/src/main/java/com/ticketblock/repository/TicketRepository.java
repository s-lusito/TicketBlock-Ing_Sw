package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.Ticket;
import com.ticketblock.entity.User;
import com.ticketblock.entity.enumeration.RowSector;
import com.ticketblock.entity.enumeration.TicketStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {


    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId " +
            "AND (:ticketStatus IS NULL OR t.ticketStatus = :ticketStatus)")
    List<Ticket> findByEventIdAndOptionalTicketStatus(@Param("eventId") Integer eventId,
                                                      @Param("ticketStatus") TicketStatus ticketStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // serve per bloccare letture e scritture sui record selezionati
    List<Ticket> findAllByIdIn(List<Integer> ids);

    boolean findAllByOwnerAndEvent(User owner, Event event);

    int countAllByOwnerAndEvent(User owner, Event event);
}
