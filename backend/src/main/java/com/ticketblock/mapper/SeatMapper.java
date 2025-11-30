package com.ticketblock.mapper;

import com.ticketblock.dto.Response.SeatDto;
import com.ticketblock.entity.Seat;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SeatMapper {

    public static SeatDto toDto(Seat seat) {
        return SeatDto.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .build();
    }


}


