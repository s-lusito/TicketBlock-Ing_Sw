package com.ticketblock.service;

import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.exception.ResourceNotFoundException;
import com.ticketblock.mapper.VenueMapper;
import com.ticketblock.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for managing venues in the ticketing system.
 * 
 * This service handles venue-related operations such as retrieving
 * venue information by ID. Venues contain row and seat information
 * needed for event ticket generation.
 */
@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;

    public VenueDto getVenueByVenueId(Integer venueId) {
         return venueRepository.findById(venueId)
                 .map(VenueMapper::toDto)
                 .orElseThrow(() -> new ResourceNotFoundException("Venue with id " + venueId + " not found"));
         // poichè viene restituito un optional, attraverso l'interfaccia di optional posso richiamare
        // un il metodo orElseThrow che accetta una funzione che crea l'eccezione da lanciare

    }



}
