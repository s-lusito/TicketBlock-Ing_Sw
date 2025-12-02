package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
