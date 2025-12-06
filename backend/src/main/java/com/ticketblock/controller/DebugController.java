package com.ticketblock.controller;

import com.ticketblock.dto.Response.AuthenticationResponse;
import com.ticketblock.entity.Event;
import com.ticketblock.entity.enumeration.EventSaleStatus;
import com.ticketblock.repository.EventRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/debug")
public class DebugController {
    private final EventRepository eventRepository;

    // This endpoint is for debug purposes only
    @PostMapping("/open-sale")
    public void openSale(){
        List<Event> events = eventRepository.findAll();
        for(Event event : events){
            event.setSaleStatus(EventSaleStatus.ONGOING);
        }
        eventRepository.saveAll(events);
    }




}
