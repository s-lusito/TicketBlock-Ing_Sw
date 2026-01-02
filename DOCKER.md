# Docker Compose Setup

Questa guida spiega come utilizzare Docker Compose per eseguire l'intera applicazione TicketBlock con tutti i suoi componenti.

## Componenti

Il docker-compose orchestra tre servizi principali:

1. **Database (PostgreSQL)**: Database PostgreSQL per la persistenza dei dati
2. **Backend (Spring Boot)**: API REST sviluppata con Spring Boot e Java 21
3. **Frontend (Vue.js)**: Interfaccia utente sviluppata con Vue 3 e Vite

## Prerequisiti

- Docker (versione 20.10 o superiore)
- Docker Compose (versione 2.0 o superiore)

## Avvio dei Servizi

Per avviare tutti i servizi, eseguire il seguente comando nella directory root del progetto:

```bash
docker compose up -d
```

Questo comando:
- Costruirà le immagini Docker per backend e frontend se non esistono
- Avvierà tutti e tre i servizi in background
- Il database sarà pronto quando il healthcheck avrà successo
- Il backend si avvierà solo dopo che il database sarà pronto
- Il frontend si avvierà dopo il backend

## Porte

I servizi sono accessibili alle seguenti porte:

- **Frontend**: http://localhost:80
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432

## Visualizzare i Log

Per visualizzare i log di tutti i servizi:

```bash
docker compose logs -f
```

Per visualizzare i log di un servizio specifico:

```bash
docker compose logs -f frontend
docker compose logs -f backend
docker compose logs -f db
```

## Fermare i Servizi

Per fermare tutti i servizi:

```bash
docker compose down
```

Per fermare i servizi e rimuovere anche i volumi (attenzione: questo cancellerà tutti i dati del database):

```bash
docker compose down -v
```

## Ricostruire le Immagini

Se si apportano modifiche al codice, è necessario ricostruire le immagini:

```bash
docker compose up -d --build
```

Per ricostruire un singolo servizio:

```bash
docker compose up -d --build backend
docker compose up -d --build frontend
```

## Variabili d'Ambiente

Il database utilizza le seguenti credenziali (configurate in docker-compose.yml):

- **POSTGRES_USER**: team
- **POSTGRES_PASSWORD**: password
- **POSTGRES_DB**: ticketblock

Il backend si connette automaticamente al database utilizzando queste credenziali tramite variabili d'ambiente.

## Troubleshooting

### Il backend non si connette al database

Verificare che il servizio db sia in esecuzione e healthy:

```bash
docker compose ps
```

### Errori di build

Pulire tutte le immagini e ricostruire da zero:

```bash
docker compose down
docker compose build --no-cache
docker compose up -d
```

### Il frontend non carica

Verificare che nginx sia configurato correttamente:

```bash
docker compose logs frontend
```

### Accedere ai container

Per accedere a un container in esecuzione:

```bash
docker compose exec backend sh
docker compose exec frontend sh
docker compose exec db psql -U team -d ticketblock
```

## Sviluppo

Durante lo sviluppo, è possibile:

1. Mantenere il database in esecuzione: `docker compose up -d db`
2. Eseguire backend e frontend localmente per un ciclo di sviluppo più rapido
3. Il backend locale può connettersi al database Docker usando `localhost:5432`
4. Il frontend locale può proxy verso il backend locale tramite vite.config.js

## Architettura di Rete

Tutti i servizi sono connessi alla rete `app-network`. Questo permette:

- Al backend di comunicare con il database usando il nome del servizio `db`
- Al frontend (nginx) di fare proxy delle richieste API al backend usando `backend:8080`
- Isolamento dalla rete host mantenendo le porte esposte dove necessario
