# TicketBlock System - UML Documentation

This directory contains UML diagrams created with Mermaid for the TicketBlock ticketing system.

## Contents

### 1. [Class Diagram](./class-diagram.md)
Shows the main domain entities and their relationships:
- User, Event, Ticket, Venue, Row, Seat, Wallet, Address
- Enumerations: EventSaleStatus, TicketStatus, Role, RowSector
- Relationships between entities

### 2. Sequence Diagrams
Detailed sequence diagrams for the main use cases:

#### [Event Creation](./sequence-event-creation.md)
- Organizer creates a new event
- System validates dates and venue availability
- Creates tickets for all seats in the venue

#### [Ticket Purchase](./sequence-ticket-purchase.md)
- User purchases one or more tickets
- System processes payment
- Mints or transfers NFT on blockchain
- Updates ticket ownership

#### [Ticket Resale](./sequence-ticket-resale.md)
- User puts a previously purchased ticket back on sale
- System validates ownership and resellability
- Makes ticket available again

#### [Ticket Invalidation](./sequence-ticket-invalidation.md)
- User invalidates a ticket (e.g., when entering event)
- System marks ticket as invalidated
- Burns the NFT on blockchain

### 3. [Use Case Diagram](./use-case-diagram.md)
Shows:
- System actors: User, Organizer, System Scheduler
- Main use cases and their relationships
- Business rules and constraints

## How to View

These diagrams use Mermaid syntax and can be viewed in:
- GitHub (renders Mermaid automatically)
- VS Code with Mermaid extension
- Any Mermaid-compatible viewer
- Online at https://mermaid.live/

## System Overview

TicketBlock is a blockchain-based ticketing system that:
- Uses Ethereum smart contracts for ticket NFTs
- Supports event creation by organizers
- Allows users to purchase tickets with optional resale capability
- Implements automated sale status management
- Provides secure ticket validation through blockchain

### Key Technologies
- **Backend**: Java Spring Boot
- **Database**: JPA/Hibernate with relational database
- **Blockchain**: Ethereum (Truffle framework)
- **Smart Contracts**: Solidity

### Main Features
- Event management with venue booking
- Ticket purchasing with payment processing
- Blockchain-based ticket NFTs
- Resale marketplace for tickets (with fee)
- Automatic sale status updates
- Ticket invalidation for event entry
