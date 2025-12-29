# Diagramma di Sequenza UML - Registrazione Utente

Questo diagramma mostra la sequenza di interazioni per la registrazione utente con allocazione del wallet blockchain nel sistema TicketBlock.

```mermaid
sequenceDiagram
    actor User
    participant Controller as AuthenticationController
    participant Service as AuthenticationService
    participant UserRepo as UserRepository
    participant WalletRepo as WalletRepository
    participant DB as Database

    User->>Controller: POST /api/auth/register (RegisterRequest)
    activate Controller
    
    Controller->>Service: register(request)
    activate Service
    
    Service->>UserRepo: findByEmail(email)
    activate UserRepo
    UserRepo->>DB: SELECT user WHERE email
    DB-->>UserRepo: user data (if exists)
    UserRepo-->>Service: Optional<User>
    deactivate UserRepo
    
    alt Utente già esistente
        Service-->>Controller: throw IllegalArgumentException
        Controller-->>User: 400 Bad Request (Utente già registrato)
    end
    
    Service->>WalletRepo: countWalletsByFreeTrue()
    activate WalletRepo
    Note over WalletRepo: Verifica disponibilità<br/>wallet blockchain liberi
    WalletRepo->>DB: COUNT wallets WHERE free=true
    DB-->>WalletRepo: count
    WalletRepo-->>Service: long (count)
    deactivate WalletRepo
    
    alt Nessun wallet disponibile
        Service-->>Controller: throw RuntimeException
        Controller-->>User: 500 Internal Server Error (Nessun wallet disponibile)
    end
    
    Service->>WalletRepo: findFirstByFreeTrue()
    activate WalletRepo
    WalletRepo->>DB: SELECT wallet WHERE free=true LIMIT 1
    DB-->>WalletRepo: wallet data
    WalletRepo-->>Service: Wallet
    deactivate WalletRepo
    
    Service->>Service: Create User with hashed password
    Note over Service: Password hashata con BCrypt
    Service->>Service: Set user.wallet = allocatedWallet
    Service->>Service: Set wallet.free = false
    Service->>Service: Set wallet.user = user
    
    Service->>UserRepo: save(user)
    activate UserRepo
    UserRepo->>DB: INSERT user
    DB-->>UserRepo: utente salvato
    UserRepo-->>Service: User
    deactivate UserRepo
    
    Service->>Service: generateJwtToken(user)
    Note over Service: Crea JWT access token<br/>e refresh token
    
    Service-->>Controller: AuthenticationResponse(tokens)
    deactivate Service
    
    Controller-->>User: 200 OK (tokens, informazioni utente)
    deactivate Controller
```
