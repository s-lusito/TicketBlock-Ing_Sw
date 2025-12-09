package com.ticketblock.dto.Request;

import com.ticketblock.utils.TimeSlot;
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
