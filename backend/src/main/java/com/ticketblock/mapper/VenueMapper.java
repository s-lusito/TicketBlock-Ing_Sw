package com.ticketblock.mapper;

import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.entity.Venue;


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
