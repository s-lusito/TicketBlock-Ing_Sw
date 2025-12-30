# Diagramma di Sequenza UML - Registrazione Utente

Questo diagramma mostra la sequenza di interazioni per la registrazione utente con allocazione del wallet blockchain nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor User
    participant AuthenticationController
    participant AuthenticationService
    participant UserRepository
    participant WalletRepository

    User->>AuthenticationController: Richiesta registrazione
    AuthenticationController->>AuthenticationService: Elabora registrazione
    
    AuthenticationService->>UserRepository: Verifica email esistente
    
    alt Email già registrata
        AuthenticationService-->>User: Errore: utente già registrato
    end
    
    AuthenticationService->>WalletRepository: Verifica wallet disponibili
    
    alt Nessun wallet disponibile
        AuthenticationService-->>User: Errore: nessun wallet disponibile
    end
    
    AuthenticationService->>WalletRepository: Alloca wallet libero
    
    AuthenticationService->>AuthenticationService: Crea utente con password hashata
    Note over AuthenticationService: Password protetta con BCrypt<br/>Associa wallet all'utente
    
    AuthenticationService->>UserRepository: Salva nuovo utente
    
    AuthenticationService->>AuthenticationService: Genera token JWT
    Note over AuthenticationService: Access token e refresh token
    
    AuthenticationService-->>AuthenticationController: Token di autenticazione
    AuthenticationController-->>User: Registrazione completata con successo
```
