package com.ticketblock.mapper;

import com.ticketblock.dto.Response.AddressDto;
import com.ticketblock.entity.Address;

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
