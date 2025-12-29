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
        -Set~Ticket~ tickets
        -Set~Event~ events
        -Wallet wallet
        +getAuthorities()
        +getUsername()
        +isAccountNonExpired() boolean
        +isAccountNonLocked() boolean
        +isCredentialsNonExpired() boolean
        +isEnabled() boolean
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
        -User organizer
        -Venue venue
        -List~Ticket~ tickets
    }

    class Ticket {
        -Integer id
        -BigInteger blockchainId
        -Boolean resellable
        -BigDecimal price
        -TicketStatus ticketStatus
        -Event event
        -Seat seat
        -User owner
    }

    class Venue {
        -Integer id
        -String name
        -Address address
        -List~Row~ rows
        -List~Event~ events
        +getStandardRows() List~Row~
        +getVipRows() List~Row~
    }

    class Row {
        -Integer id
        -String letter
        -RowSector sector
        -Venue venue
        -List~Seat~ seats
    }

    class Seat {
        -Integer id
        -Integer seatNumber
        -Row row
        -Set~Ticket~ tickets
    }

    class Wallet {
        -Integer id
        -String address
        -Boolean free
        -User user
    }

    class Address {
        -Integer id
        -String street
        -String city
        -String state
        -Venue venue
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

    User "1" -- "1" Wallet : possiede
    User "1" -- "*" Ticket : proprietario
    User "1" -- "*" Event : organizza
    Event "*" -- "1" Venue : si tiene a
    Event "1" -- "*" Ticket : include
    Ticket "*" -- "1" Seat : assegnato a
    Venue "1" -- "1" Address : situato in
    Venue "1" -- "*" Row : contiene
    Row "1" -- "*" Seat : contiene
    Event -- EventSaleStatus : stato
    Ticket -- TicketStatus : stato
    User -- Role : ruolo
    Row -- RowSector : settore
```

## Livello Repository

```mermaid
classDiagram
    class UserRepository {
        <<interface>>
        +findByEmail(email) Optional~User~
    }

    class EventRepository {
        <<interface>>
        +findAllBySaleStatusIn(statusList) List~Event~
        +findAllByOrganizer(user) List~Event~
        +findAllByDate(date) List~Event~
        +findAllByDateAndVenueId(date, venueId) List~Event~
        +findAllToOpenToday() List~Event~
    }

    class TicketRepository {
        <<interface>>
        +findByEventIdAndOptionalTicketStatus(eventId, status) List~Ticket~
        +findAllByIdIn(ids) List~Ticket~
        +findAllByOwner(user) List~Ticket~
        +countAllByOwnerAndEvent(user, event) int
        +countSoldTicketsByEvent(event) int
        +countSoldTicketsByEventAndPrice(event, price) int
    }

    class VenueRepository {
        <<interface>>
        +findById(id) Optional~Venue~
    }

    class WalletRepository {
        <<interface>>
        +findFirstByFreeTrue() Wallet
        +countWalletsByFreeTrue() long
    }

    class RowRepository {
        <<interface>>
        +findById(id) Optional~Row~
    }

    class SeatRepository {
        <<interface>>
        +findById(id) Optional~Seat~
    }

    UserRepository --|> JpaRepository
    EventRepository --|> JpaRepository
    TicketRepository --|> JpaRepository
    VenueRepository --|> JpaRepository
    WalletRepository --|> JpaRepository
    RowRepository --|> JpaRepository
    SeatRepository --|> JpaRepository
```

## Livello Servizi

```mermaid
classDiagram
    class TicketService {
        -BigDecimal feePercentage
        -int MAX_TICKETS_PER_EVENT
        -TicketRepository ticketRepository
        -SecurityService securityService
        -TicketContract ticketContract
        +getTicketsFromEvent(eventId, status) List~TicketDto~
        +purchaseTickets(request) PurchaseTicketResponse
        +resellTicket(ticketId) void
        +invalidateTicket(ticketId) void
        +getLoggedUserTickets() List~TicketDto~
        -verifyTicketOwnershipLimit(user, event, tickets) void
        -managePayment(cardNumber, date, cvv, holder, amount) boolean
    }

    class EventService {
        -EventRepository eventRepository
        -VenueRepository venueRepository
        -SecurityService securityService
        -VenueService venueService
        +createEvent(request) EventDto
        +getAllEvents(statusList) List~EventDto~
        +getEventById(id) EventDto
        +removeEventById(id) EventDto
        +updateEventsSaleStatus() void
        +updateStatusIfSoldOut(event) void
        +getLoggedOrganizerAllEventsDetails() List~EventSaleDetailsDto~
        +getLoggedOrganizerEventDetails(eventId) EventSaleDetailsDto
        -verifyDateAndTime(event) void
        -createTickets(venue, event) void
    }

    class VenueService {
        -VenueRepository venueRepository
        -EventRepository eventRepository
        +getVenueByVenueId(venueId) VenueDto
        +getVenueAvailableSlots(venueId, date) VenueAvailableSlotsResponse
        +getAvailableSlots(venueId, date) Boolean[]
        +isVenueAvailable(venueId, date, timeSlot, duration) boolean
    }

    class AuthenticationService {
        -UserRepository userRepository
        -WalletRepository walletRepository
        -JwtService jwtService
        -PasswordEncoder passwordEncoder
        +register(request) AuthenticationResponse
        +login(request) AuthenticationResponse
        +refreshToken(refreshToken) AuthenticationResponse
        -allocateWallet(user) void
    }

    class UserService {
        -UserRepository userRepository
        +getUserById(userId) UserDto
    }

    class SecurityService {
        +getLoggedInUser() User
    }

    class JwtService {
        -String SECRET_KEY
        -Integer EXPIRATION_TIME
        +extractUsername(token) String
        +generateToken(userDetails) String
        +isTokenValid(token, userDetails) boolean
        -isTokenExpired(token) boolean
        -extractExpiration(token) Date
        -extractClaim(token, resolver) T
    }

    TicketService --> TicketRepository
    TicketService --> SecurityService
    TicketService --> TicketContract
    EventService --> EventRepository
    EventService --> VenueRepository
    EventService --> SecurityService
    EventService --> VenueService
    VenueService --> VenueRepository
    VenueService --> EventRepository
    AuthenticationService --> UserRepository
    AuthenticationService --> WalletRepository
    AuthenticationService --> JwtService
    UserService --> UserRepository
```

## Livello Controller

```mermaid
classDiagram
    class AuthenticationController {
        -AuthenticationService authenticationService
        +register(request) ResponseEntity~AuthenticationResponse~
        +login(request) ResponseEntity~AuthenticationResponse~
        +refreshToken(refreshToken) ResponseEntity~AuthenticationResponse~
    }

    class TicketController {
        -TicketService ticketService
        +getTicketsFromEvent(eventId, status) ResponseEntity~List~TicketDto~~
        +purchaseTickets(request) ResponseEntity~PurchaseTicketResponse~
        +resellTicket(ticketId) ResponseEntity~Void~
        +invalidateTicket(ticketId) ResponseEntity~Void~
        +getLoggedUserTickets() ResponseEntity~List~TicketDto~~
    }

    class OrganizerEventController {
        -EventService eventService
        +createEvent(request) ResponseEntity~EventDto~
        +removeEvent(eventId) ResponseEntity~EventDto~
        +getOrganizerEvents() ResponseEntity~List~EventSaleDetailsDto~~
        +getOrganizerEventDetails(eventId) ResponseEntity~EventSaleDetailsDto~
    }

    class PublicEventController {
        -EventService eventService
        +getAllEvents(statusList) ResponseEntity~List~EventDto~~
        +getEventById(eventId) ResponseEntity~EventDto~
    }

    class VenueController {
        -VenueService venueService
        +getVenueById(venueId) ResponseEntity~VenueDto~
        +getVenueAvailableSlots(venueId, date) ResponseEntity~VenueAvailableSlotsResponse~
    }

    class UserController {
        -UserService userService
        +getUserById(userId) ResponseEntity~UserDto~
    }

    AuthenticationController --> AuthenticationService
    TicketController --> TicketService
    OrganizerEventController --> EventService
    PublicEventController --> EventService
    VenueController --> VenueService
    UserController --> UserService
```

## Livello DTO e Mapper

```mermaid
classDiagram
    class EventDto {
        +Integer id
        +String name
        +LocalDate date
        +EventSaleStatus saleStatus
        +String imageUrl
    }

    class TicketDto {
        +Integer id
        +BigDecimal price
        +TicketStatus ticketStatus
        +Boolean resellable
    }

    class UserDto {
        +Integer userId
        +String email
        +String firstName
        +String lastName
        +Role role
    }

    class VenueDto {
        +Integer id
        +String name
        +AddressDto address
        +List~RowDto~ rows
    }

    class EventMapper {
        <<utility>>
        +toDto(event) EventDto
        +toEntity(request) Event
    }

    class TicketMapper {
        <<utility>>
        +toDto(ticket) TicketDto
    }

    class UserMapper {
        <<utility>>
        +toDto(user) UserDto
    }

    class VenueMapper {
        <<utility>>
        +toDto(venue) VenueDto
    }

    EventMapper ..> Event
    EventMapper ..> EventDto
    TicketMapper ..> Ticket
    TicketMapper ..> TicketDto
    UserMapper ..> User
    UserMapper ..> UserDto
    VenueMapper ..> Venue
    VenueMapper ..> VenueDto
```

## Integrazione Blockchain

```mermaid
classDiagram
    class TicketContract {
        -String contractAddress
        -Web3j web3j
        -Credentials credentials
        +mintTicket(owner, price, resellable, info) BigInteger
        +transferTicket(to, ticketId) void
        +burnTicket(ticketId) void
        +getTicket(ticketId) List~Type~
        +isTicketResellable(ticketId) boolean
        +verifyTicketOwnership(ticketId, ownerAddress) boolean
    }

    class Web3Config {
        -String nodeUrl
        -String privateKey
        -String contractAddress
        +web3j() Web3j
        +credentials() Credentials
        +ticketContract() TicketContract
    }

    class MoneyHelper {
        <<utility>>
        +normalizeAmount(amount) BigDecimal
        +priceInCents(price) BigInteger
    }

    class TimeSlot {
        <<utility>>
        -int index
        -String description
        +fromIndexOrThrow(index) TimeSlot
        +getLastSlot() TimeSlot
    }

    TicketContract --> Web3j
    TicketContract --> Credentials
    Web3Config ..> TicketContract
    Web3Config ..> Web3j
    Web3Config ..> Credentials
```

## Eventi Applicativi

```mermaid
classDiagram
    class TicketPurchasedEvent {
        -Event event
        +getEvent() Event
    }

    class TicketResoldEvent {
        -Event event
        +getEvent() Event
    }

    class EventStatusListener {
        -EventService eventService
        +handleTicketPurchased(event) void
        +handleTicketResold(event) void
    }

    TicketPurchasedEvent --|> ApplicationEvent
    TicketResoldEvent --|> ApplicationEvent
    EventStatusListener ..> TicketPurchasedEvent
    EventStatusListener ..> TicketResoldEvent
    EventStatusListener --> EventService
```

