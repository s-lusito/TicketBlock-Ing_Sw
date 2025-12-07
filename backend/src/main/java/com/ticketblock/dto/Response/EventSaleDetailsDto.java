package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EventSaleDetailsDto {
    private EventDto event;
    private Integer standardTicketsSold;
    private Integer vipTicketsSold;
    private BigDecimal totalSales;

}
