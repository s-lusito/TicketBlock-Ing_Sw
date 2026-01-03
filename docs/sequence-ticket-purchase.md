# Diagramma di Sequenza UML - Acquisto Biglietto

Questo diagramma mostra la sequenza di interazioni per l'acquisto di biglietti nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor Client
    participant TicketController
    participant TicketService
    participant TicketRepository
    participant BlockchainContract
    participant EventPublisher

    Client->>TicketController: Richiesta acquisto biglietti
    TicketController->>TicketService: Elabora acquisto
    
    TicketService->>TicketRepository: Recupera biglietti
    Note over TicketService: Verifica limite 4 biglietti per evento
    
    alt Biglietti non disponibili
        TicketService-->>Client: Errore: biglietti non disponibili
    end
    
    TicketService->>TicketService: Calcola prezzo totale con fee opzionale
    Note over TicketService: Fee 10% per abilitare rivendita
    
    TicketService->>TicketService: Elabora pagamento
    
    alt Pagamento fallito
        TicketService-->>Client: Errore: pagamento fallito
    end
    
    alt Primo acquisto
        TicketService->>BlockchainContract: Crea NFT biglietto
        alt Errore blockchain
            BlockchainContract-->>TicketService: Errore: transazione fallita
            TicketService-->>Client: Errore: impossibile creare NFT
        else Successo
            BlockchainContract-->>TicketService: ID blockchain
        end
    else Rivendita
        TicketService->>BlockchainContract: Verifica proprietà
        alt Errore verifica
            BlockchainContract-->>TicketService: Errore: verifica fallita
            TicketService-->>Client: Errore: proprietà non verificata
        else Proprietà verificata
            TicketService->>BlockchainContract: Trasferisci NFT
            alt Errore trasferimento
                BlockchainContract-->>TicketService: Errore: trasferimento fallito
                TicketService-->>Client: Errore: impossibile trasferire NFT
            end
        end
    end
    
    TicketService->>TicketRepository: Salva biglietti
    TicketService->>EventPublisher: Pubblica evento acquisto
    
    TicketService-->>TicketController: Conferma acquisto
    TicketController-->>Client: Acquisto completato con successo
```
