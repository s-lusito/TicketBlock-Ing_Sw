package com.ticketblock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor //obbligatorio per un entity jpa
@Builder
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @Column(nullable = false)
    private String venueName;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "venue")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venue")
    private List<Row> rows;



}