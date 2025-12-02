package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
