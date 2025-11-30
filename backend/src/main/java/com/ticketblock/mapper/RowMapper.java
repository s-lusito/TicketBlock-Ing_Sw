package com.ticketblock.mapper;

import com.ticketblock.dto.Response.RowDto;
import com.ticketblock.entity.Row;
import com.ticketblock.entity.enumeration.RowSector;

import java.util.List;


public class RowMapper {

    public static RowDto toDto(Row row) {
        return RowDto.builder()
                .id(row.getId())
                .letter(row.getLetter())
                .sector(row.getSector())
                .seats(
                        row.getSeats()
                                .stream()
                                .map(SeatMapper::toDto)
                                .toList()
                )
                .build();
    }
}
