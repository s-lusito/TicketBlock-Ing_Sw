package com.ticketblock.controller;

import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.service.VenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/venues")
@Slf4j
public class VenueController {

    private final VenueService venueService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getVenueById(@PathVariable Integer id) {
        log.debug("Getting venue with id: {}", id);
        VenueDto venue = venueService.getVenueByVenueId(id);
        return ResponseEntity.ok(venue);

    }

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<?> getVenueAvailableSlots(@PathVariable Integer id, @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(venueService.getVenueAvailableSlots(id, date));

    }

    @GetMapping
    public ResponseEntity<?> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

}
