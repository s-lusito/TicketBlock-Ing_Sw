package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.User;
import com.ticketblock.entity.enumeration.EventSaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {


    @Query("SELECT e FROM Event e WHERE e.saleStatus = 'NOT_STARTED' AND e.saleStartDate = CURRENT_DATE")
    List<Event> findAllToOpenToday();


    List<Event> findAllBySaleStatusIn(List<EventSaleStatus> saleStatusList);

    List<Event> findAllByDate(LocalDate date);

    List<Event> findAllByOrganizer(User loggedUser);

    Integer id(Integer id);

    List<Event> findAllByDateAndVenueId(LocalDate date, Integer venueId);
}
