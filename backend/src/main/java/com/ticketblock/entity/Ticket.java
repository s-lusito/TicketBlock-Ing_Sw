package com.ticketblock.entity;

import com.ticketblock.entity.enumeration.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id","seat_id"}) //ogni ticket deve aver una combinazione unica di evento e posto fisico
)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne()
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne()
    @JoinColumn()
    private User owner;
    

    @Column(nullable = false)
    private Boolean resellable;

    // updatable = false garantisce l'immutabilità del prezzo
    // Una volta salvato nel DB, non potrà più essere modificato
    @Column(nullable = false, updatable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus ticketStatus = TicketStatus.AVAILABLE;






}
