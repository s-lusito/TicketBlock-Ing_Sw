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

### 1. Diagrammi delle Classi

#### [Class Diagram - Backend](./class-diagram.md)
Mostra le entità di dominio principali e le loro relazioni nel backend:
- 8 entità: User, Event, Ticket, Venue, Row, Seat, Wallet, Address
- 4 enumerazioni: EventSaleStatus, TicketStatus, Role, RowSector
- Livello servizi e integrazione blockchain
- Architettura completa a 3 livelli (Controller → Service → Repository)

#### [Class Diagram - Frontend](./class-diagram-frontend.md)
Architettura del frontend Vue.js (da implementare):
- Componenti Vue.js per tutte le pagine
- Vuex Store per gestione stato
- Servizi API per comunicazione con backend
- Router con navigation guards
- Modelli DTOs e utilities

### 2. Diagrammi di Sequenza
Diagrammi di sequenza dettagliati per i casi d'uso principali. **Nota**: Il frontend è rappresentato come attore "Client".

#### [Registrazione Utente](./sequence-user-registration.md)
- Client richiede registrazione con email e password
- Sistema verifica disponibilità wallet blockchain
- Alloca wallet all'utente
- Crea token JWT per autenticazione

#### [Creazione Evento](./sequence-event-creation.md)
- Client (organizzatore) crea un nuovo evento
- Sistema valida date e disponibilità venue
- Crea biglietti per tutti i posti nel venue

#### [Acquisto Biglietto](./sequence-ticket-purchase.md)
- Client acquista uno o più biglietti
- Sistema elabora pagamento
- Verifica proprietà blockchain per biglietti rivenduti
- Crea o trasferisce NFT sulla blockchain
- **Include gestione errori blockchain**

#### [Rivendita Biglietto](./sequence-ticket-resale.md)
- Client rimette in vendita un biglietto acquistato
- Sistema valida proprietà sulla blockchain
- Sistema verifica rivendibilità sulla blockchain
- **Include gestione errori blockchain**
- Rende il biglietto nuovamente disponibile

#### [Invalidazione Biglietto](./sequence-ticket-invalidation.md)
- Client invalida un biglietto (es. all'ingresso evento)
- Sistema verifica proprietà sulla blockchain
- Marca il biglietto come invalidato
- Brucia l'NFT sulla blockchain
- **Include gestione errori blockchain**

### 3. [Diagramma dei Casi d'Uso](./use-case-diagram.md)
Mostra:
- Attori del sistema: Client (Frontend), Organizer, System Scheduler
- Casi d'uso principali e le loro relazioni
- Regole di business e vincoli

## Come Visualizzare

Questi diagrammi usano sintassi Mermaid e possono essere visualizzati in:
- GitHub (rende Mermaid automaticamente)
- VS Code con estensione Mermaid
- Qualsiasi visualizzatore compatibile con Mermaid
- Online su https://mermaid.live/

## Panoramica del Sistema

TicketBlock è un sistema di biglietteria basato su blockchain che:
- Usa smart contract Ethereum per NFT dei biglietti
- Supporta creazione eventi da parte degli organizzatori
- Permette agli utenti di acquistare biglietti con capacità di rivendita opzionale
- Implementa gestione automatica dello stato di vendita
- Fornisce validazione sicura dei biglietti tramite blockchain

### Tecnologie Principali
- **Backend**: Java Spring Boot
- **Database**: JPA/Hibernate con database relazionale
- **Blockchain**: Ethereum (framework Truffle)
- **Smart Contracts**: Solidity
- **Frontend**: Vue.js 3 (da implementare)

### Funzionalità Principali
- Event management with venue booking
- Ticket purchasing with payment processing
- Blockchain-based ticket NFTs
- Resale marketplace for tickets (with fee)
- Automatic sale status updates
- Ticket invalidation for event entry
