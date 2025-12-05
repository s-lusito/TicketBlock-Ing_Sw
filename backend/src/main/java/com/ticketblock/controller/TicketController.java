package com.ticketblock.controller;

import com.ticketblock.dto.Request.PurchaseTicketRequest;
import com.ticketblock.dto.Response.PurchaseTicketResponse;
import com.ticketblock.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> resellTickets(@PathVariable Integer id) {
        ticketService.resellTicket(id);
        return ResponseEntity.ok().build();
    }







}
