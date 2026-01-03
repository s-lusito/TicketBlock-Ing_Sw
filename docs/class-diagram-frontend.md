# Diagramma delle Classi UML - Frontend Vue.js

Questo diagramma mostra l'architettura del frontend Vue.js del sistema TicketBlock.

## Componenti Vue.js

```mermaid
classDiagram
    class App {
        +Router router
        +Store store
        +mounted()
        +created()
    }

    class HomePage {
        -Event[] events
        +mounted()
        +loadEvents()
        +filterEvents(status)
    }

    class EventDetailPage {
        -Event event
        -Ticket[] tickets
        +mounted()
        +loadEventDetails(eventId)
        +loadTickets()
    }

    class TicketPurchasePage {
        -Ticket[] selectedTickets
        -boolean acceptFee
        -number totalPrice
        +selectTicket(ticket)
        +calculateTotal()
        +purchaseTickets()
    }

    class MyTicketsPage {
        -Ticket[] myTickets
        +mounted()
        +loadMyTickets()
        +resellTicket(ticketId)
        +invalidateTicket(ticketId)
    }

    class OrganizerDashboard {
        -Event[] myEvents
        +mounted()
        +loadMyEvents()
        +viewEventDetails(eventId)
    }

    class CreateEventPage {
        -EventForm eventForm
        -Venue[] venues
        +mounted()
        +loadVenues()
        +validateForm()
        +createEvent()
    }

    class LoginPage {
        -LoginCredentials credentials
        +login()
        +handleLoginSuccess()
    }

    class RegisterPage {
        -RegistrationData registrationData
        +register()
        +validatePassword()
        +handleRegisterSuccess()
    }

    App --> HomePage
    App --> EventDetailPage
    App --> TicketPurchasePage
    App --> MyTicketsPage
    App --> OrganizerDashboard
    App --> CreateEventPage
    App --> LoginPage
    App --> RegisterPage
```

## Vuex Store

```mermaid
classDiagram
    class Store {
        -State state
        -Mutations mutations
        -Actions actions
        -Getters getters
    }

    class State {
        +User user
        +string authToken
        +Event[] events
        +Ticket[] tickets
        +boolean loading
    }

    class Mutations {
        +SET_USER(user)
        +SET_AUTH_TOKEN(token)
        +SET_EVENTS(events)
        +SET_TICKETS(tickets)
        +SET_LOADING(loading)
        +LOGOUT()
    }

    class Actions {
        +login(credentials)
        +register(data)
        +logout()
        +fetchEvents()
        +fetchMyTickets()
        +purchaseTickets(request)
    }

    class Getters {
        +isAuthenticated() boolean
        +currentUser() User
        +isOrganizer() boolean
        +availableEvents() Event[]
    }

    Store --> State
    Store --> Mutations
    Store --> Actions
    Store --> Getters
```

## Servizi API

```mermaid
classDiagram
    class ApiService {
        -string baseURL
        -AxiosInstance httpClient
        +setAuthToken(token)
        +get(url, config)
        +post(url, data, config)
        +put(url, data, config)
        +delete(url, config)
    }

    class AuthService {
        -ApiService apiService
        +login(credentials) Promise~AuthResponse~
        +register(data) Promise~AuthResponse~
        +refreshToken(token) Promise~AuthResponse~
        +logout()
    }

    class EventService {
        -ApiService apiService
        +getAllEvents(statusList) Promise~Event[]~
        +getEventById(id) Promise~Event~
        +createEvent(request) Promise~Event~
        +removeEvent(id) Promise~void~
        +getOrganizerEvents() Promise~Event[]~
    }

    class TicketService {
        -ApiService apiService
        +getTicketsFromEvent(eventId, status) Promise~Ticket[]~
        +purchaseTickets(request) Promise~PurchaseResponse~
        +resellTicket(ticketId) Promise~void~
        +invalidateTicket(ticketId) Promise~void~
        +getMyTickets() Promise~Ticket[]~
    }

    class VenueService {
        -ApiService apiService
        +getVenueById(id) Promise~Venue~
        +getVenueAvailableSlots(id, date) Promise~AvailableSlots~
    }

    AuthService --> ApiService
    EventService --> ApiService
    TicketService --> ApiService
    VenueService --> ApiService
```

## Modelli e DTOs

```mermaid
classDiagram
    class User {
        +number userId
        +string email
        +string firstName
        +string lastName
        +Role role
        +Wallet wallet
    }

    class Event {
        +number id
        +string name
        +string date
        +string startTime
        +string endTime
        +EventSaleStatus saleStatus
        +string imageUrl
        +number standardTicketPrice
        +number vipTicketPrice
        +Venue venue
    }

    class Ticket {
        +number id
        +string blockchainId
        +number price
        +TicketStatus ticketStatus
        +boolean resellable
        +Event event
        +Seat seat
    }

    class Venue {
        +number id
        +string name
        +Address address
        +Row[] rows
    }

    class Seat {
        +number id
        +number seatNumber
        +Row row
    }

    class Wallet {
        +number id
        +string address
    }

    class Role {
        <<enumeration>>
        USER
        ORGANIZER
        ADMIN
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

    User --> Role
    User --> Wallet
    Event --> EventSaleStatus
    Event --> Venue
    Ticket --> TicketStatus
    Ticket --> Event
    Ticket --> Seat
```

## Router

```mermaid
classDiagram
    class VueRouter {
        -Route[] routes
        +push(location)
        +replace(location)
        +go(n)
        +beforeEach(guard)
    }

    class Route {
        +string path
        +string name
        +Component component
        +RouteMeta meta
        +beforeEnter(guard)
    }

    class NavigationGuard {
        +checkAuth()
        +checkRole(role)
        +redirectIfAuthenticated()
    }

    VueRouter --> Route
    VueRouter --> NavigationGuard
```

## Utilities

```mermaid
classDiagram
    class DateFormatter {
        +formatDate(date) string
        +formatTime(time) string
        +formatDateTime(dateTime) string
    }

    class PriceFormatter {
        +formatPrice(price) string
        +calculateTotal(tickets, fee) number
    }

    class Validator {
        +validateEmail(email) boolean
        +validatePassword(password) boolean
        +validateDate(date) boolean
    }

    class ErrorHandler {
        +handleApiError(error)
        +showNotification(message, type)
    }
```

## Note

**Importante**: Questo diagramma rappresenta l'architettura prevista per il frontend Vue.js che non è ancora stato implementato. Include:

- **Componenti Vue**: Pagine principali dell'applicazione
- **Vuex Store**: Gestione stato globale
- **Servizi API**: Comunicazione con backend Spring Boot
- **Router**: Navigazione tra pagine con guards
- **Models**: DTOs che rispecchiano le entità backend
- **Utilities**: Funzioni helper per formattazione e validazione

L'implementazione seguirà le best practices Vue.js 3 con Composition API e TypeScript.
