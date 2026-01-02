<template>
  <div class="container event-detail-page">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <router-link to="/events">Torna agli Eventi</router-link>
    </div>

    <div v-else class="event-content">
      <div class="event-header">
        <div class="header-content">
          <span class="event-badge">In Arrivo</span>
          <h1 class="event-title">{{ event.name }}</h1>
          <div class="event-meta">
            <div class="meta-item">
              <span class="label">Data</span>
              <span class="value">{{ new Date(event.date).toLocaleDateString('it-IT', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}</span>
            </div>
            <div class="meta-item">
              <span class="label">Ora</span>
              <span class="value">{{ event.startTime?.slice(0,5) }} - {{ event.endTime?.slice(0,5) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="grid-layout">
        <div class="main-column">
          <section class="section">
            <h2>Informazioni sull'evento</h2>
            <p class="description">{{ event.description || 'Nessuna descrizione disponibile.' }}</p>
          </section>

          <section class="section" v-if="event.venue">
            <h2>Luogo</h2>
            <div class="venue-card">
              <h3>{{ event.venue.name }}</h3>
              <p>{{ event.venue.city }}</p>
            </div>
          </section>
        </div>

        <div class="sidebar">
          <div class="ticket-card card">
            <h3>Acquista Biglietti</h3>
            <div class="ticket-option">
              <div class="ticket-info">
                <span class="ticket-name">Standard</span>
                <span class="ticket-price">{{ Number(event.standardTicketPrice).toFixed(2) }}€</span>
              </div>
              <button v-if="!isOrganizer" class="buy-btn primary" @click="openPurchaseModal('STANDARD')">Acquista Standard</button>
              <button v-else class="buy-btn" style="background-color: #333; color: #fff; cursor: not-allowed; opacity: 0.7;" disabled>Acquisto non disponibile</button>
            </div>
            <div class="ticket-option">
              <div class="ticket-info">
                <span class="ticket-name">VIP</span>
                <span class="ticket-price">{{ Number(event.vipTicketPrice).toFixed(2) }}€</span>
              </div>
              <button v-if="!isOrganizer" class="buy-btn primary" @click="openPurchaseModal('VIP')">Acquista VIP</button>
              <button v-else class="buy-btn" style="background-color: #333; color: #fff; cursor: not-allowed; opacity: 0.7;" disabled>Acquisto non disponibile</button>
            </div>
          </div>

          <div v-if="isOrganizer" class="admin-actions card">
            <h3>Gestione Evento</h3>
            <button class="delete-btn" @click="handleDelete">Elimina Evento</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Purchase Modal -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content card">
        <h3>Completa Acquisto</h3>
        <p>Stai acquistando un biglietto <strong>{{ selectedTicketType }}</strong></p>
        
        <form @submit.prevent="handlePurchase">
          <div class="form-group">
            <label>Numero Carta</label>
            <input type="text" v-model="payment.cardNumber" placeholder="1234 5678 1234 5678" required />
          </div>
          <div class="row">
            <div class="form-group">
              <label>Scadenza</label>
              <input type="text" v-model="payment.expiration" placeholder="MM/YY" required />
            </div>
            <div class="form-group">
              <label>CVV</label>
              <input type="text" v-model="payment.cvv" placeholder="123" required />
            </div>
          </div>
          <div class="form-group">
            <label>Intestatario</label>
            <input type="text" v-model="payment.holderName" placeholder="Mario Rossi" required />
          </div>
          
          <div class="checkbox-container">
            <label class="checkbox-label">
              <input type="checkbox" v-model="payment.resellable" />
              <span>Abilita rivendita (+10% fee)</span>
            </label>
          </div>

          <div class="total-section">
            <span class="total-label">Totale da pagare:</span>
            <span class="total-amount">{{ Number(totalPrice).toFixed(2) }}€</span>
          </div>

          <div class="modal-actions">
            <button type="button" class="cancel-btn" @click="closeModal">Annulla</button>
            <button type="submit" class="submit-btn" :disabled="processing">
              {{ processing ? 'Elaborazione...' : 'Paga Ora' }}
            </button>
          </div>
          <p v-if="purchaseError" class="error-msg">{{ purchaseError }}</p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import eventService from '@/services/eventService';
import ticketService from '@/services/ticketService';
import authService from '@/services/authService';

const route = useRoute();
const router = useRouter();
const event = ref(null);
const loading = ref(true);
const error = ref('');
const isOrganizer = computed(() => authService.getUserRole() === 'ORGANIZER');

// Elimina l'evento (solo organizzatori)
const handleDelete = async () => {
  if (!confirm('Sei sicuro di voler eliminare questo evento? Questa azione è irreversibile.')) return;
  
  try {
    await eventService.deleteEvent(event.value.id);
    alert('Evento eliminato con successo.');
    router.push('/events');
  } catch (err) {
    console.error(err);
    alert('Errore durante l\'eliminazione dell\'evento.');
  }
};

// Modal & Purchase State
const showModal = ref(false);
const selectedTicketType = ref('');
const processing = ref(false);
const purchaseError = ref('');
const availableTickets = ref([]);

const payment = ref({
  cardNumber: '',
  expiration: '',
  cvv: '',
  holderName: '',
  resellable: false
});

// Calcola il prezzo totale (con maggiorazione per l'opzione rivendibilità)
const totalPrice = computed(() => {
  if (!event.value || !selectedTicketType.value) return 0;
  const basePrice = selectedTicketType.value === 'STANDARD' ? event.value.standardTicketPrice : event.value.vipTicketPrice;
  return payment.value.resellable ? basePrice * 1.1 : basePrice;
});

onMounted(async () => {
  try {
    const eventId = route.params.id;
    // Carica dettagli evento e biglietti disponibili in parallelo
    const [eventRes, ticketsRes] = await Promise.all([
      eventService.getEventById(eventId),
      ticketService.getEventTickets(eventId, 'AVAILABLE')
    ]);
    event.value = eventRes.data;
    availableTickets.value = ticketsRes.data;
  } catch (err) {
    error.value = 'Impossibile caricare i dettagli dell\'evento.';
    console.error(err);
  } finally {
    loading.value = false;
  }
});

// Apre il modale di acquisto e aggiorna la disponibilità dei biglietti
const openPurchaseModal = async (type) => {
  selectedTicketType.value = type;
  purchaseError.value = '';
  // Refetch tickets to ensure availability is up to date
  processing.value = true;
  try {
     const res = await ticketService.getEventTickets(route.params.id, 'AVAILABLE');
     availableTickets.value = res.data;
     console.log('Tickets refreshed:', availableTickets.value);
  } catch(e) {
     console.error('Failed to refresh tickets', e);
  } finally {
     processing.value = false;
     showModal.value = true;
  }
};

const closeModal = () => {
  showModal.value = false;
  payment.value = { cardNumber: '', expiration: '', cvv: '', holderName: '', resellable: false };
};

// Gestisce l'acquisto effettivo del biglietto
const handlePurchase = async () => {
  processing.value = true;
  purchaseError.value = '';

  // Trova un biglietto del settore selezionato tra quelli disponibili
  // Nota: L'API backend restituisce i biglietti con 'seat' che ha 'sector'. 
  // Dobbiamo filtrare i biglietti disponibili in base al settore che corrisponde al tipo selezionato.
  
  console.log('Searching for ticket type:', selectedTicketType.value);
  console.log('Available tickets:', availableTickets.value);

  const ticketToBuy = availableTickets.value.find(t => 
    t.seat && 
    t.seat.sector && 
    t.seat.sector.toUpperCase() === selectedTicketType.value.toUpperCase()
  );

  if (!ticketToBuy) {
    console.warn(`No ticket found for sector ${selectedTicketType.value}. Available sectors:`, availableTickets.value.map(t => t.seat?.sector));
    purchaseError.value = `Nessun biglietto ${selectedTicketType.value} disponibile.`;
    processing.value = false;
    return;
  }

  const payload = {
    ticketFeeMap: { [ticketToBuy.id]: payment.value.resellable },
    creditCardNumber: payment.value.cardNumber.replace(/\s/g, ''),
    expirationDate: payment.value.expiration,
    cvv: payment.value.cvv,
    cardHolderName: payment.value.holderName
  };

  try {
    await ticketService.purchaseTickets(payload);
    alert('Acquisto completato con successo!');
    closeModal();
    router.push('/my-tickets'); // Redirect to My Tickets (to be implemented)
  } catch (err) {
    console.error(err);
    if (err.response?.data?.errors && Array.isArray(err.response.data.errors)) {
      // Backend validation errors
      purchaseError.value = err.response.data.errors.map(e => e.message).join('\n');
    } else {
      purchaseError.value = err.response?.data?.userMessage || err.response?.data?.detail || 'Errore durante l\'acquisto.';
    }
  } finally {
    processing.value = false;
  }
};
</script>

<style scoped>
.event-detail-page {
  padding-top: 40px;
  padding-bottom: 60px;
}

.event-header {
  margin-bottom: 40px;
  text-align: center;
}

.event-badge {
  background: rgba(0, 113, 227, 0.1);
  color: var(--accent-color);
  padding: 4px 12px;
  border-radius: 980px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 16px;
  display: inline-block;
}

.event-title {
  font-size: 3.5rem;
  line-height: 1.1;
  margin-bottom: 24px;
}

.event-meta {
  display: flex;
  justify-content: center;
  gap: 40px;
  color: var(--text-secondary);
}

.meta-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.meta-item .label {
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.meta-item .value {
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--text-primary);
}

.grid-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 40px;
  margin-top: 60px;
}

.section {
  margin-bottom: 40px;
}

.section h2 {
  font-size: 1.8rem;
  margin-bottom: 20px;
}

.description {
  font-size: 1.1rem;
  line-height: 1.6;
  color: var(--text-secondary);
}

.venue-card {
  background: var(--bg-secondary);
  padding: 24px;
  border-radius: 16px;
}

.ticket-card {
  position: sticky;
  top: 80px;
}

.ticket-card h3 {
  margin-bottom: 24px;
}

.ticket-option {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.ticket-option:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.ticket-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-weight: 500;
}

.buy-btn {
  width: 100%;
}



.admin-actions {
  margin-top: 24px;
  border: 1px solid rgba(255, 59, 48, 0.2);
}

.admin-actions h3 {
  margin-bottom: 16px;
  font-size: 1.1rem;
  color: var(--text-primary);
}

.delete-btn {
  width: 100%;
  background-color: #fff;
  color: var(--error-color);
  border: 1px solid var(--error-color);
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}

.delete-btn:hover {
  background-color: var(--error-color);
  color: white;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  width: 100%;
  max-width: 400px;
  padding: 30px;
}

.modal-content h3 {
  margin-bottom: 20px;
  text-align: center;
}

.checkbox-container {
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  margin: 0;
}

.total-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-top: 16px;
  border-top: 1px solid rgba(0,0,0,0.1);
  font-size: 1.1rem;
}

.total-amount {
  font-weight: 700;
  color: var(--accent-color);
  font-size: 1.3rem;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .grid-layout {
    grid-template-columns: 1fr;
  }
  
  .event-title {
    font-size: 2.5rem;
  }
}
</style>
