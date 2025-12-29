# UML Sequence Diagram - Ticket Invalidation

This diagram shows the sequence of interactions for invalidating a ticket in the TicketBlock system.

```mermaid
sequenceDiagram
    actor User
    participant Controller as TicketController
    participant Service as TicketService
    participant SecurityService
    participant TicketRepo as TicketRepository
    participant TicketContract as BlockchainContract
    participant DB as Database

    User->>Controller: POST /api/tickets/{ticketId}/invalidate
    activate Controller
    
    Controller->>Service: invalidateTicket(ticketId)
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
    
    alt Ticket not found
        Service-->>Controller: throw ResourceNotFoundException
        Controller-->>User: 404 Not Found
    end
    
    alt User is not ticket owner
        Service-->>Controller: throw ForbiddenActionException
        Controller-->>User: 403 Forbidden
    end
    
    Service->>TicketContract: verifyTicketOwnership(blockchainId, ownerAddress)
    activate TicketContract
    Note over TicketContract: Verifies ownership<br/>on blockchain before invalidation
    TicketContract-->>Service: boolean (isOwner)
    deactivate TicketContract
    
    alt Ownership verification failed
        Service-->>Controller: throw ForbiddenActionException
        Controller-->>User: 403 Forbidden
    end
    
    Service->>Service: Set ticket.ticketStatus = INVALIDATED
    
    Service->>TicketRepo: save(ticket)
    activate TicketRepo
    TicketRepo->>DB: UPDATE ticket
    DB-->>TicketRepo: updated ticket
    TicketRepo-->>Service: Ticket
    deactivate TicketRepo
    
    Service->>TicketContract: burnTicket(blockchainId)
    activate TicketContract
    Note over TicketContract: Burns the ticket NFT<br/>from the blockchain,<br/>making it permanently invalid
    TicketContract-->>Service: success
    deactivate TicketContract
    
    Service-->>Controller: success
    deactivate Service
    
    Controller-->>User: 200 OK (Ticket invalidated)
    deactivate Controller
```
