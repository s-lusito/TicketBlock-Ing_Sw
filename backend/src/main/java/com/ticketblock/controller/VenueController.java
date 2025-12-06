package com.ticketblock.controller;

import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.service.VenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
