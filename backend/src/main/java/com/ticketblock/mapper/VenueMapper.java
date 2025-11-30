package com.ticketblock.mapper;

import com.ticketblock.dto.Response.RowDto;
import com.ticketblock.dto.Response.SeatDto;
import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.entity.Row;
import com.ticketblock.entity.Venue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VenueMapper {

    public static VenueDto toDto(Venue venue) {
        return VenueDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(AddressMapper.toDto(venue.getAddress()))
                .rows(
                        venue.getRows()
                                .stream()
                                .map(RowMapper::toDto)
                                .toList()
                )
                .build();

    }



}
