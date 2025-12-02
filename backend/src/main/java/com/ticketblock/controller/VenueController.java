package com.ticketblock.controller;

import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/venues")
public class VenueController {

    private final VenueService venueService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getVenueById(@PathVariable Integer id) {
        VenueDto venue = venueService.getVenueByVenueId(id);
        return ResponseEntity.ok(venue);

    }



}
