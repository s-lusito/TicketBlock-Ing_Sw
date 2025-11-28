package com.ticketblock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"venue_id","row_Letter"})
)
public class Row {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String rowLetter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RowSector rowSector = RowSector.STANDARD;

    @ManyToOne// molte file associate a una venue
    private Venue venue;

    @Column(nullable = false)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "row")
    private List<Seat> seats;


}
