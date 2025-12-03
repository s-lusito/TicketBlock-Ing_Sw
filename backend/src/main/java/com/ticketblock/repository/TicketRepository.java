package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.Ticket;
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

//    @Query("SELECT t FROM Ticket t " +
//            "JOIN t.seat s " +
//            "JOIN s.row r " + // In jpql specifica il nome del campo legato alla relazione, non si fa come in sql normale
//            "WHERE t.event.id = :eventId " +
//            "AND (:ticketStatus IS NULL OR t.ticketStatus = :ticketStatus) " + // il filtro è opzionale perciò se è null lo ignoro
//            "AND (:ticketSector IS NULL OR r.sector = :ticketSector)") // il filtro è opzionale perciò se è null lo ignoro //TODO valutare di mettere il rowSector nel ticket direttamente per evitare questo join
//    List<Ticket> findByEventId(@Param("eventId") Integer eventId,
//                               @Param("ticketStatus") TicketStatus ticketStatus,
//                               @Param("ticketSector") RowSector ticketSector);

    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId " +
            "AND (:ticketStatus IS NULL OR t.ticketStatus = :ticketStatus)")
    List<Ticket> findByEventIdAndOptionalTicketStatus(@Param("eventId") Integer eventId,
                                                      @Param("ticketStatus") TicketStatus ticketStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // serve per bloccare letture e scritture sui record selezionati
    List<Ticket> findAllByIdIn(List<Integer> ids);
}
