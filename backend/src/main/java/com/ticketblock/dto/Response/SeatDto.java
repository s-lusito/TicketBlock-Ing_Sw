package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Seat information in API responses.
 * 
 * This DTO is used to transfer individual seat data including
 * the seat number within a row.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatDto {
    private Integer id;
    private Integer seatNumber;

}
