package com.ticketblock.dto.Response;

import com.ticketblock.entity.enumeration.EventSaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    private Integer id;
    private String name;
    private UserDto organizer;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private EventSaleStatus eventSaleStatus;
    private LocalDate saleStartDate;
    private String imageUrl;
    private VenueSummaryDto venue;
    private BigDecimal standardTicketPrice;
    private BigDecimal vipTicketPrice;
}

