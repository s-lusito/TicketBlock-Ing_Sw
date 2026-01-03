# Diagrammi UML in PlantUML - Sistema TicketBlock

Questa directory contiene tutti i diagrammi UML del sistema TicketBlock in formato PlantUML (.puml).

## Contenuti

### Diagrammi di Sequenza

1. **sequence-ticket-purchase.puml** - Diagramma di sequenza per l'acquisto di biglietti
   - Gestione fee opzionale per rivendita
   - Verifica limiti per utente
   - Integrazione blockchain (mint/transfer NFT)
   - **Gestione errori blockchain**

2. **sequence-ticket-resale.puml** - Diagramma di sequenza per la rivendita di biglietti
   - Verifica proprietà su blockchain
   - Controllo rivendibilità
   - Pubblicazione evento rivendita
   - **Gestione errori blockchain**

3. **sequence-ticket-invalidation.puml** - Diagramma di sequenza per l'invalidazione di biglietti
   - Verifica proprietà
   - Burning NFT su blockchain
   - Aggiornamento stato biglietto
   - **Gestione errori blockchain**

4. **sequence-user-registration.puml** - Diagramma di sequenza per la registrazione utente
   - Allocazione wallet blockchain
   - Hashing password con BCrypt
   - Generazione token JWT

5. **sequence-event-creation.puml** - Diagramma di sequenza per la creazione di eventi
   - Validazione date e disponibilità venue
   - Generazione automatica biglietti
   - Impostazione stato vendita

**Nota**: In tutti i diagrammi di sequenza, il frontend è rappresentato come attore "Client" (Vue.js).

### Diagrammi delle Classi

1. **class-diagram-entities.puml** - Entità di dominio backend
   - 8 entità principali (User, Event, Ticket, Venue, Row, Seat, Wallet, Address)
   - 4 enumerazioni (EventSaleStatus, TicketStatus, Role, RowSector)
   - Relazioni complete con cardinalità

2. **class-diagram-architecture.puml** - Architettura completa del sistema backend
   - Controller Layer (6 controller REST)
   - Service Layer (7 servizi)
   - Repository Layer (7 repository)
   - Blockchain Integration (TicketContract, Web3Config)
   - Dipendenze tra livelli

3. **class-diagram-frontend.puml** - Architettura frontend Vue.js
   - Componenti Vue per tutte le pagine
   - Vuex Store per gestione stato globale
   - Servizi API per comunicazione con backend
   - Router con navigation guards
   - Modelli DTOs e utilities

### Diagramma dei Casi d'Uso

1. **use-case-diagram.puml** - Casi d'uso del sistema
   - 3 attori (Client/Frontend, Organizer, System Scheduler)
   - 14 casi d'uso principali
   - Relazioni include/extend

## Come Visualizzare

### Opzione 1: PlantUML Online
Visita [PlantUML Server](https://www.plantuml.com/plantuml/uml/) e carica i file .puml

### Opzione 2: VS Code
1. Installa l'estensione "PlantUML" di jebbs
2. Apri un file .puml
3. Usa `Alt+D` per visualizzare l'anteprima

### Opzione 3: IntelliJ IDEA
1. Installa il plugin "PlantUML integration"
2. Apri un file .puml
3. L'anteprima viene mostrata automaticamente

### Opzione 4: Riga di comando
```bash
# Installa PlantUML
sudo apt-get install plantuml

# Genera immagine PNG
plantuml sequence-ticket-purchase.puml

# Genera immagine SVG
plantuml -tsvg sequence-ticket-purchase.puml

# Genera tutti i diagrammi
plantuml *.puml
```

## Generazione Immagini

Per generare tutte le immagini in formato PNG:
```bash
cd docs/plantuml
plantuml *.puml
```

Per generare in formato SVG (scalabile):
```bash
cd docs/plantuml
plantuml -tsvg *.puml
```

## Note

- Tutti i diagrammi sono documentati in italiano
- I nomi di classi, metodi e variabili sono in inglese per coerenza con il codice
- I diagrammi sono stati semplificati rispetto alla versione Mermaid per maggiore chiarezza
- Le versioni Mermaid sono disponibili nella directory padre `docs/`

## Differenze con Versione Mermaid

La versione PlantUML presenta:
- Sintassi standard PlantUML per massima compatibilità
- Diagrammi delle classi divisi in file separati per migliore manutenibilità
- Rimozione di dettagli implementativi non essenziali nei diagrammi di sequenza
- Temi e stili ottimizzati per la stampa

## Integrazione nella Relazione

Questi diagrammi possono essere facilmente integrati nella relazione tecnica LaTeX:
1. Genera le immagini in formato PDF usando: `plantuml -tpdf *.puml`
2. Includi nelle sezioni appropriate della relazione in `docs/latex/`
