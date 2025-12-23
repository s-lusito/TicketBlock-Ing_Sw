package com.ticketblock.controller;

import com.ticketblock.dto.Request.PurchaseTicketRequest;
import com.ticketblock.dto.Response.PurchaseTicketResponse;
import com.ticketblock.dto.Response.TicketDto;
import com.ticketblock.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;


    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTickets(@RequestBody @Valid PurchaseTicketRequest purchaseTicketRequests) {
        PurchaseTicketResponse purchaseResponse = ticketService.purchaseTickets(purchaseTicketRequests);
        return ResponseEntity.ok(purchaseResponse);
    }

    @PostMapping("/{id}/resell")
    public ResponseEntity<?> resellTicket(@PathVariable Integer id) {
        ticketService.resellTicket(id);
        return ResponseEntity.ok().build();
    }
    // restituisce i biglietti dell'utente loggato
    @GetMapping("/mine")
    public ResponseEntity<?> getMineTickets() {
        List<TicketDto> userTickets = ticketService.getLoggedUserTickets();
        return ResponseEntity.ok(userTickets);

    }

    @PostMapping("/{id}/invalidate")
    public ResponseEntity<?> invalidateTicket(@PathVariable Integer id) {
        ticketService.invalidateTicket(id);
        return ResponseEntity.ok().build();
    }





}
