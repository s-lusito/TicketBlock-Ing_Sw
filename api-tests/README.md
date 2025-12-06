# TicketBlock API Test Suite

Questa directory contiene i test JavaScript per testare tutte le API del backend TicketBlock. I test includono sia casi semplici che casi limite per garantire la robustezza dell'applicazione.

## Struttura del Progetto

```
api-tests/
├── config.js                 # Configurazione per i test (URL base, credenziali di test)
├── test-utils.js            # Utility per i test (TestRunner, assertions, axios client)
├── test-runner.js           # Script principale per eseguire tutti i test
├── package.json             # Dipendenze Node.js
├── tests/                   # Directory contenente tutti i test
│   ├── auth.test.js        # Test per autenticazione e registrazione
│   ├── events.test.js      # Test per la gestione degli eventi
│   ├── venues.test.js      # Test per la gestione dei venues
│   ├── tickets.test.js     # Test per acquisto e rivendita biglietti
│   └── event-tickets.test.js # Test per ottenere biglietti di un evento
└── README.md               # Questo file
```

## Prerequisiti

- Node.js (versione 14 o superiore)
- npm (Node Package Manager)
- Backend TicketBlock in esecuzione (di default su http://localhost:8080)

## Installazione

1. Navigare nella directory `api-tests`:
   ```bash
   cd api-tests
   ```

2. Installare le dipendenze:
   ```bash
   npm install
   ```

## Configurazione

Il file `config.js` contiene la configurazione per i test. È possibile modificare:

- **baseURL**: L'URL base del backend API (default: `http://localhost:8080/api/v1`)
- **timeout**: Timeout per le richieste HTTP (default: 10000ms)
- **testUsers**: Credenziali per utenti di test
- **testCreditCard**: Dati carta di credito di test

Per cambiare l'URL del backend, puoi anche usare una variabile d'ambiente:
```bash
export API_BASE_URL=http://localhost:8080/api/v1
```

## Esecuzione dei Test

### Eseguire tutti i test

```bash
npm test
```

Oppure:
```bash
node test-runner.js
```

### Eseguire test specifici

Test di autenticazione:
```bash
npm run test:auth
```

Test degli eventi:
```bash
npm run test:events
```

Test dei venues:
```bash
npm run test:venues
```

Test dei biglietti:
```bash
npm run test:tickets
```

Oppure eseguire direttamente un file di test:
```bash
node tests/auth.test.js
node tests/events.test.js
node tests/venues.test.js
node tests/tickets.test.js
node tests/event-tickets.test.js
```

## Descrizione dei Test

### 1. Authentication API Tests (`auth.test.js`)

**Casi Semplici:**
- Registrazione nuovo utente con dati validi
- Registrazione nuovo organizzatore con dati validi
- Autenticazione con credenziali valide

**Casi Limite:**
- Registrazione con email non valida
- Registrazione con password troppo corta (< 8 caratteri)
- Registrazione con ruolo ADMIN (non permesso)
- Registrazione con ruolo non valido
- Registrazione con campi mancanti
- Registrazione con firstName vuoto o troppo lungo (> 50 caratteri)
- Autenticazione con password errata
- Autenticazione con email non esistente
- Autenticazione con email non valida
- Autenticazione con credenziali vuote

### 2. Event API Tests (`events.test.js`)

**Casi Semplici:**
- Ottenere tutti gli eventi senza filtri
- Ottenere tutti gli eventi con filtro per status
- Creare evento con dati validi (come organizzatore)
- Ottenere evento per ID
- Eliminare evento (come organizzatore)

**Casi Limite:**
- Creare evento con campi mancanti
- Creare evento con nome vuoto o troppo lungo (> 50 caratteri)
- Creare evento con ora di fine prima dell'ora di inizio
- Creare evento con prezzo negativo o zero
- Creare evento con venue ID non esistente
- Ottenere evento con ID non esistente
- Eliminare evento con ID non esistente
- Creare evento senza autenticazione
- Filtro con status non valido

### 3. Venue API Tests (`venues.test.js`)

**Casi Semplici:**
- Ottenere venue per ID valido

**Casi Limite:**
- Ottenere venue con ID non esistente
- Ottenere venue con ID negativo
- Ottenere venue con ID zero
- Ottenere venue con formato ID non valido (stringa)
- Ottenere venue con ID molto grande

### 4. Ticket API Tests (`tickets.test.js`)

**Casi Semplici:**
- Ottenere i propri biglietti (utente autenticato)
- Acquistare biglietti con dati validi
- Rivendere biglietto con ID valido

**Casi Limite:**
- Ottenere biglietti senza autenticazione
- Acquistare biglietti con numero carta di credito non valido
- Acquistare biglietti con formato data scadenza non valido
- Acquistare biglietti con CVV troppo corto o non numerico
- Acquistare biglietti con mappa biglietti vuota
- Acquistare biglietti con campi mancanti
- Acquistare biglietti con nome titolare troppo corto o troppo lungo
- Acquistare biglietti senza autenticazione
- Rivendere biglietto con ID non esistente
- Rivendere biglietto senza autenticazione
- Rivendere biglietto con formato ID non valido

### 5. Event Tickets API Tests (`event-tickets.test.js`)

**Casi Semplici:**
- Ottenere biglietti da evento senza filtri
- Ottenere biglietti con filtro status AVAILABLE
- Ottenere biglietti con filtro status SOLD
- Ottenere biglietti da evento appena creato

**Casi Limite:**
- Ottenere biglietti da evento non esistente
- Ottenere biglietti con filtro status non valido
- Ottenere biglietti da evento con ID negativo
- Ottenere biglietti da evento con ID zero
- Ottenere biglietti con formato ID evento non valido
- Ottenere biglietti con filtro status INVALIDATED

## Output dei Test

Ogni test mostra:
- ✅ **PASS**: Test superato con successo
- ❌ **FAIL**: Test fallito con messaggio di errore, status code e response data

Alla fine dell'esecuzione viene mostrato un riepilogo:
- Numero totale di test eseguiti
- Numero di test superati
- Numero di test falliti
- Percentuale di successo

Esempio di output:
```
============================================================
📋 Test Suite: Authentication API Tests
============================================================
✅ PASS: Register new user with valid data
✅ PASS: Authenticate with valid credentials
❌ FAIL: Register with invalid email format
   Error: Request failed with status code 400
   Status: 400
   Data: { "message": "Not a valid email" }
============================================================
📊 Test Summary
============================================================
Total Tests: 50
✅ Passed: 48
❌ Failed: 2
Success Rate: 96.00%
============================================================
```

## Note Importanti

1. **Backend deve essere in esecuzione**: Assicurati che il backend TicketBlock sia avviato prima di eseguire i test.

2. **Database**: Alcuni test potrebbero fallire se il database non contiene dati iniziali (es. venue con ID 1).

3. **Timestamping**: I test utilizzano timestamp per creare email univoche ed evitare conflitti di utenti duplicati.

4. **Autenticazione**: I test che richiedono autenticazione creano automaticamente nuovi utenti e salvano i token temporaneamente.

5. **Pulizia**: Attualmente i test non puliscono i dati creati nel database. Considera di aggiungere uno script di pulizia se necessario.

## Troubleshooting

### Errore di connessione
Se ottieni errori di connessione:
- Verifica che il backend sia in esecuzione
- Controlla che l'URL in `config.js` sia corretto
- Verifica che non ci siano firewall che bloccano la connessione

### Test falliti
Se molti test falliscono:
- Controlla i log del backend per errori
- Verifica che il database sia inizializzato correttamente
- Assicurati che le dipendenze del backend siano installate

### Timeout
Se i test vanno in timeout:
- Aumenta il valore di `timeout` in `config.js`
- Controlla le performance del backend e del database

## Estendere i Test

Per aggiungere nuovi test:

1. Crea un nuovo file nella directory `tests/` (es. `my-feature.test.js`)
2. Importa `TestRunner` e `assert` da `test-utils.js`
3. Crea test usando `suite.test(description, testFn)`
4. Aggiungi il test suite al `test-runner.js`

Esempio:
```javascript
const { api, TestRunner, assert } = require('../test-utils');

async function runMyTests() {
  const suite = new TestRunner('My Feature Tests');

  suite.test('My test case', async () => {
    const response = await api.get('/my-endpoint');
    assert.equal(response.status, 200);
  });

  await suite.run();
}

module.exports = runMyTests;
```

## Licenza

Questo progetto di test fa parte del progetto TicketBlock.
