package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Address information in API responses.
 * 
 * This DTO is used to transfer address data including street, city,
 * and state information for venues.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AddressDto {
    private Integer id;
    private String street;
    private String city;
    private String state;


}
