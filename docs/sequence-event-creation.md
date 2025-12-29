# Diagramma di Sequenza UML - Creazione Evento

Questo diagramma mostra la sequenza di interazioni per la creazione di un nuovo evento nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor Organizer
    participant Controller as OrganizerEventController
    participant Service as EventService
    participant SecurityService
    participant VenueService
    participant VenueRepo as VenueRepository
    participant EventRepo as EventRepository
    participant DB as Database

    Organizer->>Controller: POST /api/organizer/events (EventCreationRequest)
    activate Controller
    
    Controller->>Service: createEvent(eventCreationRequest)
    activate Service
    
    Service->>Service: verifyDateAndTime(event)
    Note over Service: Verifica che la data evento non sia nel passato<br/>Verifica validità data inizio vendita<br/>Verifica minimo 3 giorni tra vendita ed evento
    
    Service->>VenueRepo: findById(venueId)
    activate VenueRepo
    VenueRepo->>DB: SELECT venue
    DB-->>VenueRepo: dati venue
    VenueRepo-->>Service: Venue
    deactivate VenueRepo
    
    Service->>VenueService: isVenueAvailable(venueId, date, timeSlot, duration)
    activate VenueService
    VenueService-->>Service: boolean (disponibile)
    deactivate VenueService
    
    alt Venue non disponibile
        Service-->>Controller: throw VenueNotAvailableException
        Controller-->>Organizer: 409 Conflict
    end
    
    Service->>SecurityService: getLoggedInUser()
    activate SecurityService
    SecurityService-->>Service: User (organizzatore)
    deactivate SecurityService
    
    Service->>Service: createTickets(venue, event)
    Note over Service: Per ogni posto nel venue:<br/>Crea biglietto con prezzo basato sul settore<br/>Imposta stato ad AVAILABLE
    
    Service->>Service: setSaleStatus(event)
    Note over Service: Imposta a NOT_STARTED se data vendita è futura<br/>Imposta a ONGOING se vendita inizia oggi
    
    Service->>EventRepo: save(event)
    activate EventRepo
    EventRepo->>DB: INSERT event e biglietti
    DB-->>EventRepo: entità salvata
    EventRepo-->>Service: Event
    deactivate EventRepo
    
    Service-->>Controller: EventDto
    deactivate Service
    
    Controller-->>Organizer: 201 Created (EventDto)
    deactivate Controller
```
