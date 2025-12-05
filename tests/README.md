# Test Scripts per TicketBlock

Questa directory contiene script JavaScript per testare l'acquisto di biglietti nell'applicazione TicketBlock.

## Prerequisiti

- Node.js 18+ (con supporto nativo per `fetch`)
- Backend TicketBlock in esecuzione su `http://localhost:8080`
- Database PostgreSQL configurato e avviato

## Avvio del Backend

1. Avvia il database con Docker:
   ```bash
   docker-compose up -d
   ```

2. Avvia l'applicazione Spring Boot:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

## Esecuzione dei Test

### Test Lato Organizer

Testa il flusso dell'organizzatore: registrazione, login, creazione evento (con generazione automatica dei biglietti).

```bash
cd tests
node organizer-ticket-test.js
```

### Test Lato User

Testa il flusso dell'utente: registrazione, login, visualizzazione eventi, acquisto biglietti.

```bash
cd tests
node user-ticket-test.js
```

### Esegui Tutti i Test

```bash
cd tests
npm run test:all
```

## Configurazione

Per cambiare l'URL del server, usa la variabile d'ambiente `API_BASE_URL`:

```bash
API_BASE_URL=http://localhost:3000 node organizer-ticket-test.js
```

## Struttura dei Test

### Test Organizer (`organizer-ticket-test.js`)
1. **Registrazione Organizer** - Crea un nuovo account organizzatore
2. **Login Organizer** - Autentica l'organizzatore
3. **Creazione Evento** - Crea un evento con generazione automatica biglietti
4. **Visualizzazione Evento** - Recupera i dettagli dell'evento creato
5. **Lista Eventi** - Elenca tutti gli eventi disponibili
6. **Eliminazione Evento** - Rimuove l'evento di test

### Test User (`user-ticket-test.js`)
1. **Registrazione User** - Crea un nuovo account utente
2. **Login User** - Autentica l'utente
3. **Visualizzazione Eventi** - Elenca gli eventi disponibili
4. **Dettagli Evento** - Recupera i dettagli di un evento specifico
5. **Acquisto Biglietto** - Acquista un biglietto (quando implementato)
6. **I Miei Biglietti** - Visualizza i biglietti acquistati (quando implementato)

## Note

- I test usano email generate dinamicamente per evitare conflitti
- Alcuni endpoint per l'acquisto biglietti potrebbero non essere ancora implementati nel backend
- I test puliscono automaticamente i dati creati durante l'esecuzione
