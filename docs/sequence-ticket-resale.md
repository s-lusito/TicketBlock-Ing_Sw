# Diagramma di Sequenza UML - Rivendita Biglietto

Questo diagramma mostra la sequenza di interazioni per la rivendita di un biglietto nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor Client
    participant TicketController
    participant TicketService
    participant BlockchainContract
    participant TicketRepository
    participant EventPublisher

    Client->>TicketController: Richiesta rivendita biglietto
    TicketController->>TicketService: Elabora rivendita
    
    TicketService->>TicketRepository: Recupera biglietto
    
    alt Biglietto non trovato
        TicketService-->>Client: Errore: biglietto non trovato
    end
    
    alt Utente non è proprietario
        TicketService-->>Client: Errore: accesso negato
    end
    
    TicketService->>BlockchainContract: Verifica proprietà su blockchain
    alt Errore blockchain
        BlockchainContract-->>TicketService: Errore: verifica fallita
        TicketService-->>Client: Errore: impossibile verificare proprietà
    else Verifica proprietà fallita
        TicketService-->>Client: Errore: proprietà non verificata
    end
    
    TicketService->>BlockchainContract: Verifica rivendibilità su blockchain
    alt Errore blockchain
        BlockchainContract-->>TicketService: Errore: verifica fallita
        TicketService-->>Client: Errore: impossibile verificare rivendibilità
    else Biglietto non rivendibile
        TicketService-->>Client: Errore: biglietto non rivendibile
    end
    
    TicketService->>TicketService: Rimuovi proprietario
    Note over TicketService: Imposta stato AVAILABLE<br/>Rimuove associazione proprietario
    
    TicketService->>TicketRepository: Salva modifiche
    TicketService->>EventPublisher: Pubblica evento rivendita
    Note over EventPublisher: Aggiorna stato evento se necessario
    
    TicketService-->>TicketController: Conferma rivendita
    TicketController-->>Client: Biglietto rimesso in vendita
```
