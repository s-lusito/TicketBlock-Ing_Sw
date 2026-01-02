# Docker Compose Setup

Setup semplice per far girare TicketBlock con Docker Compose.

## Cosa serve

- Docker
- Docker Compose

## Come far partire tutto

```bash
docker compose up -d
```

## Porte

- Frontend: http://localhost:80
- Backend: http://localhost:8080
- Database: localhost:5432

## Vedere i log

```bash
docker compose logs -f
```

## Fermare tutto

```bash
docker compose down
```

## Ricostruire dopo modifiche al codice

```bash
docker compose up -d --build
```

## Note

- Ganache deve girare in locale separatamente (non è incluso nel docker-compose)
- Il database PostgreSQL ha username `team` e password `password`
