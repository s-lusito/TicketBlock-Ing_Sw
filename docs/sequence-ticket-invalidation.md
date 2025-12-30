# Diagramma di Sequenza UML - Invalidazione Biglietto

Questo diagramma mostra la sequenza di interazioni per l'invalidazione di un biglietto nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor User
    participant TicketController
    participant TicketService
    participant BlockchainContract
    participant TicketRepository

    User->>TicketController: Richiesta invalidazione biglietto
    TicketController->>TicketService: Elabora invalidazione
    
    TicketService->>TicketRepository: Recupera biglietto
    
    alt Biglietto non trovato
        TicketService-->>User: Errore: biglietto non trovato
    end
    
    alt Utente non è proprietario
        TicketService-->>User: Errore: accesso negato
    end
    
    TicketService->>BlockchainContract: Verifica proprietà su blockchain
    Note over BlockchainContract: Conferma proprietà prima dell'invalidazione
    
    alt Verifica proprietà fallita
        TicketService-->>User: Errore: proprietà non verificata
    end
    
    TicketService->>TicketService: Imposta stato INVALIDATED
    TicketService->>TicketRepository: Salva modifiche
    
    TicketService->>BlockchainContract: Brucia NFT
    Note over BlockchainContract: Il biglietto diventa<br/>permanentemente invalido
    
    TicketService-->>TicketController: Conferma invalidazione
    TicketController-->>User: Biglietto invalidato con successo
```
