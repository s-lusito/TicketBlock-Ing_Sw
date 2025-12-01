package com.ticketblock.service;

import com.ticketblock.dto.Request.EventCreationRequest;
import com.ticketblock.dto.Response.EventDto;
import com.ticketblock.entity.*;
import com.ticketblock.entity.enumeration.RowSector;
import com.ticketblock.entity.enumeration.TicketStatus;
import com.ticketblock.exception.ResourceNotFoundException;
import com.ticketblock.exception.VenueNotAvailableException;
import com.ticketblock.mapper.EventMapper;
import com.ticketblock.repository.EventRepository;
import com.ticketblock.repository.UserRepository;
import com.ticketblock.repository.VenueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().
                stream()
                .map(EventMapper::toDto)
                .toList();
    }

    public EventDto getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .map(EventMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:" + eventId + " not found"));
    }

    @Transactional
    public EventDto createEvent(EventCreationRequest eventCreationRequest) {
        Event event = EventMapper.toEntity(eventCreationRequest);
        //verifico che le date e gli orari siano corretti
        verifyDateAndTime(eventCreationRequest);

        //recupero il venue
        Venue venue = venueRepository.findById(eventCreationRequest.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue with id:" + eventCreationRequest.getVenueId() + " not found"));
        event.setVenue(venue);

        //verifico che il venue sia disponibile
        verifyVenueAvailability(venue, eventCreationRequest);

        //recupero l'organizzatore dall'utente loggato
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User organizer = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email:" + email + " not found"));
        event.setOrganizer(organizer);

        //ora creo i ticket dell'evento
        createTickets(venue, event);
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toDto(savedEvent);
    }


    private static void verifyDateAndTime(EventCreationRequest eventCreationRequest) {
        if (eventCreationRequest.getEndTime().isBefore(eventCreationRequest.getStartTime())) {
            throw new IllegalArgumentException("Event end time cannot be before start time");
        }
        if (eventCreationRequest.getDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Event date cannot be in the past");
        }
    }

    private static void verifyVenueAvailability(Venue venue, EventCreationRequest newEvent) {
        for (Event existingEvent : venue.getEvents()) {
            if (existingEvent.getDate().equals(newEvent.getDate())) {
                //se la data coincide, verifico se gli orari si sovrappongono:

                boolean compatible =
                        newEvent.getEndTime().isBefore(existingEvent.getStartTime()) || //l'evento nuovo finisce prima che inizi quello esistente
                                newEvent.getStartTime().isAfter(existingEvent.getEndTime()); //l'evento nuovo inizia dopo che finisce quello esistente

                if (!compatible) {
                    throw new VenueNotAvailableException("Venue is not available at the selected date and time");
                }
            }
        }
    }

    private static void createTickets(Venue venue, Event event) {
        for (Row row : venue.getRows()) {
            RowSector rowSector = row.getSector();
            BigDecimal price = rowSector == RowSector.STANDARD ? event.getStandardTicketPrice() : event.getVipTicketPrice();
            for (Seat seat : row.getSeats()) {
                Ticket ticket = Ticket.builder()
                        .seat(seat)
                        .owner(null) // Nessun proprietario iniziale
                        .resellable(false)
                        .ticketStatus(TicketStatus.AVAILABLE)
                        .price(price)
                        .event(event)
                        .build();
                event.getTickets().add(ticket);
            }
        }
    }

    public EventDto removeEventById(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:" + eventId + " not found"));
        eventRepository.delete(event);
        return EventMapper.toDto(event);
    }

}
