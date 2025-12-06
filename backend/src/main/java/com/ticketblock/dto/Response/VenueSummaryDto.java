package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for summarized Venue information in API responses.
 * 
 * This DTO is used to transfer basic venue data (name and address) without
 * the complete row and seat layout, useful for event listings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueSummaryDto {
    private Integer id;
    private String name;
    private AddressDto address;
}
