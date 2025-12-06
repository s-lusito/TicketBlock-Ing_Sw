package com.ticketblock.entity;

import com.ticketblock.entity.enumeration.RowSector;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Entity representing a row of seats in a venue.
 * 
 * This entity represents a row within a venue, identified by a letter (e.g., A, B, C).
 * Each row belongs to a sector (STANDARD or VIP) and contains multiple seats.
 * The combination of venue and letter must be unique.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"venue_id","letter"})
)
public class Row {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private String letter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RowSector sector = RowSector.STANDARD;

    @ManyToOne(fetch = FetchType.LAZY)// molte file associate a una venue
    @JoinColumn(nullable = false)
    @ToString.Exclude
    private Venue venue;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "row")
    private List<Seat> seats;


}
