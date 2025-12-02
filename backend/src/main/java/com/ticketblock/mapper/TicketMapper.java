package com.ticketblock.mapper;

import com.ticketblock.dto.Response.TicketDto;
import com.ticketblock.entity.Ticket;

public class TicketMapper {
    public static TicketDto toDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.getId())
                .seat(SeatMapper.toDto(ticket.getSeat()))
                .owner(UserMapper.toDto(ticket.getOwner()))
                .resellable(ticket.getResellable())
                .ticketStatus(ticket.getTicketStatus().name())
                .price(ticket.getPrice())
                .build();
    }
}
