package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByDateAndVenue(LocalDate date, Venue venue);

}
