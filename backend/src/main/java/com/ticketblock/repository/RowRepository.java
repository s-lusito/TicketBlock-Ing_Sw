package com.ticketblock.repository;

import com.ticketblock.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Row entity database operations.
 * 
 * Provides standard CRUD operations through JpaRepository for managing
 * row data within venues, including associated seats.
 */
public interface RowRepository extends JpaRepository<Row, Integer> {

}
