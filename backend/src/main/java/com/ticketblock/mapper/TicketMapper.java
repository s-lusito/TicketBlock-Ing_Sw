package com.ticketblock.mapper;

import com.ticketblock.dto.Response.TicketDto;
import com.ticketblock.entity.Ticket;

/**
 * Mapper class for converting Ticket entities to DTOs.
 * 
 * Provides static methods to transform Ticket domain objects into
 * TicketDto data transfer objects for API responses.
 */
public class TicketMapper {
    public static TicketDto toDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.getId())
                .seat(SeatMapper.toDto(ticket.getSeat()))
                .owner(ticket.getOwner() != null ? UserMapper.toDto(ticket.getOwner()) : null)
                .resellable(ticket.getResellable())
                .ticketStatus(ticket.getTicketStatus().name())
                .price(ticket.getPrice())
                .build();
    }
}
