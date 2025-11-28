package com.ticketblock.entity;

import com.ticketblock.entity.enumeration.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id","seat_id"}) //ogni ticket deve aver euna combinazione unica di evento e posto fisico
)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn
    private Event event;

    @ManyToOne()
    @JoinColumn()
    private Seat seat;

    @ManyToOne()
    @JoinColumn()
    private User owner;

    @ManyToOne()
    @JoinColumn()
    private User organizer;

    @Column(nullable = false)
    private Boolean resellable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus ticketStatus = TicketStatus.SELLING;






}
