package com.ticketblock.entity;

import com.ticketblock.entity.enumeration.RowSector;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
