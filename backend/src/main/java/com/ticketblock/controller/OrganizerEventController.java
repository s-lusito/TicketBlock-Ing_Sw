package com.ticketblock.controller;

import com.ticketblock.dto.Request.EventCreationRequest;
import com.ticketblock.dto.Response.EventDto;
import com.ticketblock.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organizer/events")
@Slf4j
public class OrganizerEventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventCreationRequest eventCreationRequest) {
        EventDto createdEvent = eventService.createEvent(eventCreationRequest);
        return ResponseEntity.ok(createdEvent);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer id) {
        log.debug("User wants to delete event with id " + id);
        eventService.removeEventById(id);
        log.debug("Event with id " + id + " has been deleted");
        return ResponseEntity.noContent().build();
    }
}
