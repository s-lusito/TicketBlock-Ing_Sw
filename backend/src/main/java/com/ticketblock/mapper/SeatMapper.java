package com.ticketblock.mapper;

import com.ticketblock.dto.Response.SeatDto;
import com.ticketblock.entity.Seat;

/**
 * Mapper class for converting Seat entities to DTOs.
 * 
 * Provides static methods to transform Seat domain objects into
 * SeatDto data transfer objects for API responses.
 */
public class SeatMapper {

    public static SeatDto toDto(Seat seat) {
        return SeatDto.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .build();
    }


}


