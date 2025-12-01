package com.ticketblock.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    private Integer id;
    private String name;
    private UserDto organizer;
    private LocalDate localDate;
    private LocalTime localTime;
    private VenueDto venue;
    private BigDecimal standardTicketPrice;
    private BigDecimal vipTicketPrice;
}

