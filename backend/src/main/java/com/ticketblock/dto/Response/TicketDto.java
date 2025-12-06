package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Ticket information in API responses.
 * 
 * This DTO is used to transfer ticket data including seat information,
 * owner details, price, resellability status, and ticket status.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketDto {

    private Integer id;
    private SeatDto seat;
    private UserDto owner;
    private BigDecimal price;
    private Boolean resellable;
    private String ticketStatus;

}
