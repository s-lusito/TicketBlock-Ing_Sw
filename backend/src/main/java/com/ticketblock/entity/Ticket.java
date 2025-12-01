package com.ticketblock.entity;

import com.ticketblock.entity.enumeration.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id","seat_id"}) //ogni ticket deve aver euna combinazione unica di evento e posto fisico
)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
    

    @Column(nullable = false)
    private Boolean resellable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus ticketStatus = TicketStatus.SELLING;






}
