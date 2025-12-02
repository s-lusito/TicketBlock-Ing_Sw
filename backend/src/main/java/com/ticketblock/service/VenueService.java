package com.ticketblock.service;

import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.entity.Venue;
import com.ticketblock.exception.ResourceNotFoundException;
import com.ticketblock.repository.VenueRepository;
import com.ticketblock.mapper.VenueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
