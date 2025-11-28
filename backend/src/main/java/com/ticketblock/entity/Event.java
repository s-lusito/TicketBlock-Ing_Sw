package com.ticketblock.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String eventName;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Venue venue;


}


