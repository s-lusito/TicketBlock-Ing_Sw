package com.ticketblock.mapper;

import com.ticketblock.dto.Response.SeatDto;
import com.ticketblock.entity.Seat;

public class SeatMapper {

    public static SeatDto toDto(Seat seat) {
        return SeatDto.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .build();
    }

}
