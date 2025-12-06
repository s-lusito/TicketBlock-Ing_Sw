package com.ticketblock.mapper;

import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.dto.Response.VenueSummaryDto;
import com.ticketblock.entity.Venue;

/**
 * Mapper class for converting Venue entities to DTOs.
 * 
 * Provides static methods to transform Venue domain objects into
 * VenueDto and VenueSummaryDto data transfer objects for API responses.
 */
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

    public static VenueSummaryDto toSummaryDto(Venue venue) {
        return VenueSummaryDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(AddressMapper.toDto(venue.getAddress()))
                .build();
    }



}
