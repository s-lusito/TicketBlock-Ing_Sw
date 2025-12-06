package com.ticketblock.controller;

import com.ticketblock.dto.Request.EventCreationRequest;
import com.ticketblock.dto.Response.EventDto;
import com.ticketblock.dto.Response.TicketDto;
import com.ticketblock.entity.enumeration.EventSaleStatus;
import com.ticketblock.entity.enumeration.RowSector;
import com.ticketblock.entity.enumeration.TicketStatus;
import com.ticketblock.service.EventService;
import com.ticketblock.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing events.
 * 
 * This controller provides endpoints for creating, retrieving, and deleting events,
 * as well as retrieving tickets for specific events. It handles event filtering
 * by sale status and ticket filtering by status.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;
    private final TicketService ticketService;

    /**
     * Retrieves all events, optionally filtered by sale status.
     * 
     * @param saleStatus optional list of sale statuses to filter events (e.g., ONGOING, NOT_STARTED, SOLD_OUT, ENDED)
     * @return ResponseEntity containing list of EventDto objects
     */
    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestParam(required = false) List<EventSaleStatus> saleStatus // Es.  GET /events?status=ONGOING&status=NOT_STARTED
    ) {
        List<EventDto> events = eventService.getAllEvents(saleStatus);
        return ResponseEntity.ok(events);
    }

    /**
     * Retrieves a specific event by its ID.
     * 
     * @param id the ID of the event to retrieve
     * @return ResponseEntity containing EventDto with event details
     * @throws com.ticketblock.exception.ResourceNotFoundException if event is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Integer id) {
        EventDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    /**
     * Creates a new event.
     * 
     * Requires ORGANIZER or ADMIN role. Creates tickets automatically for all seats in the venue.
     * 
     * @param eventCreationRequest the event creation request with all event details
     * @return ResponseEntity containing EventDto with created event details
     * @throws com.ticketblock.exception.InvalidDateAndTimeException if dates or times are invalid
     * @throws com.ticketblock.exception.ResourceNotFoundException if venue is not found
     * @throws com.ticketblock.exception.VenueNotAvailableException if venue is already booked
     */
    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventCreationRequest eventCreationRequest) {
        EventDto createdEvent = eventService.createEvent(eventCreationRequest);
        return ResponseEntity.ok(createdEvent);
    }

    /**
     * Deletes an event by its ID.
     * 
     * Only the event organizer can delete the event. Requires ORGANIZER or ADMIN role.
     * 
     * @param id the ID of the event to delete
     * @return ResponseEntity with no content (204)
     * @throws com.ticketblock.exception.ResourceNotFoundException if event is not found
     * @throws com.ticketblock.exception.ForbiddenActionException if user is not the organizer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer id) {
        eventService.removeEventById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves tickets for a specific event, optionally filtered by status.
     * 
     * @param id the ID of the event
     * @param ticketStatus optional ticket status to filter by (AVAILABLE, SOLD, INVALIDATED)
     * @return ResponseEntity containing list of TicketDto objects
     */
    @GetMapping("{id}/tickets")
    public ResponseEntity<?> getTicketFromEvent(@PathVariable Integer id,
                                                @RequestParam(required = false) TicketStatus ticketStatus
                                                ) {
        List<TicketDto> tickets= ticketService.getTicketsFromEvent(id, ticketStatus);
        return ResponseEntity.ok(tickets);
    }



}
