package com.ticketblock.dto.Response;

import com.ticketblock.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
