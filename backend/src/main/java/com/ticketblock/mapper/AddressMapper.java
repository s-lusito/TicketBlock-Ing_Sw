package com.ticketblock.mapper;

import com.ticketblock.dto.Response.AddressDto;
import com.ticketblock.entity.Address;

/**
 * Mapper class for converting Address entities to DTOs.
 * 
 * Provides static methods to transform Address domain objects into
 * AddressDto data transfer objects for API responses.
 */
public class AddressMapper {
    public static AddressDto toDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .build();
    }
}
