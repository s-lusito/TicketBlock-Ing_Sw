# TicketBlock System - Documentazione Completa

Questa directory contiene tutta la documentazione del progetto TicketBlock, inclusi diagrammi UML e documentazione LaTeX completa.

## Contenuti

### Documentazione LaTeX (Italiano)

La directory `latex/` contiene la documentazione completa richiesta per il corso:

1. **[Relazione Tecnica](./latex/relazione-tecnica.tex)** - Documento completo che include:
   - Introduzione con ambito applicativo, obiettivo, problema e soluzione
   - Stato dell'arte e lavori correlati
   - Modello di processo Agile con Gantt, rischi e costi
   - Requisiti funzionali e non funzionali
   - Architettura e tech stack completo
   - Descrizione del prototipo
   - Validazione e verifica con test documentati
   - Discussione delle sfide tecniche
   - Conclusioni e sviluppi futuri
   - Bibliografia

2. **[Presentazione](./latex/presentazione.tex)** - Presentazione Beamer che include:
   - Copertina con logo applicazione
   - Presentazione del gruppo
   - Presentazione incrementale dei primi due step di consegna
   - Implementazione e interfaccia
   - Validazione e metriche
   - Conclusioni e roadmap futura

**Nota:** La documentazione include placeholder `[FRONTEND]` per le parti relative al frontend Vue.js non ancora completato.

Per compilare i documenti LaTeX, consultare il [README nella directory latex](./latex/README.md).

### Diagrammi UML

I diagrammi UML sono disponibili in **due formati**:

#### Formato Mermaid (Visualizzazione GitHub)
Diagrammi visualizzabili direttamente su GitHub con sintassi Mermaid. Ottimi per la consultazione online.

#### Formato PlantUML (Stampa e Integrazione)
Directory **[plantuml/](./plantuml/)** - Versione PlantUML dei diagrammi per:
- Generazione di immagini ad alta qualità (PNG, SVG, PDF)
- Integrazione nella relazione tecnica
- Compatibilità con editor e IDE
- Consultare il [README PlantUML](./plantuml/README.md) per istruzioni di utilizzo

### 1. [Class Diagram](./class-diagram.md)
Shows the main domain entities and their relationships:
- User, Event, Ticket, Venue, Row, Seat, Wallet, Address
- Enumerations: EventSaleStatus, TicketStatus, Role, RowSector
- Service layer and blockchain integration classes
- Relationships between entities

### 2. Sequence Diagrams
Detailed sequence diagrams for the main use cases:

#### [User Registration](./sequence-user-registration.md)
- User registers with email and password
- System checks wallet availability
- Allocates blockchain wallet to user
- Creates JWT tokens for authentication

#### [Event Creation](./sequence-event-creation.md)
- Organizer creates a new event
- System validates dates and venue availability
- Creates tickets for all seats in the venue

#### [Ticket Purchase](./sequence-ticket-purchase.md)
- User purchases one or more tickets
- System processes payment
- Verifies blockchain ownership for resold tickets
- Mints or transfers NFT on blockchain
- Updates ticket ownership

#### [Ticket Resale](./sequence-ticket-resale.md)
- User puts a previously purchased ticket back on sale
- System validates ownership on blockchain
- System verifies resellability on blockchain
- Makes ticket available again

#### [Ticket Invalidation](./sequence-ticket-invalidation.md)
- User invalidates a ticket (e.g., when entering event)
- System verifies ownership on blockchain
- Marks ticket as invalidated
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
