package com.ticketblock.dto.Request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EventCreationRequest {
    @NotBlank
    @Size(max = 50, message = "Event name must not exceed 50 characters")
    private String name;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;

    private String imageUrl;

    @NotNull
    private Integer venueId;
    @Positive
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Standard ticket price must be greater than zero")
    private BigDecimal standardTicketPrice;
    @Positive
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Vip ticket price must be greater than zero")
    private BigDecimal vipTicketPrice;

}
