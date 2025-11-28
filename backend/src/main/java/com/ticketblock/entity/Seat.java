package com.ticketblock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"row_id","seat_number"})
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    private String seatNumber;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Row row;
}