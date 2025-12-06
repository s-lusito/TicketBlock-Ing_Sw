# Quick Start Guide - TicketBlock API Tests

## Avvio Rapido

### 1. Installazione

```bash
cd api-tests
npm install
```

### 2. Avviare il Backend

Prima di eseguire i test, assicurati che il backend TicketBlock sia in esecuzione.

Con Docker:
```bash
cd ..
docker-compose up
```

Oppure con Maven (dalla directory backend):
```bash
cd ../backend
./mvnw spring-boot:run
```

### 3. Eseguire i Test

Esegui tutti i test:
```bash
npm test
```

### 4. Risultati Attesi

I test genereranno output simile a questo:

```
╔════════════════════════════════════════════════════════════╗
║        TicketBlock API Test Suite                         ║
║        Testing all API endpoints                          ║
╚════════════════════════════════════════════════════════════╝


============================================================
📋 Test Suite: Authentication API Tests
============================================================
✅ PASS: Register new user with valid data
✅ PASS: Register new organizer with valid data
✅ PASS: Authenticate with valid credentials
✅ PASS: Register with invalid email format
...

============================================================
📊 Test Summary
============================================================
Total Tests: 60
✅ Passed: 58
❌ Failed: 2
Success Rate: 96.67%
============================================================
```

## Test Disponibili

### Test di Autenticazione (15 test)
- Registrazione e autenticazione con dati validi
- Validazione campi email, password, ruoli
- Gestione errori per credenziali invalide

### Test Eventi (12 test)
- CRUD completo per eventi
- Validazione date e orari
- Controllo prezzi e venue

### Test Venue (6 test)
- Recupero venue per ID
- Gestione errori per ID invalidi

### Test Biglietti (15 test)
- Acquisto biglietti con validazione carta di credito
- Rivendita biglietti
- Gestione autenticazione

### Test Biglietti Eventi (10 test)
- Recupero biglietti per evento
- Filtri per status
- Gestione errori

## Personalizzazione

### Cambiare URL del Backend

Modifica `config.js`:
```javascript
baseURL: 'http://tuo-server:porta/api/v1'
```

Oppure usa variabile d'ambiente:
```bash
export API_BASE_URL=http://tuo-server:porta/api/v1
npm test
```

### Eseguire Test Specifici

```bash
# Solo autenticazione
npm run test:auth

# Solo eventi
npm run test:events

# Solo biglietti
npm run test:tickets

# Solo venue
npm run test:venues
```

## Troubleshooting Comune

### "Connection refused"
- Il backend non è in esecuzione
- Controlla che l'URL in config.js sia corretto

### "404 Not Found" per venue
- Il database potrebbe non avere dati iniziali
- Alcuni test potrebbero essere skippati automaticamente

### Test timeout
- Aumenta il timeout in config.js (default: 10000ms)
- Il backend potrebbe essere lento a rispondere

## Prossimi Passi

1. ✅ Hai installato le dipendenze
2. ✅ Hai avviato il backend
3. ✅ Hai eseguito i test
4. 📚 Leggi il README.md completo per maggiori dettagli
5. 🔧 Personalizza i test per le tue esigenze
6. 🚀 Integra i test nella tua CI/CD pipeline

## Supporto

Per problemi o domande, consulta:
- README.md completo nella stessa directory
- Documentazione del backend TicketBlock
- Codice sorgente dei controller in backend/src/main/java/com/ticketblock/controller/
