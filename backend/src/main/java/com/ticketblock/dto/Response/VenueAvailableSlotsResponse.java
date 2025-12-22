package com.ticketblock.dto.Response;

import com.ticketblock.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class VenueAvailableSlotsResponse {
    private HashMap<Integer, Boolean> slot;
}
