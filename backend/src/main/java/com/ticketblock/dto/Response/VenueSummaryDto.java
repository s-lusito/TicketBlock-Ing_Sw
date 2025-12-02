package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueSummaryDto {
    private Integer id;
    private String name;
    private AddressDto address;
}
