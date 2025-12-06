package com.ticketblock.repository;

import com.ticketblock.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Seat entity database operations.
 * 
 * Provides standard CRUD operations through JpaRepository for managing
 * seat data within venue rows.
 */
public interface SeatRepository extends JpaRepository<Seat, Integer> {

}
