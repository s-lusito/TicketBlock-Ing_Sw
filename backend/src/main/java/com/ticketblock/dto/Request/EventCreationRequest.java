package com.ticketblock.dto.Request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EventCreationRequest {
    @NotBlank
    @Size(max = 50, message = "Event name must not exceed 50 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    @NotNull
    private LocalDate date;
    @PositiveOrZero
    @Min(value = 32, message = "Time slot range is: 8.00(32) - 23.00(92)")
    @Max(value = 92, message = "Time slot range is: 8.00(32) - 23.00(92)")
    @NotNull
    private Integer startTimeSlot;
    @Positive
    private Integer duration;

    private String imageUrl;

    @NotNull
    private LocalDate saleStartDate;

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
