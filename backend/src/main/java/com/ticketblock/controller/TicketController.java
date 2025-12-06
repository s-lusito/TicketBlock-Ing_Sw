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

/**
 * REST controller for managing tickets.
 * 
 * This controller provides endpoints for purchasing tickets, reselling tickets,
 * and retrieving tickets owned by the currently authenticated user.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;


    /**
     * Purchases tickets for an event.
     * 
     * Requires USER or ADMIN role. Maximum 4 tickets per user per event.
     * 
     * @param purchaseTicketRequests the purchase request containing ticket IDs, fee preferences, and payment info
     * @return ResponseEntity containing PurchaseTicketResponse with success status and total amount
     * @throws com.ticketblock.exception.ResourceNotFoundException if tickets are not found
     * @throws com.ticketblock.exception.UnavailableTicketException if tickets are not available
     * @throws com.ticketblock.exception.ForbiddenActionException if user exceeds 4-ticket limit
     * @throws com.ticketblock.exception.FailedPaymentException if payment processing fails
     */
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTickets(@RequestBody @Valid PurchaseTicketRequest purchaseTicketRequests) {
        PurchaseTicketResponse purchaseResponse = ticketService.purchaseTickets(purchaseTicketRequests);
        return ResponseEntity.ok(purchaseResponse);
    }

    /**
     * Resells a ticket back to the marketplace.
     * 
     * Only resellable tickets (purchased with resale fee) can be resold.
     * Requires USER or ADMIN role and ticket ownership.
     * 
     * @param id the ID of the ticket to resell
     * @return ResponseEntity with no content (200)
     * @throws com.ticketblock.exception.ResourceNotFoundException if ticket is not found
     * @throws com.ticketblock.exception.ForbiddenActionException if user doesn't own the ticket
     * @throws com.ticketblock.exception.NonResellableTicketException if ticket is not resellable
     */
    @PostMapping("/{id}/resell")
    public ResponseEntity<?> resellTicket(@PathVariable Integer id) {
        ticketService.resellTicket(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves all tickets owned by the currently logged-in user.
     * 
     * Requires USER or ADMIN role.
     * 
     * @return ResponseEntity containing list of TicketDto objects owned by the user
     */
    @GetMapping("/mine")
    public ResponseEntity<?> getMineTickets() {
        List<TicketDto> userTickets = ticketService.getLoggedUserTickets();
        return ResponseEntity.ok(userTickets);

    }





}
