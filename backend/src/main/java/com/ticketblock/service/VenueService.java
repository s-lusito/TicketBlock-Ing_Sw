package com.ticketblock.service;

import com.ticketblock.dto.Response.VenueAvailableSlotsResponse;
import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.entity.Event;
import com.ticketblock.exception.ResourceNotFoundException;
import com.ticketblock.mapper.VenueMapper;
import com.ticketblock.repository.EventRepository;
import com.ticketblock.repository.VenueRepository;
import com.ticketblock.utils.TimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    public VenueDto getVenueByVenueId(Integer venueId) {
         return venueRepository.findById(venueId)
                 .map(VenueMapper::toDto)
                 .orElseThrow(() -> new ResourceNotFoundException("Venue with id " + venueId + " not found"));
         // poichè viene restituito un optional, attraverso l'interfaccia di optional posso richiamare
        // un il metodo orElseThrow che accetta una funzione che crea l'eccezione da lanciare

    }

    public VenueAvailableSlotsResponse getVenueAvailableSlots(Integer venueId, LocalDate date) {
        return  new VenueAvailableSlotsResponse(getAvailableSlots(venueId,date));
    }


    public Boolean[] getAvailableSlots(Integer venueId, LocalDate date) {
        List<Event> eventList = eventRepository.findAllByDateAndVenueId(date,venueId);
        Boolean[] availableSlots = new Boolean[TimeSlot.getLastSlot().getIndex() +1 ];
        Arrays.fill(availableSlots, true);

        for (Event event : eventList){
            TimeSlot startSlot = TimeSlot.fromTime(event.getStartTime());
            TimeSlot endSlot = TimeSlot.fromTime(event.getEndTime());
            for (int i = startSlot.getIndex(); i <= endSlot.getIndex(); i++) {
                availableSlots[i] = false; //slot occupato
            }

        }
        return availableSlots;
    }


    public boolean isVenueAvailable(Integer venueId, LocalDate date, TimeSlot startSlot, Integer duration) {
        Boolean[] availableSlots = getAvailableSlots(venueId, date);
        TimeSlot endSlot = startSlot.plus(duration);
        for ( int i = startSlot.getIndex(); i <= endSlot.getIndex(); i++) {
            if (availableSlots[i] == false) return false;
        }
        return true;
    }


}
