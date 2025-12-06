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

    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestParam(required = false) List<EventSaleStatus> saleStatus // Es.  GET /events?status=ONGOING&status=NOT_STARTED
    ) {
        List<EventDto> events = eventService.getAllEvents(saleStatus);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Integer id) {
        EventDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventCreationRequest eventCreationRequest) {
        EventDto createdEvent = eventService.createEvent(eventCreationRequest);
        return ResponseEntity.ok(createdEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer id) {
        eventService.removeEventById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}/tickets")
    public ResponseEntity<?> getTicketFromEvent(@PathVariable Integer id,
                                                @RequestParam(required = false) TicketStatus ticketStatus
                                                ) {
        List<TicketDto> tickets= ticketService.getTicketsFromEvent(id, ticketStatus);
        return ResponseEntity.ok(tickets);
    }



}
