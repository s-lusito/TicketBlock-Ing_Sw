package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.Venue;
import com.ticketblock.entity.enumeration.EventSaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByDateAndVenue(LocalDate date, Venue venue);

    @Query("SELECT e FROM Event e WHERE e.saleStatus = 'NOT_STARTED' AND e.saleStartDate = CURRENT_DATE")
    List<Event> findAllToOpenToday();

    @Query("SELECT e FROM Event e WHERE e.date = CURRENT_DATE + 1")
    List<Event> findAllTomorrow();


    List<Event> findAllBySaleStatusIn(List<EventSaleStatus> saleStatusList);
}
