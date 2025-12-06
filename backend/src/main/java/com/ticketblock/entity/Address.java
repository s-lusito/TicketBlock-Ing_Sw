package com.ticketblock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a physical address for a venue.
 * 
 * This entity stores the location details of a venue including
 * street, city, and state information.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;

    @OneToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

}