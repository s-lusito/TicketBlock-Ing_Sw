package com.ticketblock.repository;

import com.ticketblock.entity.Event;
import com.ticketblock.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

}
