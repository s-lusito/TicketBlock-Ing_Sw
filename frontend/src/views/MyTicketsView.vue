<template>
  <div class="container my-tickets-page">
    <div class="page-header">
      <h1>I Miei Biglietti</h1>
      <p class="subtitle">Gestisci i tuoi biglietti e rivendili se necessario.</p>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="tickets.length === 0" class="empty-state">
      <p>Non hai ancora acquistato biglietti.</p>
      <router-link to="/events" class="cta-button">Esplora Eventi</router-link>
    </div>

    <div v-else class="tickets-grid">
      <div v-for="ticket in tickets" :key="ticket.id" class="card ticket-card">
        <div class="ticket-header">
          <span class="ticket-id">{{ ticket.event?.name || '#'+ticket.id }}</span>
          <span :class="['status-badge', ticket.ticketStatus.toLowerCase()]">{{ ticket.seat.sector }}</span>
        </div>
        
        <div class="ticket-body">
          <div class="info-row" v-if="ticket.event">
             <span class="label">Luogo</span>
             <div class="venue-info">
               <span class="venue-name">{{ ticket.event.venue.name }}</span>
               <span class="venue-address">{{ ticket.event.venue.address.street }}, {{ ticket.event.venue.address.city }}</span>
             </div>
          </div>
          <div class="info-row" v-if="ticket.event">
             <span class="label">Data</span>
             <span class="value">{{ new Date(ticket.event.date).toLocaleDateString('it-IT') }} {{ ticket.event.startTime?.slice(0,5) }}</span>
          </div>
          <div class="info-row">
            <span class="label">Posto</span>
            <span class="value">Fila {{ ticket.seat.row }} - Posto {{ ticket.seat.seatNumber }}</span>
          </div>
          <div class="info-row">
            <span class="label">Prezzo</span>
            <span class="value">{{ ticket.price }} €</span>
          </div>
          <div class="info-row">
            <span class="label">Rivendibile</span>
            <span class="value">{{ ticket.resellable ? 'Sì' : 'No' }}</span>
          </div>
        </div>

        <div class="ticket-footer">
          <button 
            v-if="ticket.resellable && ticket.ticketStatus === 'SOLD'" 
            class="resell-btn"
            @click="handleResell(ticket.id)"
            :disabled="resellingId === ticket.id"
          >
            {{ resellingId === ticket.id ? 'In corso...' : 'Rivendi Biglietto' }}
          </button>
          <span v-else-if="ticket.ticketStatus === 'AVAILABLE'" class="info-text">In vendita</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import ticketService from '@/services/ticketService';

const tickets = ref([]);
const loading = ref(true);
const error = ref('');
const resellingId = ref(null);

// Carica i biglietti acquistati dall'utente
const fetchTickets = async () => {
  try {
    const response = await ticketService.getMyTickets();
    tickets.value = response.data;
  } catch (err) {
    error.value = 'Impossibile caricare i tuoi biglietti.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchTickets);

// Gestisce la messa in vendita di un biglietto
const handleResell = async (ticketId) => {
  if (!confirm('Sei sicuro di voler rivendere questo biglietto?')) return;
  
  resellingId.value = ticketId;
  try {
    await ticketService.resellTicket(ticketId);
    // Aggiorna la lista per mostrare il nuovo stato
    await fetchTickets();
    alert('Biglietto messo in vendita con successo!');
  } catch (err) {
    console.error(err);
    alert('Errore durante la rivendita: ' + (err.response?.data?.userMessage || err.response?.data?.detail || err.response?.data?.message || 'Errore sconosciuto'));
  } finally {
    resellingId.value = null;
  }
};
</script>

<style scoped>
.my-tickets-page {
  padding-top: 40px;
  padding-bottom: 60px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.tickets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.ticket-card {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.ticket-id {
  font-weight: 700;
  font-size: 1.1rem;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
}

.status-badge.sold {
  background: #e3f2fd;
  color: #1976d2;
}

.status-badge.available {
  background: #e8f5e9;
  color: #2e7d32;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  align-items: baseline;
}

.venue-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  text-align: right;
}

.venue-name {
  font-weight: 600;
  color: var(--text-primary);
}

.venue-address {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-top: 2px;
}

.info-row .label {
  color: var(--text-secondary);
}

.info-row .value {
  font-weight: 500;
}

.ticket-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.resell-btn {
  width: 100%;
  background-color: #c0392b;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: opacity 0.2s;
}

.resell-btn:hover {
  opacity: 0.9;
}

.resell-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  padding: 60px;
}

.info-text {
  display: block;
  text-align: center;
  color: var(--text-secondary);
  font-style: italic;
}
</style>
