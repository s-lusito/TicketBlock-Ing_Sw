package com.ticketblock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"row_id","seat_number"})
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private Integer seatNumber;

    @ManyToOne (fetch = FetchType.LAZY) //non carica anche la row dal db finchè non serve, evita caricamenti inutili
    @JoinColumn(nullable = false, name = "row_id")
    @ToString.Exclude//questo evita di dover richiamere a loro volta gli altri metodi tostring a catena
    private Row row;

    @OneToMany(mappedBy = "seat")
    private Set<Ticket> tickets;
}