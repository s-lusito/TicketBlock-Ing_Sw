package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for complete Venue information in API responses.
 * 
 * This DTO is used to transfer full venue data including name, address,
 * and complete row/seat layout information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueDto {
    private Integer id;
    private String name;
    private AddressDto address;
    private List<RowDto> rows;
}
