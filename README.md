# TicketBlock 🎟️

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white)
![Ethereum](https://img.shields.io/badge/Ethereum-3C3C3D?style=for-the-badge&logo=Ethereum&logoColor=white)
![Solidity](https://img.shields.io/badge/Solidity-%23363636.svg?style=for-the-badge&logo=solidity&logoColor=white)
![Vue.js](https://img.shields.io/badge/vuejs-%2335495e.svg?style=for-the-badge&logo=vuedotjs&logoColor=%234FC08D)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

**TicketBlock** è un sistema di biglietteria **Web 2.5** progettato per risolvere le problematiche del mercato tradizionale (bagarinaggio, contraffazione) sfruttando la trasparenza della Blockchain, ma mantenendo l'usabilità di una classica applicazione web.

> Progetto realizzato per il corso di Ingegneria del Software del Politecnico di Bari, sviluppato da Novembre 2025 a Gennaio 2026.

---

## 🎯 Obiettivo
Il sistema punta a creare una piattaforma decentralizzata che garantisca:
* ❌ **Anti-Bagarinaggio:** Limiti all'acquisto e rivendita controllata.
* ❌ **Anti-Contraffazione:** Ogni biglietto è un NFT unico su blockchain.
* ✅ **Semplicità:** L'utente finale non deve gestire wallet o crypto, il sistema astrae la complessità.

## 🚀 Funzionalità Principali

* **NFT Tickets:** Ogni biglietto è un token ERC-721 (custom) unico e tracciabile.
* **Acquisto Limitato:** Massimo 4 biglietti per evento per utente per prevenire l'accaparramento.
* **Rivendita Controllata:** Possibilità di assicurarsi la possibilità di rivendere il biglietto pagando una fee del 10% sul prezzo(smart contract enforcement).
* **Gestione Eventi:** Dashboard per gli organizzatori per creare eventi e monitorare le vendite.
## 🛠️ Tech Stack

L'architettura segue il pattern **Web 2.5** con tre livelli principali:

## ⚙️ Installazione e Avvio
Il progetto è containerizzato con **Docker**, ma richiede un'istanza locale di Ganache sulla macchina host.

### Prerequisiti
* Docker & Docker Compose
* Ganache (GUI o CLI)
* Node.js & Truffle

### Setup Rapido
1.  **Avvia Ganache:** Crea un nuovo workspace sulla porta `7545` (o quella configurata).
2.  **Deploy dei Contratti:**
    ```bash
    cd blockchain
    truffle migrate --reset
    ```
    Copia l'indirizzo del contratto deployato.*
3.  **Configurazione Docker Compose:**
    Apri il file `docker-compose.yml` e aggiorna le variabili d'ambiente per il servizio `backend` con i dati di Ganache:
    ```yaml
    environment:
      GANACHE_URL: "http://host.docker.internal:7545"
      PRIVATE_KEY: "0x..."
      CONTRACT_ADDRESS: "0x..."
    ```
4.  **Avvia l'applicazione:**
    ```bash
    docker-compose up --build
    ```

## 👥 Autori

* **Samuele Giuseppe Lusito**
* **Enzo Filippo Rubino**
* **Davis Dileo**

## 📄 Licenza

Questo progetto è distribuito sotto licenza **MIT**.
