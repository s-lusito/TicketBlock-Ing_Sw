package com.ticketblock.service;

import com.ticketblock.dto.Request.EventCreationRequest;
import com.ticketblock.dto.Response.EventDto;
import com.ticketblock.dto.Response.EventSaleDetailsDto;
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
import com.ticketblock.repository.TicketRepository;
import com.ticketblock.repository.VenueRepository;
import com.ticketblock.utils.MoneyHelper;
import com.ticketblock.utils.TimeSlot;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    public static final int DAYS_BETWEEN_SALES_START_AND_EVENT = 3;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SecurityService securityService;
    private final TicketRepository ticketRepository;
    private final VenueService venueService;

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
        verifyDateAndTime(event);

        //recupero il venue
        Venue venue = venueRepository.findById(eventCreationRequest.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found","Venue with id:" + eventCreationRequest.getVenueId() + " not found"));
        event.setVenue(venue);



        //verifico che il venue sia disponibile
        if(!venueService.isVenueAvailable(
                venue.getId(),
                eventCreationRequest.getDate(),
                TimeSlot.fromIndexOrThrow(eventCreationRequest.getStartSlot()),
                eventCreationRequest.getDuration())) {
            throw new VenueNotAvailableException("Venue not available at selected time");
        }


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


    private static void verifyDateAndTime(Event event) {
//        if (event.getEndTime().isBefore(event.getStartTime())) { // non più possibile usando il sistema con slot
//            throw new InvalidDateAndTimeException("Event end time cannot be before start time");
//        }
        if (event.getDate().isBefore(LocalDate.now())) {
            throw new InvalidDateAndTimeException("Event date cannot be in the past");
        }

        if(event.getSaleStartDate().isBefore(LocalDate.now())) {
            throw new InvalidDateAndTimeException("Sale start date cannot be in the past");
        }

        if(event.getSaleStartDate().isAfter(event.getDate().minusDays(DAYS_BETWEEN_SALES_START_AND_EVENT))) {
            throw new InvalidDateAndTimeException("Sale start date must be at least 3 days before the event date");
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
        Event retrivedEvent = eventRepository.findById(event.getId()).orElseThrow(() -> new ResourceNotFoundException("Event with id:" + event.getId() + " not found"));
        boolean allSold = retrivedEvent.getTickets().stream()
                .allMatch(ticket -> ticket.getTicketStatus().equals(TicketStatus.SOLD));
        if (allSold) {
            retrivedEvent.setSaleStatus(EventSaleStatus.SOLD_OUT);
            log.debug("Update status of event with id " + event.getId() + " to " + retrivedEvent.getSaleStatus());
        }
        else if (retrivedEvent.getSaleStatus().equals(EventSaleStatus.SOLD_OUT)) {
            retrivedEvent.setSaleStatus(EventSaleStatus.ONGOING);
            log.debug("Update status of event with id " + event.getId() + " to " + retrivedEvent.getSaleStatus());

        }
            eventRepository.save(retrivedEvent);
    }


    public List<EventSaleDetailsDto> getLoggedOrganizerAllEventsDetails() {
        List<EventSaleDetailsDto> eventSaleDetailsDtos = new ArrayList<>();
        User loggedUser = securityService.getLoggedInUser();
        List<Event> events = eventRepository.findAllByOrganizer(loggedUser);
        for (Event e : events){
            EventSaleDetailsDto eventSaleDetailsDto = getEventSaleDetails(e);
            eventSaleDetailsDtos.add(eventSaleDetailsDto);
        }

        return eventSaleDetailsDtos;

    }

    public EventSaleDetailsDto getLoggedOrganizerEventDetails(int eventId) {
        User loggedUser = securityService.getLoggedInUser();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event with id:" + eventId + " not found"));
        return getEventSaleDetails(event);
    }


    private EventSaleDetailsDto getEventSaleDetails(Event e) {
        Integer standardTicketsSold = ticketRepository.countSoldTicketsByEventAndPrice(e, e.getStandardTicketPrice());
        Integer vipTicketsSold = ticketRepository.countSoldTicketsByEventAndPrice(e, e.getVipTicketPrice());

        BigDecimal totalSales = getTotalSales(e, standardTicketsSold, vipTicketsSold);

        return EventSaleDetailsDto.builder()
                .event(EventMapper.toDto(e))
                .standardTicketsSold(standardTicketsSold)
                .vipTicketsSold(vipTicketsSold)
                .totalSales(totalSales)
                .build();
    }




    private static BigDecimal getTotalSales(Event e, int standardTicketsSold, int vipTicketsSold) {
        return MoneyHelper.normalizeAmount(
                e.getStandardTicketPrice().multiply(BigDecimal.valueOf(standardTicketsSold))
                        .add(
                                e.getVipTicketPrice().multiply(BigDecimal.valueOf(vipTicketsSold))));
    }
}
