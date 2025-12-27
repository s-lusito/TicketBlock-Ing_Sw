# UML Use Case Diagram - TicketBlock System

This diagram shows the main use cases and actors in the TicketBlock ticketing system.

```mermaid
graph TB
    subgraph TicketBlock System
        UC1[Create Event]
        UC2[Remove Event]
        UC3[View Events]
        UC4[View Event Details]
        UC5[Purchase Ticket]
        UC6[Resell Ticket]
        UC7[Invalidate Ticket]
        UC8[View My Tickets]
        UC9[View My Events]
        UC10[View Event Sales Details]
        UC11[Create Venue]
        UC12[Register User]
        UC13[Login]
    end
    
    User((User))
    Organizer((Organizer))
    System((System Scheduler))
    
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7
    User --> UC8
    User --> UC12
    User --> UC13
    
    Organizer --> UC1
    Organizer --> UC2
    Organizer --> UC9
    Organizer --> UC10
    Organizer --> UC11
    
    System --> UC14[Update Event Sale Status]
    
    UC1 -.includes.-> UC3
    UC5 -.includes.-> UC4
    UC9 -.extends.-> UC3
    UC10 -.extends.-> UC4
    
    style UC1 fill:#e1f5ff
    style UC2 fill:#e1f5ff
    style UC5 fill:#fff4e1
    style UC6 fill:#fff4e1
    style UC7 fill:#fff4e1
    style UC8 fill:#fff4e1
    style UC11 fill:#e1f5ff
```

## Actor Descriptions

### User
A registered user who can:
- View available events
- Purchase tickets (with or without resale option)
- View their owned tickets
- Resell purchased tickets (if resellable)
- Invalidate their tickets (e.g., when entering an event)

### Organizer
A special type of user (with ORGANIZER role) who can:
- Create new events
- Remove events (if no tickets sold)
- Create venues
- View their organized events
- View sales details for their events

### System Scheduler
Automated system process that:
- Updates event sale statuses based on dates
- Opens sales when sale start date arrives
- Closes sales one day before event
- Runs daily at midnight

## Main Use Cases

1. **Create Event**: Organizer creates a new event with venue, dates, and pricing
2. **Purchase Ticket**: User buys one or more tickets for an event
3. **Resell Ticket**: User puts a previously purchased ticket back on sale
4. **Invalidate Ticket**: User marks a ticket as used/invalid (burns NFT on blockchain)
5. **View Event Sales Details**: Organizer views statistics about ticket sales for their events

## Business Rules

- Users can purchase maximum 4 tickets per event
- Tickets can be purchased with 10% fee to make them resellable
- Events must have sales start at least 3 days before event date
- Sales close automatically 1 day before event
- Organizers cannot delete events with sold tickets
- Each ticket is represented as an NFT on the blockchain
