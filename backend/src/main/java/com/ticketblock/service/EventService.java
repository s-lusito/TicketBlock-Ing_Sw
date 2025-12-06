package com.ticketblock.service;

import com.ticketblock.dto.Request.EventCreationRequest;
import com.ticketblock.dto.Response.EventDto;
import com.ticketblock.entity.*;
import com.ticketblock.entity.enumeration.EventSaleStatus;
import com.ticketblock.entity.enumeration.RowSector;
import com.ticketblock.entity.enumeration.TicketStatus;
import com.ticketblock.exception.InvalidDateAndTimeException;
import com.ticketblock.exception.ResourceNotFoundException;
import com.ticketblock.exception.ForbiddenActionException;
import com.ticketblock.exception.VenueNotAvailableException;
import com.ticketblock.mapper.EventMapper;
import com.ticketblock.repository.EventRepository;
import com.ticketblock.repository.VenueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing events in the ticketing system.
 * 
 * This service handles event creation, retrieval, deletion, and automatic
 * status updates. It validates event dates and times, checks venue availability,
 * creates tickets for events, and runs scheduled tasks to update event sale
 * statuses based on dates.
 */
@Service
@RequiredArgsConstructor
public class EventService {

    public static final int DAYS_BETWEEN_SALES_START_AND_EVENT = 3;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SecurityService securityService;

    public List<EventDto> getAllEvents(List<EventSaleStatus> saleStatusList) {
        if( saleStatusList == null || saleStatusList.isEmpty() ) {
            return eventRepository.findAll().
                    stream()
                    .map(EventMapper::toDto)
                    .toList();
        }

        return eventRepository.findAllBySaleStatusIn(saleStatusList).
                stream()
                .map(EventMapper::toDto)
                .toList();
    }

    public EventDto getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .map(EventMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:" + eventId + " not found","Event not found"));
    }

    @Transactional
    public EventDto createEvent(EventCreationRequest eventCreationRequest) {
        Event event = EventMapper.toEntity(eventCreationRequest);
        //verifico che le date e gli orari siano corretti
        verifyDateAndTime(eventCreationRequest);

        //recupero il venue
        Venue venue = venueRepository.findById(eventCreationRequest.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found","Venue with id:" + eventCreationRequest.getVenueId() + " not found"));
        event.setVenue(venue);

        //verifico che il venue sia disponibile
        verifyVenueAvailability(venue, eventCreationRequest);

        //recupero l'organizzatore dall'utente loggato
        User organizer = securityService.getLoggedInUser();
        event.setOrganizer(organizer);

        //ora creo i ticket dell'evento
        createTickets(venue, event);
        if(event.getSaleStartDate().isAfter(LocalDate.now())) {
            event.setSaleStatus(EventSaleStatus.NOT_STARTED);
        } else {
            event.setSaleStatus(EventSaleStatus.ONGOING);
        }
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toDto(savedEvent);
    }


    private static void verifyDateAndTime(EventCreationRequest eventCreationRequest) {
        if (eventCreationRequest.getEndTime().isBefore(eventCreationRequest.getStartTime())) {
            throw new InvalidDateAndTimeException("Event end time cannot be before start time");
        }
        if (eventCreationRequest.getDate().isBefore(LocalDate.now())) {
            throw new InvalidDateAndTimeException("Event date cannot be in the past");
        }

        if(eventCreationRequest.getSaleStartDate().isBefore(LocalDate.now())) {
            throw new InvalidDateAndTimeException("Sale start date cannot be before event date");
        }

        if(eventCreationRequest.getSaleStartDate().isAfter(eventCreationRequest.getDate().minusDays(DAYS_BETWEEN_SALES_START_AND_EVENT))) {
            throw new InvalidDateAndTimeException("Sale start date must be at least 3 days before the event date");
        }
    }

    private void verifyVenueAvailability(Venue venue, EventCreationRequest newEvent) {
        int hourBuffer = 1; //buffer di un'ora tra eventi
        List<Event> events = eventRepository.findAllByDateAndVenue(newEvent.getDate(), venue);
        for (Event existingEvent : events) {
                //se la data coincide, verifico se gli orari si sovrappongono:
                boolean compatible =
                        newEvent.getEndTime().isBefore(existingEvent.getStartTime().minusHours(hourBuffer)) || //l'evento nuovo finisce un'ora prima che inizi quello esistente
                                newEvent.getStartTime().isAfter(existingEvent.getEndTime().plusHours(hourBuffer)); //l'evento nuovo inizia dopo un'ora che finisce quello esistente

                if (!compatible) {
                    throw new VenueNotAvailableException("Venue is not available at the selected date and time");
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
        User loggedUser = securityService.getLoggedInUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:" + eventId + " not found"));

        // verifico che l'utente sia l'organizzatore dell'evento
        if (!loggedUser.equals(event.getOrganizer())) {
            throw new ForbiddenActionException("User is not authorized to delete this event, since is not the organizer", "You are not authorized to delete this event");
        }
        eventRepository.delete(event);
        return EventMapper.toDto(event);
    }


    @Scheduled(cron = "0 0 0 * * *") // ogni mezzanotte
    @Transactional
    public void updateEventsSaleStatus() {
        List<Event> eventsToOpen = eventRepository.findAllToOpenToday();
        // apri vendite degli eventi la cui data di inizio vendita è oggi
        for (Event e : eventsToOpen) {
            e.setSaleStatus(EventSaleStatus.ONGOING);
        }

        // chiudi vendite degli eventi che si tengono domani
        List<Event> eventsToClose = eventRepository.findAllByDate(LocalDate.now().plusDays(1));

        for (Event e : eventsToClose) {
            e.setSaleStatus(EventSaleStatus.ENDED);
        }
        eventRepository.saveAll(eventsToClose);
        eventRepository.saveAll(eventsToOpen);
    }



    @Transactional
    public void updateStatusIfSoldOut(Event event) {
        boolean allSold = event.getTickets().stream()
                .allMatch(ticket -> ticket.getTicketStatus().equals(TicketStatus.SOLD));
        if (allSold) {
            event.setSaleStatus(EventSaleStatus.SOLD_OUT);
            eventRepository.save(event);
        }
    }


}
