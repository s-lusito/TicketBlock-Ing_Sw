package com.ticketblock.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(nullable = false)
    private String eventName;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User organizer;


    @Column(name = "local_date", columnDefinition = "DATE")
    private LocalDate localDate;

    @Column(name = "local_time", columnDefinition = "TIME")
    private LocalTime localTime;



    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(nullable = false, updatable = false)
    private Venue venue;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "event")
    private Set<Ticket> tickets;

    @Column(nullable = false, updatable = false) //dopo aver scelto il prezzo non si può cambiare
    private BigDecimal standardTicketPrice; //big decimal è preferito a float o double essendo più preciso

    @Column(nullable = false, updatable = false) //dopo aver scelto il prezzo non si può cambiare
    private BigDecimal vipTicketPrice;

}


