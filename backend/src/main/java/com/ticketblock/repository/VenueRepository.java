package com.ticketblock.repository;

import com.ticketblock.entity.Row;
import com.ticketblock.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Integer> {

}
