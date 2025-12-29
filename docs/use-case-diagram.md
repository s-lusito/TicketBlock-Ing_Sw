# Diagramma dei Casi d'Uso UML - Sistema TicketBlock

Questo diagramma mostra i principali casi d'uso e attori nel sistema di biglietteria TicketBlock.

```mermaid
graph TB
    subgraph TicketBlock System
        UC1[Create Event]
        UC2[Remove Event]
        UC3[View Events]
        UC4[View Event Details]
        UC5[Purchase Ticket]
        UC6[Resell Ticket]
        UC7[Invalidate Ticket]
        UC8[View My Tickets]
        UC9[View My Events]
        UC10[View Event Sales Details]
        UC11[Create Venue]
        UC12[Register User]
        UC13[Login]
        UC14[Update Event Sale Status]
    end
    
    User((User))
    Organizer((Organizer))
    System((System Scheduler))
    
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7
    User --> UC8
    User --> UC12
    User --> UC13
    
    Organizer --> UC1
    Organizer --> UC2
    Organizer --> UC9
    Organizer --> UC10
    Organizer --> UC11
    
    System --> UC14[Update Event Sale Status]
    
    UC1 -.includes.-> UC3
    UC5 -.includes.-> UC4
    UC9 -.extends.-> UC3
    UC10 -.extends.-> UC4
    
    style UC1 fill:#e1f5ff
    style UC2 fill:#e1f5ff
    style UC5 fill:#fff4e1
    style UC6 fill:#fff4e1
    style UC7 fill:#fff4e1
    style UC8 fill:#fff4e1
    style UC11 fill:#e1f5ff
```

## Descrizione Attori

### User (Utente)
Un utente registrato che può:
- Visualizzare gli eventi disponibili
- Acquistare biglietti (con o senza opzione di rivendita)
- Visualizzare i propri biglietti posseduti
- Rivendere biglietti acquistati (se rivendibili)
- Invalidare i propri biglietti (es. quando entra all'evento)

### Organizer (Organizzatore)
Un tipo speciale di utente (con ruolo ORGANIZER) che può:
- Creare nuovi eventi
- Rimuovere eventi (se non sono stati venduti biglietti)
- Creare venue
- Visualizzare i propri eventi organizzati
- Visualizzare i dettagli delle vendite dei propri eventi

### System Scheduler (Schedulatore di Sistema)
Processo automatico di sistema che:
- Aggiorna gli stati di vendita degli eventi in base alle date
- Apre le vendite quando arriva la data di inizio vendita
- Chiude le vendite un giorno prima dell'evento
- Viene eseguito quotidianamente a mezzanotte

## Casi d'Uso Principali

1. **Create Event**: L'organizzatore crea un nuovo evento con venue, date e prezzi
2. **Purchase Ticket**: L'utente acquista uno o più biglietti per un evento
3. **Resell Ticket**: L'utente rimette in vendita un biglietto acquistato in precedenza
4. **Invalidate Ticket**: L'utente segna un biglietto come usato/invalido (brucia l'NFT sulla blockchain)
5. **View Event Sales Details**: L'organizzatore visualizza le statistiche sulle vendite dei biglietti dei propri eventi

## Regole di Business

- Gli utenti possono acquistare massimo 4 biglietti per evento
- I biglietti possono essere acquistati con una fee opzionale (configurabile, attualmente 10%) per renderli rivendibili
- Gli eventi devono avere inizio vendita almeno 3 giorni prima della data dell'evento
- Le vendite si chiudono automaticamente 1 giorno prima dell'evento
- Gli organizzatori non possono eliminare eventi con biglietti venduti
- Ogni biglietto è rappresentato come un NFT sulla blockchain
