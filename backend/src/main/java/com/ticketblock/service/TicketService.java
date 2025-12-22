package com.ticketblock.service;

import com.ticketblock.ApplicationEvent.TicketPurchasedEvent;
import com.ticketblock.ApplicationEvent.TicketResoldEvent;
import com.ticketblock.dto.Request.PurchaseTicketRequest;
import com.ticketblock.dto.Response.PurchaseTicketResponse;
import com.ticketblock.dto.Response.TicketDto;
import com.ticketblock.entity.Event;
import com.ticketblock.entity.Ticket;
import com.ticketblock.entity.User;
import com.ticketblock.entity.enumeration.EventSaleStatus;
import com.ticketblock.entity.enumeration.TicketStatus;
import com.ticketblock.exception.*;
import com.ticketblock.mapper.TicketMapper;
import com.ticketblock.repository.TicketRepository;
import com.ticketblock.utils.MoneyHelper;
import com.ticketblock.utils.TicketContract;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final BigDecimal feePercentage = new BigDecimal("1.10");
    private final TicketRepository ticketRepository;
    private final SecurityService securityService;

    private final int MAX_TICKETS_PER_EVENT = 4;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TicketContract ticketContract;

    public List<TicketDto> getTicketsFromEvent(Integer eventId, TicketStatus ticketStatus) {
        return ticketRepository.findByEventIdAndOptionalTicketStatus(eventId, ticketStatus).stream().map(TicketMapper::toDto).toList();
    }

    @Transactional
    public PurchaseTicketResponse purchaseTickets(PurchaseTicketRequest ticketsRequested) {
        User loggedUser = securityService.getLoggedInUser();
        Map<Integer, Boolean> ticketFeeMap = ticketsRequested.getTicketFeeMap(); // la mappa contiene ticketId e se accetta la fee
        // recupero gli ids
        List<Integer> ticketIds = ticketFeeMap.keySet().stream().toList();

        List<Ticket> tickets = ticketRepository.findAllByIdIn(ticketIds);
        if (tickets.isEmpty()) {
            throw new ResourceNotFoundException("No tickets found for the provided ids");
        }


        //recupero l'eventId del primo ticket per confrontarlo con gli altri
        Event event = tickets.getFirst().getEvent();

        if(!event.getSaleStatus().equals(EventSaleStatus.ONGOING)){
            throw new UnavailableTicketException("Tickets for this event are not available for purchase at this time");
        }

        if (tickets.size() != ticketIds.size()) { // controllo che tutti i ticket siano stati trovati
            throw new ResourceNotFoundException("One or more tickets not found for the provided ids");
        }

        verifyTicketOwnershipLimit(loggedUser, event, tickets); // verifica che l'utente non superi il limite di 4 ticket per evento


        //CALCOLO PREZZO E AGGIORNO STATI DEI TICKET, VERIFICANDO LA DISPONIBILITÀ E CHE SIANO TUTTI RELATIVI ALLO STESSO EVENTO

        // Inizializza totalPrice come un BigDecimal pari a zero ma con scala fissa a 2 decimali e con politica di arrotondamento RoundingMode.HALF_UP.
        BigDecimal totalPrice = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (Ticket ticket : tickets) {
            if (ticket.getTicketStatus() != TicketStatus.AVAILABLE || !ticket.getEvent().equals(event)) { // controllo che il ticket sia disponibile e relativo allo stesso evento
                throw new UnavailableTicketException("One or more tickets are not available for purchase");
            }
            if (ticketFeeMap.get(ticket.getId())) { // accetta la fee
                BigDecimal priceWithFee = MoneyHelper.normalizeAmount(ticket.getPrice().multiply(feePercentage));
                totalPrice = totalPrice.add(priceWithFee); // aggiungo la fee al prezzo
                ticket.setResellable(true); // imposto resellable a true se accetta la fee
            } else {
                totalPrice = totalPrice.add(ticket.getPrice());
                ticket.setResellable(false);
            }
            ticket.setTicketStatus(TicketStatus.SOLD); // imposto lo stato a SOLD
            if(ticket.getOwner()==null){ // prima volta che viene acquistato

            }

        }


        // Gestione del pagamento (simulata)
        if (managePayment(ticketsRequested.getCreditCardNumber(),
                ticketsRequested.getExpirationDate(),
                ticketsRequested.getCvv(),
                ticketsRequested.getCardHolderName(),
                totalPrice))
        {
            for (Ticket ticket : tickets) {
                if( ticket.getOwner() ==  null){ // se è la prima volta che viene venduto viene mintato
                    try {
                        BigInteger blockchainTicketId = ticketContract.mintTicket(
                                loggedUser.getWallet().getAddress(),
                                (MoneyHelper.priceInCents(ticket.getPrice())) ,
                                ticket.getResellable(),
                                ticket.getEvent().getName()
                        ).send();
                        ticket.setBlockchainId(blockchainTicketId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else  {
                    try {
                        ticketContract.transferTicket(
                                loggedUser.getWallet().getAddress(),
                                ticket.getBlockchainId()
                        ).send();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                ticket.setOwner(loggedUser); // imposto il proprietario

            }


            ticketRepository.saveAll(tickets); // salvo i ticket aggiornati

            // Pubblica l'evento di acquisto del biglietto
            applicationEventPublisher.publishEvent(new TicketPurchasedEvent(this, event));

            // TODO: Trasfermento denaro conto venditore( organizzatore o precedente proprietario)

            return PurchaseTicketResponse.builder()
                    .success(true)
                    .message(String.format("Purchase successful! Total amount charged: %s", totalPrice))
                    .totalPrice(totalPrice)
                    .build();
        } else {
            throw new FailedPaymentException("Payment processing failed"); // fa il rollback della transazione
        }


    }


    private void verifyTicketOwnershipLimit(User loggedUser, Event event, List<Ticket> tickets) {
        int eventTickedAlreadyOwned =ticketRepository.countAllByOwnerAndEvent(loggedUser, event);
        if (eventTickedAlreadyOwned + tickets.size()  > MAX_TICKETS_PER_EVENT ) { // se supera il limite di 4 ticket per evento, lancio eccezione
            throw new ForbiddenActionException("User cannot purchase more than 4 tickets for the same event", String.format("You already own %d tickets for this event a", eventTickedAlreadyOwned));
        }
    }


    @Transactional
    public void resellTicket(Integer ticketId){
        User user = securityService.getLoggedInUser();
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException(String.format("Ticket with id %d not found", ticketId), "Ticket not found"));

        if (ticket.getOwner() == null || !ticket.getOwner().equals(user))
            throw new ForbiddenActionException("Ticket is not in your account");

        if(!ticket.getResellable())
            throw new NonResellableTicketException("This ticket is not resellable");

        ticket.setResellable(false);
        ticket.setTicketStatus(TicketStatus.AVAILABLE);
        ticket.setOwner(null);

        ticketRepository.save(ticket);
        applicationEventPublisher.publishEvent(new TicketResoldEvent(this, ticket.getEvent()));

    }


    public List<TicketDto> getLoggedUserTickets() {
        User loggedUser = securityService.getLoggedInUser();
        return ticketRepository.findAllByOwner(loggedUser).stream().map(TicketMapper::toDto).toList();
    }


    @SuppressWarnings("unused") // ignora il warning di parametri non usati
    private static boolean managePayment(String creditCardNumber, String expirationDate, String cvv, String cardHolderName, BigDecimal amount) {
        // Simula la gestione del pagamento
        return true; // Supponiamo che il pagamento sia sempre riuscito
    }

    public void invalidateTicket(Integer ticketId){
       Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException(String.format("Ticket with id %d not found", ticketId), "Ticket not found"));
       ticket.setTicketStatus(TicketStatus.INVALIDATED);
       ticketRepository.save(ticket);

        //lo brucio dalla bc
        ticketContract.burnTicket(ticket.getBlockchainId());

    }
}
