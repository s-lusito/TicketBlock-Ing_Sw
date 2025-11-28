package com.ticketblock.entity;

import jakarta.persistence.*;

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


