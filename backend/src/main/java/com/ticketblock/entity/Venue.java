package com.ticketblock.entity;

import com.ticketblock.entity.enumeration.RowSector;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Entity representing a venue where events take place.
 * 
 * This entity stores information about a physical location that hosts events.
 * Each venue has an address, rows of seats, and can host multiple events.
 * Provides helper methods to filter rows by sector (STANDARD or VIP).
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor //obbligatorio per un entity jpa
@Builder
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "venue")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venue")
    private List<Row> rows;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venue")
    private List<Event> events;

    public List<Row> getStandardRows(){
        return rows.stream().filter(row -> row.getSector().equals(RowSector.STANDARD)).toList();
    }

    public List<Row> getVipRows(){
        return rows.stream().filter(row -> row.getSector().equals(RowSector.VIP)).toList();
    }

}