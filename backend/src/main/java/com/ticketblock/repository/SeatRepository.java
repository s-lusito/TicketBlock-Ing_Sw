package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

}
