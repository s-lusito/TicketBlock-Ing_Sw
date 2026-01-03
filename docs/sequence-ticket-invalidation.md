# Diagramma di Sequenza UML - Invalidazione Biglietto

Questo diagramma mostra la sequenza di interazioni per l'invalidazione di un biglietto nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor Client
    participant TicketController
    participant TicketService
    participant BlockchainContract
    participant TicketRepository

    Client->>TicketController: Richiesta invalidazione biglietto
    TicketController->>TicketService: Elabora invalidazione
    
    TicketService->>TicketRepository: Recupera biglietto
    
    alt Biglietto non trovato
        TicketService-->>Client: Errore: biglietto non trovato
    end
    
    alt Utente non è proprietario
        TicketService-->>Client: Errore: accesso negato
    end
    
    TicketService->>BlockchainContract: Verifica proprietà su blockchain
    Note over BlockchainContract: Conferma proprietà prima dell'invalidazione
    alt Errore blockchain
        BlockchainContract-->>TicketService: Errore: verifica fallita
        TicketService-->>Client: Errore: impossibile verificare proprietà
    else Verifica proprietà fallita
        TicketService-->>Client: Errore: proprietà non verificata
    end
    
    TicketService->>TicketService: Imposta stato INVALIDATED
    TicketService->>TicketRepository: Salva modifiche
    
    TicketService->>BlockchainContract: Brucia NFT
    Note over BlockchainContract: Il biglietto diventa<br/>permanentemente invalido
    alt Errore blockchain
        BlockchainContract-->>TicketService: Errore: burn fallito
        TicketService-->>Client: Errore: impossibile invalidare NFT
    end
    
    TicketService-->>TicketController: Conferma invalidazione
    TicketController-->>Client: Biglietto invalidato con successo
```
