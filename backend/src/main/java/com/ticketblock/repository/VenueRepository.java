package com.ticketblock.repository;

import com.ticketblock.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Venue entity database operations.
 * 
 * Provides standard CRUD operations through JpaRepository for managing
 * venue data including associated rows, seats, and address information.
 */
public interface VenueRepository extends JpaRepository<Venue, Integer> {

}
