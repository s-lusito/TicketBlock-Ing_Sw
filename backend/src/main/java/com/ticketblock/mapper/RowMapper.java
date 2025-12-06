package com.ticketblock.mapper;

import com.ticketblock.dto.Response.RowDto;
import com.ticketblock.entity.Row;

/**
 * Mapper class for converting Row entities to DTOs.
 * 
 * Provides static methods to transform Row domain objects into
 * RowDto data transfer objects for API responses.
 */
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
