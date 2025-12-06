package com.ticketblock.entity;

import com.ticketblock.entity.enumeration.EventSaleStatus;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User organizer;


    @Column(columnDefinition = "DATE")
    private LocalDate date;

    @Column(columnDefinition = "TIME")
    private LocalTime startTime;

    @Column(columnDefinition = "TIME")
    private LocalTime endTime;


    @Enumerated(EnumType.STRING)
    private EventSaleStatus saleStatus;

    @Column(columnDefinition = "DATE")
    private LocalDate saleStartDate;



    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(nullable = false, updatable = false)
    private Venue venue;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "event")
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @Column(nullable = false, updatable = false) //dopo aver scelto il prezzo non si può cambiare
    private BigDecimal standardTicketPrice; //big decimal è preferito a float o double essendo più preciso

    @Column(nullable = false, updatable = false) //dopo aver scelto il prezzo non si può cambiare
    private BigDecimal vipTicketPrice;

    @PostConstruct
    public void init() {
        this.tickets = new ArrayList<>();
    }
}
