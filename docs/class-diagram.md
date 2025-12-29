# Diagramma delle Classi UML - Sistema TicketBlock

Questo diagramma mostra le classi principali e le loro relazioni nel sistema di biglietteria TicketBlock.

## Entità di Dominio

```mermaid
classDiagram
    class User {
        -Integer userId
        -String email
        -String password
        -String firstName
        -String lastName
        -Role role
        +getAuthorities()
        +getUsername()
    }

    class Event {
        -Integer id
        -String name
        -LocalDate date
        -LocalTime startTime
        -LocalTime endTime
        -EventSaleStatus saleStatus
        -LocalDate saleStartDate
        -String imageUrl
        -BigDecimal standardTicketPrice
        -BigDecimal vipTicketPrice
    }

    class Ticket {
        -Integer id
        -BigInteger blockchainId
        -Boolean resellable
        -BigDecimal price
        -TicketStatus ticketStatus
    }

    class Venue {
        -Integer id
        -String name
        +getStandardRows()
        +getVipRows()
    }

    class Row {
        -Integer id
        -String letter
        -RowSector sector
    }

    class Seat {
        -Integer id
        -Integer seatNumber
    }

    class Wallet {
        -Integer id
        -String address
        -Boolean free
    }

    class Address {
        -Integer id
        -String street
        -String city
        -String state
    }

    class EventSaleStatus {
        <<enumeration>>
        NOT_STARTED
        ONGOING
        SOLD_OUT
        ENDED
    }

    class TicketStatus {
        <<enumeration>>
        AVAILABLE
        SOLD
        INVALIDATED
    }

    class Role {
        <<enumeration>>
        USER
        ORGANIZER
        ADMIN
    }

    class RowSector {
        <<enumeration>>
        STANDARD
        VIP
    }

    User "1" -- "1" Wallet : has
    User "1" -- "*" Ticket : owns
    User "1" -- "*" Event : organizes
    Event "*" -- "1" Venue : held at
    Event "1" -- "*" Ticket : includes
    Ticket "*" -- "1" Seat : assigned to
    Venue "1" -- "1" Address : located at
    Venue "1" -- "*" Row : contains
    Row "1" -- "*" Seat : contains
    Event -- EventSaleStatus : has
    Ticket -- TicketStatus : has
    User -- Role : has
    Row -- RowSector : has
```

## Livello Servizi e Integrazione Blockchain

```mermaid
classDiagram
    class TicketService {
        -BigDecimal feePercentage
        -int MAX_TICKETS_PER_EVENT
        +getTicketsFromEvent(eventId, status) List~TicketDto~
        +purchaseTickets(request) PurchaseTicketResponse
        +resellTicket(ticketId) void
        +invalidateTicket(ticketId) void
        +getLoggedUserTickets() List~TicketDto~
        -verifyTicketOwnershipLimit(user, event, tickets) void
        -managePayment(cardNumber, date, cvv, holder, amount) boolean
    }

    class TicketContract {
        -String contractAddress
        -Web3j web3j
        +mintTicket(owner, price, resellable, info) BigInteger
        +transferTicket(to, ticketId) void
        +burnTicket(ticketId) void
        +getTicket(ticketId) List~Type~
        +isTicketResellable(ticketId) boolean
        +verifyTicketOwnership(ticketId, ownerAddress) boolean
    }

    class AuthenticationService {
        +register(request) AuthenticationResponse
        +login(request) AuthenticationResponse
        +refreshToken(refreshToken) AuthenticationResponse
        -allocateWallet(user) void
    }

    class WalletRepository {
        <<interface>>
        +findFirstByFreeTrue() Wallet
        +countWalletsByFreeTrue() long
    }

    class EventService {
        +createEvent(request) EventDto
        +getAllEvents(statusList) List~EventDto~
        +getEventById(id) EventDto
        +removeEventById(id) EventDto
        +updateEventsSaleStatus() void
        +updateStatusIfSoldOut(event) void
        -verifyDateAndTime(event) void
        -createTickets(venue, event) void
    }

    TicketService --> TicketContract : uses
    TicketService --> TicketRepository : uses
    AuthenticationService --> WalletRepository : uses
    EventService --> VenueService : uses
```

