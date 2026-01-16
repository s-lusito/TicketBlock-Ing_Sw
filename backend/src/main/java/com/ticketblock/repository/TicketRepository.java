package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.Ticket;
import com.ticketblock.entity.User;
import com.ticketblock.entity.enumeration.TicketStatus;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {


    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId " +
            "AND (:ticketStatus IS NULL OR t.ticketStatus = :ticketStatus)")
    List<Ticket> findByEventIdAndOptionalTicketStatus(@Param("eventId") Integer eventId,
                                                      @Param("ticketStatus") TicketStatus ticketStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
        // serve per bloccare letture e scritture sui record selezionati
    List<Ticket> findAllByIdIn(List<Integer> ids);

    boolean findAllByOwnerAndEvent(User owner, Event event);

    int countAllByOwnerAndEvent(User owner, Event event);

    List<Ticket> findAllByOwner(User loggedUser);

    /**
     * Tickets sold by Organizer (Resold tickets do not count) counted by price
     *
     * @param event
     * @return
     */
    @Query("""

            SELECT COUNT(t)
    FROM Ticket t
    WHERE t.event = :event
      AND t.owner IS NOT NULL
      AND t.price = :price
    """)
    Integer countSoldTicketsByEventAndPrice(@Param("event") Event event, @Param("price") BigDecimal price);

    @Query("""
    SELECT COUNT(t)
    FROM Ticket t
    WHERE t.event = :event
      AND t.owner IS NOT NULL
    """)
    Integer countSoldTicketsByEvent(@Param("event") Event event);

    @Modifying
    @Transactional
    @Query("""
     UPDATE Ticket t
    SET t.ticketStatus = com.ticketblock.entity.enumeration.TicketStatus.SOLD
    WHERE t.owner IS NOT NULL AND t.ticketStatus = com.ticketblock.entity.enumeration.TicketStatus.AVAILABLE

"""
)
    int updateAllResellingTicketStatusToSold();

}