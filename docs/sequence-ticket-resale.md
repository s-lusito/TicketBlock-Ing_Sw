# Diagramma di Sequenza UML - Rivendita Biglietto

Questo diagramma mostra la sequenza di interazioni per la rivendita di un biglietto nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor User
    participant Controller as TicketController
    participant Service as TicketService
    participant SecurityService
    participant TicketRepo as TicketRepository
    participant TicketContract as BlockchainContract
    participant DB as Database

    User->>Controller: POST /api/tickets/{ticketId}/resell
    activate Controller
    
    Controller->>Service: resellTicket(ticketId)
    activate Service
    
    Service->>SecurityService: getLoggedInUser()
    activate SecurityService
    SecurityService-->>Service: User
    deactivate SecurityService
    
    Service->>TicketRepo: findById(ticketId)
    activate TicketRepo
    TicketRepo->>DB: SELECT ticket
    DB-->>TicketRepo: ticket data
    TicketRepo-->>Service: Ticket
    deactivate TicketRepo
    
    alt Biglietto non trovato
        Service-->>Controller: throw ResourceNotFoundException
        Controller-->>User: 404 Not Found
    end
    
    alt L'utente non è proprietario del biglietto
        Service-->>Controller: throw ForbiddenActionException
        Controller-->>User: 403 Forbidden
    end
    
    Service->>TicketContract: verifyTicketOwnership(blockchainId, ownerAddress)
    activate TicketContract
    Note over TicketContract: Verifica la proprietà<br/>sulla blockchain
    TicketContract-->>Service: boolean (isOwner)
    deactivate TicketContract
    
    alt Verifica proprietà fallita
        Service-->>Controller: throw ForbiddenActionException
        Controller-->>User: 403 Forbidden
    end
    
    Service->>TicketContract: isTicketResellable(blockchainId)
    activate TicketContract
    Note over TicketContract: Verifica lo stato di rivendibilità<br/>sulla blockchain
    TicketContract-->>Service: boolean (isResellable)
    deactivate TicketContract
    
    alt Biglietto non rivendibile sulla blockchain
        Service-->>Controller: throw NonResellableTicketException
        Controller-->>User: 400 Bad Request
    end
    
    alt Biglietto non rivendibile
        Service-->>Controller: throw NonResellableTicketException
        Controller-->>User: 400 Bad Request
    end
    
    Service->>Service: Set ticket.resellable = false
    Service->>Service: Set ticket.ticketStatus = AVAILABLE
    Service->>Service: Set ticket.owner = null
    
    Service->>TicketRepo: save(ticket)
    activate TicketRepo
    TicketRepo->>DB: UPDATE ticket
    DB-->>TicketRepo: updated ticket
    TicketRepo-->>Service: Ticket
    deactivate TicketRepo
    
    Service->>EventPublisher: publishEvent(TicketResoldEvent)
    activate EventPublisher
    Note over EventPublisher: L'evento viene pubblicato per aggiornare<br/>lo stato dell'evento (verifica se ancora sold out)
    EventPublisher-->>Service: evento pubblicato
    deactivate EventPublisher
    
    Service-->>Controller: success
    deactivate Service
    
    Controller-->>User: 200 OK (Biglietto rimesso in vendita)
    deactivate Controller
```
