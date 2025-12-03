package com.ticketblock.service;

import com.ticketblock.dto.Request.PurchaseTicketRequest;
import com.ticketblock.dto.Response.PurchaseTicketResponse;
import com.ticketblock.dto.Response.TicketDto;
import com.ticketblock.entity.Ticket;
import com.ticketblock.entity.User;
import com.ticketblock.entity.enumeration.RowSector;
import com.ticketblock.entity.enumeration.TicketStatus;
import com.ticketblock.exception.ResourceNotFoundException;
import com.ticketblock.exception.UnauthorizedActionException;
import com.ticketblock.mapper.TicketMapper;
import com.ticketblock.repository.TicketRepository;
import com.ticketblock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final BigDecimal feePercentage = new BigDecimal("1.10");
    private final TicketRepository ticketRepository;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    public List<TicketDto> getTicketsFromEvent(Integer eventId, TicketStatus ticketStatus, RowSector rowSector) {
        return ticketRepository.findByEventId(eventId, ticketStatus, rowSector).stream().map(TicketMapper::toDto).toList();
    }

    @Transactional
    public PurchaseTicketResponse purchaseTickets(PurchaseTicketRequest ticketsRequested) {
        User loggedUser = securityService.getLoggedInUser();
        Map<Integer, Boolean> ticketFeeMap = ticketsRequested.getTicketFeeMap(); // la mappa contiene ticketId e se accetta la fee
        // recupero gli ids
        List<Integer> ticketIds = ticketFeeMap.keySet().stream().toList();

        List<Ticket> tickets = ticketRepository.findAllByIdIn(ticketIds);
        if (tickets.size() != ticketIds.size()) { // controllo che tutti i ticket siano stati trovati
            throw new ResourceNotFoundException("One or more tickets not found for the provided ids");
        }

        BigDecimal totalPrice = new BigDecimal("0");
        for (Ticket ticket : tickets) {
            if (ticket.getTicketStatus() != TicketStatus.AVAILABLE) {
                throw new UnauthorizedActionException("One or more tickets are not available for purchase");
            }
            if (ticketFeeMap.get(ticket.getId())) { // accetta la fee
                totalPrice = totalPrice.add(ticket.getPrice().multiply(feePercentage)); // aggiungo la fee al prezzo
                ticket.setResellable(true); // imposto resellable a true se accetta la fee
            } else {
                totalPrice = totalPrice.add(ticket.getPrice());
                ticket.setResellable(false);
            }
            ticket.setTicketStatus(TicketStatus.SOLD); // imposto lo stato a SOLD
            ticket.setOwner(loggedUser); // imposto il proprietario
            ticketRepository.save(ticket);
        }

        // Gestione del pagamento (simulata)
        if (managePayment(ticketsRequested.getCreditCardNumber(),
                ticketsRequested.getExpirationDate(),
                ticketsRequested.getCvv(),
                ticketsRequested.getCardHolderName(),
                totalPrice))
        {
            return PurchaseTicketResponse.builder()
                    .success(true)
                    .message(String.format("Purchase successful! Total amount charged: %s", totalPrice))
                    .build();
        } else {
            return PurchaseTicketResponse.builder()
                    .success(false)
                    .message("Payment failed. Please check your payment details and try again.")
                    .build();
        }

    }




    private static boolean managePayment(String creditCardNumber, String expirationDate, String cvv, String cardHolderName, BigDecimal amount) {
        // Simula la gestione del pagamento
        return true; // Supponiamo che il pagamento sia sempre riuscito
    }
}
