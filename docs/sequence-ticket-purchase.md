# Diagramma di Sequenza UML - Acquisto Biglietto

Questo diagramma mostra la sequenza di interazioni per l'acquisto di biglietti nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor User
    participant Controller as TicketController
    participant Service as TicketService
    participant SecurityService
    participant TicketRepo as TicketRepository
    participant TicketContract as BlockchainContract
    participant EventPublisher as ApplicationEventPublisher
    participant DB as Database

    User->>Controller: POST /api/tickets/purchase (PurchaseTicketRequest)
    activate Controller
    
    Controller->>Service: purchaseTickets(ticketsRequested)
    activate Service
    
    Service->>SecurityService: getLoggedInUser()
    activate SecurityService
    SecurityService-->>Service: User
    deactivate SecurityService
    
    Service->>TicketRepo: findAllByIdIn(ticketIds)
    activate TicketRepo
    TicketRepo->>DB: SELECT tickets
    DB-->>TicketRepo: ticket list
    TicketRepo-->>Service: List<Ticket>
    deactivate TicketRepo
    
    Service->>Service: verifyTicketOwnershipLimit(user, event, tickets)
    Note over Service: Verifica che l'utente non superi<br/>il limite MAX_TICKETS_PER_EVENT (4)
    
    alt Biglietti non disponibili o eventi diversi
        Service-->>Controller: throw UnavailableTicketException
        Controller-->>User: 409 Conflict
    end
    
    loop Per ogni biglietto
        Service->>Service: Calculate price with optional fee
        Note over Service: Se l'utente accetta la fee (feePercentage = 10%):<br/>- Aggiunge la fee al prezzo<br/>- Imposta il biglietto come rivendibile<br/>Altrimenti:<br/>- Mantiene il prezzo originale<br/>- Imposta il biglietto come non rivendibile
        Service->>Service: Set ticketStatus = SOLD
    end
    
    Service->>Service: managePayment(creditCard, totalPrice)
    Note over Service: Elaborazione pagamento simulata
    
    alt Pagamento fallito
        Service-->>Controller: throw FailedPaymentException
        Controller-->>User: 402 Payment Required
    end
    
    loop Per ogni biglietto
        alt Primo acquisto
            Service->>TicketContract: mintTicket(walletAddress, price, resellable, eventName)
            activate TicketContract
            TicketContract-->>Service: blockchainTicketId
            deactivate TicketContract
            Service->>Service: Set ticket.blockchainId
        else Rivendita
            Service->>TicketContract: verifyTicketOwnership(blockchainId, ownerAddress)
            activate TicketContract
            Note over TicketContract: Verifica che il proprietario corrente<br/>corrisponda al record blockchain
            TicketContract-->>Service: boolean (isValid)
            deactivate TicketContract
            
            alt Verifica proprietà fallita
                Service-->>Controller: throw UnavailableTicketException
                Controller-->>User: 409 Conflict
            end
            
            Service->>TicketContract: transferTicket(walletAddress, blockchainId)
            activate TicketContract
            TicketContract-->>Service: success
            deactivate TicketContract
        end
        Service->>Service: Set ticket.owner = user
    end
    
    Service->>TicketRepo: saveAll(tickets)
    activate TicketRepo
    TicketRepo->>DB: UPDATE tickets
    DB-->>TicketRepo: success
    TicketRepo-->>Service: saved tickets
    deactivate TicketRepo
    
    Service->>EventPublisher: publishEvent(TicketPurchasedEvent)
    activate EventPublisher
    EventPublisher-->>Service: evento pubblicato
    deactivate EventPublisher
    
    Service-->>Controller: PurchaseTicketResponse
    deactivate Service
    
    Controller-->>User: 200 OK (Acquisto completato)
    deactivate Controller
```
