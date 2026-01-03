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
            <p class="card-subtitle">Scegli fino a 4 biglietti da acquistare</p>
            <button v-if="!isOrganizer" class="buy-btn primary large-btn" @click="openPurchaseModal">
              Seleziona Biglietti
            </button>
            <button v-else class="buy-btn" style="background-color: #333; color: #fff; cursor: not-allowed; opacity: 0.7;" disabled>
              Acquisto non disponibile
            </button>
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
        <button class="close-modal-btn" @click="closeModal">✕</button>
        <h3>Seleziona Biglietti</h3>
        <p class="modal-subtitle">Scegli fino a {{ remainingTicketsAllowed }} biglietti da acquistare ({{ userOwnedTicketsCount }} già posseduti)</p>

        <!-- Selected Tickets Summary -->
        <div v-if="selectedTicketIds.length > 0" class="selected-summary">
          <h4>Biglietti Selezionati ({{ selectedTicketIds.length }})</h4>
          <div class="selected-list">
            <div v-for="ticketId in selectedTicketIds" :key="ticketId" class="selected-ticket">
              <span>{{ getTicketInfo(ticketId) }}</span>
              <button type="button" class="remove-btn" @click="removeTicket(ticketId)">✕</button>
            </div>
          </div>
        </div>

        <!-- Available Tickets -->
        <div class="tickets-list-container">
          <h4>Biglietti Disponibili</h4>
          <div v-if="remainingTicketsAllowed > 0 && filteredAvailableTickets.length > 0" class="tickets-table">
            <div class="tickets-header">
              <div class="col-type">Tipo</div>
              <div class="col-row">Fila</div>
              <div class="col-seat">Posto</div>
              <div class="col-price">Prezzo</div>
              <div class="col-action">Seleziona</div>
            </div>
            <div v-for="ticket in filteredAvailableTickets" :key="ticket.id" class="ticket-row">
              <div class="col-type">
                <span class="badge" :class="ticket.seat.sector.toLowerCase()">{{ ticket.seat.sector }}</span>
              </div>
              <div class="col-row">{{ ticket.seat.row }}</div>
              <div class="col-seat">{{ ticket.seat.seatNumber }}</div>
              <div class="col-price">€{{ Number(ticket.price).toFixed(2) }}</div>
              <div class="col-action">
                <button
                  type="button"
                  class="select-ticket-btn"
                  :disabled="selectedTicketIds.includes(ticket.id) || selectedTicketIds.length >= remainingTicketsAllowed"
                  @click="selectTicket(ticket.id)"
                >
                  {{ selectedTicketIds.includes(ticket.id) ? '✓ Selezionato' : 'Seleziona' }}
                </button>
              </div>
            </div>
          </div>
          <div v-else-if="remainingTicketsAllowed <= 0" class="no-tickets">
            <p>⚠️ Hai già raggiunto il limite massimo di 4 biglietti per questo evento.</p>
          </div>
          <div v-else class="no-tickets">
            <p>Nessun biglietto disponibile</p>
          </div>
        </div>

        <!-- Payment Form -->
        <form @submit.prevent="handlePurchase" v-if="selectedTicketIds.length > 0">
          <div class="payment-section">
            <h4>Dettagli Pagamento</h4>
            <div class="form-group">
              <label>Numero Carta</label>
              <input
                type="text"
                v-model="payment.cardNumber"
                placeholder="1234 5678 1234 5678"
                :required="!payment.skipPayment"
                :disabled="payment.skipPayment"
              />
            </div>
            <div class="row">
              <div class="form-group">
                <label>Scadenza</label>
                <input
                  type="text"
                  v-model="payment.expiration"
                  placeholder="MM/YY"
                  :required="!payment.skipPayment"
                  :disabled="payment.skipPayment"
                />
              </div>
              <div class="form-group">
                <label>CVV</label>
                <input
                  type="text"
                  v-model="payment.cvv"
                  placeholder="123"
                  :required="!payment.skipPayment"
                  :disabled="payment.skipPayment"
                />
              </div>
            </div>
            <div class="form-group">
              <label>Intestatario</label>
              <input
                type="text"
                v-model="payment.holderName"
                placeholder="Mario Rossi"
                :required="!payment.skipPayment"
                :disabled="payment.skipPayment"
              />
            </div>

            <div class="checkbox-container">
              <label class="checkbox-label">
                <input type="checkbox" v-model="payment.resellable" />
                <span>Abilita rivendita (+10% fee)</span>
              </label>
            </div>

            <!-- Debug: skip payment -->
            <div class="checkbox-container">
              <label class="checkbox-label">
                <input type="checkbox" v-model="payment.skipPayment" />
                <span>Salta inserimento dati pagamento (debug — invia dati mock validi)</span>
              </label>
            </div>

            <p v-if="payment.skipPayment" class="debug-note" style="font-size:0.9rem; color:#555; margin-top:8px;">
              Modalità debug attiva: verranno inviati dati di pagamento mock (non inserire dati reali).
            </p>
          </div>

          <div class="total-section">
            <span class="total-label">Totale da pagare ({{ selectedTicketIds.length }} {{ selectedTicketIds.length === 1 ? 'biglietto' : 'biglietti' }}):</span>
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

        <div v-else class="no-selection-msg">
          <p>Seleziona almeno un biglietto per continuare</p>
        </div>
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
const processing = ref(false);
const purchaseError = ref('');
const availableTickets = ref([]);
const selectedTicketIds = ref([]);
const userOwnedTicketsCount = ref(0);
const maxTicketsAllowed = 4;

const payment = ref({
  cardNumber: '',
  expiration: '',
  cvv: '',
  holderName: '',
  resellable: false
});

// Add debug flag to skip real payment entry
payment.value.skipPayment = false;

// Computed properties
const filteredAvailableTickets = computed(() => {
  return availableTickets.value
    .filter(t => t.ticketStatus === 'AVAILABLE')
    .sort((a, b) => {
      // Ordina per fila, poi per numero posto
      if (a.seat.row !== b.seat.row) {
        return a.seat.row.localeCompare(b.seat.row);
      }
      return a.seat.seatNumber - b.seat.seatNumber;
    });
});

// Calcola i biglietti ancora acquistabili
const remainingTicketsAllowed = computed(() => {
  return maxTicketsAllowed - userOwnedTicketsCount.value;
});

// Calcola il prezzo totale basato sui biglietti selezionati
const totalPrice = computed(() => {
  let totalBasePrice = 0;

  selectedTicketIds.value.forEach(id => {
    const ticket = availableTickets.value.find(t => t.id === id);
    if (ticket) {
      totalBasePrice += Number(ticket.price);
    }
  });

  return payment.value.resellable ? totalBasePrice * 1.1 : totalBasePrice;
});

// Funzioni per gestire la selezione
const selectTicket = (ticketId) => {
  const canSelect = !selectedTicketIds.value.includes(ticketId) &&
                   selectedTicketIds.value.length < remainingTicketsAllowed.value;
  if (canSelect) {
    selectedTicketIds.value.push(ticketId);
  }
};

const removeTicket = (ticketId) => {
  selectedTicketIds.value = selectedTicketIds.value.filter(id => id !== ticketId);
};

const getTicketInfo = (ticketId) => {
  const ticket = availableTickets.value.find(t => t.id === ticketId);
  if (ticket) {
    return `${ticket.seat.sector} - Fila ${ticket.seat.row} Posto ${ticket.seat.seatNumber} (€${Number(ticket.price).toFixed(2)})`;
  }
  return 'Biglietto non trovato';
};

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

// Apre il modale di acquisto e carica i biglietti disponibili
const openPurchaseModal = async () => {
  purchaseError.value = '';
  selectedTicketIds.value = [];
  processing.value = true;
  try {
     // Carica i biglietti disponibili
     const res = await ticketService.getEventTickets(route.params.id, 'AVAILABLE');
     availableTickets.value = res.data;

     // Carica i biglietti dell'utente per questo evento
     const userTickets = await ticketService.getMyTickets();
     const eventId = parseInt(route.params.id);
     userOwnedTicketsCount.value = userTickets.data.filter(t => t.event.id === eventId).length;

     console.log('Biglietti disponibili:', availableTickets.value);
     console.log('Biglietti già posseduti per questo evento:', userOwnedTicketsCount.value);
     console.log('Biglietti ancora acquistabili:', remainingTicketsAllowed.value);
  } catch(e) {
     purchaseError.value = 'Errore nel caricamento dei biglietti disponibili.';
     console.error('Errore caricamento biglietti', e);
  } finally {
     processing.value = false;
     showModal.value = true;
  }
};

const closeModal = () => {
  showModal.value = false;
  payment.value = { cardNumber: '', expiration: '', cvv: '', holderName: '', resellable: false, skipPayment: false };
  selectedTicketIds.value = [];
};

// Gestisce l'acquisto effettivo dei biglietti
const handlePurchase = async () => {
  processing.value = true;
  purchaseError.value = '';

  // Verifica che siano stati selezionati biglietti
  if (selectedTicketIds.value.length === 0) {
    purchaseError.value = 'Seleziona almeno un biglietto.';
    processing.value = false;
    return;
  }

  // Verifica che i biglietti siano ancora disponibili
  const selectedTickets = availableTickets.value.filter(t => selectedTicketIds.value.includes(t.id));
  if (selectedTickets.length !== selectedTicketIds.value.length) {
    purchaseError.value = 'Uno o più biglietti non sono più disponibili.';
    processing.value = false;
    return;
  }

  // Costruisci la mappa di ticket ID → flag di rivendibilità
  const ticketFeeMap = {};
  selectedTicketIds.value.forEach(id => {
    ticketFeeMap[id] = payment.value.resellable;
  });

  // Build payload and override with mock payment if skipPayment is active
  const payload = {
    ticketFeeMap: ticketFeeMap,
    creditCardNumber: payment.value.cardNumber.replace(/\s/g, ''),
    expirationDate: payment.value.expiration,
    cvv: payment.value.cvv,
    cardHolderName: payment.value.holderName
  };

  if (payment.value.skipPayment) {
    // Mock valid payment data for debug purposes
    payload.creditCardNumber = '4242424242424242';
    payload.expirationDate = '12/34';
    payload.cvv = '123';
    payload.cardHolderName = 'Debug User';
  }

  try {
    await ticketService.purchaseTickets(payload);
    alert(`Acquisto di ${selectedTicketIds.value.length} ${selectedTicketIds.value.length === 1 ? 'biglietto' : 'biglietti'} completato con successo!`);
    closeModal();
    router.push('/my-tickets');
  } catch (err) {
    console.error(err);
    if (err.response?.data?.errors && Array.isArray(err.response.data.errors)) {
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
  max-width: 500px;
  padding: 30px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.close-modal-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--text-secondary);
  transition: color 0.2s;
}

.close-modal-btn:hover {
  color: var(--text-primary);
}

.modal-content h3 {
  margin-bottom: 5px;
  text-align: center;
}

.modal-subtitle {
  text-align: center;
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 20px;
}

.card-subtitle {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 15px;
}

.large-btn {
  padding: 12px 20px !important;
  font-size: 1.05rem !important;
}

/* Selected Tickets Summary */
.selected-summary {
  background: #e8f4f8;
  border: 2px solid #0071E3;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.selected-summary h4 {
  margin-bottom: 10px;
  color: #0071E3;
}

.selected-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.selected-ticket {
  background: white;
  padding: 8px 12px;
  border-radius: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: #333;
}

.remove-btn {
  background: none;
  border: none;
  color: #d32f2f;
  cursor: pointer;
  font-size: 1.2rem;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-btn:hover {
  background: #ffebee;
  border-radius: 4px;
}

/* Tickets List */
.tickets-list-container {
  margin-bottom: 20px;
}

.tickets-list-container h4 {
  margin-bottom: 12px;
  color: #333;
}

.tickets-table {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  max-height: 400px;
  overflow-y: auto;
}

.tickets-header {
  display: grid;
  grid-template-columns: 1fr 0.8fr 0.8fr 1fr 1.2fr;
  gap: 12px;
  background: #f5f5f5;
  padding: 12px;
  font-weight: 600;
  font-size: 0.85rem;
  color: #333;
  border-bottom: 2px solid #ddd;
  position: sticky;
  top: 0;
}

.ticket-row {
  display: grid;
  grid-template-columns: 1fr 0.8fr 0.8fr 1fr 1.2fr;
  gap: 12px;
  padding: 12px;
  border-bottom: 1px solid #eee;
  align-items: center;
  font-size: 0.9rem;
}

.ticket-row:last-child {
  border-bottom: none;
}

.ticket-row:hover {
  background: #f9f9f9;
}

.col-type, .col-row, .col-seat, .col-price, .col-action {
  display: flex;
  align-items: center;
}

.col-type {
  justify-content: flex-start;
}

.col-row, .col-seat {
  justify-content: center;
  font-weight: 500;
}

.col-price {
  justify-content: center;
  font-weight: 600;
  color: #0071E3;
}

.col-action {
  justify-content: center;
}

.badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.badge.standard {
  background: #e3f2fd;
  color: #1976d2;
}

.badge.vip {
  background: #f3e5f5;
  color: #7b1fa2;
}

.select-ticket-btn {
  padding: 6px 12px;
  border: 2px solid #0071E3;
  background: white;
  color: #0071E3;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.85rem;
  transition: all 0.2s;
}

.select-ticket-btn:hover:not(:disabled) {
  background: #0071E3;
  color: white;
}

.select-ticket-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  border-color: #ccc;
  color: #ccc;
}

.no-tickets {
  padding: 30px;
  text-align: center;
  color: #666;
  font-size: 0.95rem;
}

.no-selection-msg {
  padding: 20px;
  background: #fff3cd;
  border: 1px solid #ffc107;
  border-radius: 6px;
  color: #856404;
  text-align: center;
}

/* Quantity Selector - NOT USED ANYMORE */
.quantity-selector {
  display: none;
}

.qty-btn {
  display: none;
}

.qty-input {
  display: none;
}

/* Tickets Selection - NOT USED ANYMORE */
.tickets-selection {
  display: none;
}

.ticket-selection-item {
  display: none;
}

.ticket-selection-item h4 {
  display: none;
}

.seat-select {
  display: none;
}

/* Form Styles in Modal */
.modal-content .form-group {
  margin-bottom: 16px;
}

.modal-content label {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
  font-size: 0.95rem;
}

.modal-content input[type="text"],
.modal-content input[type="number"] {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #ddd;
  border-radius: 6px;
  font-size: 0.95rem;
  color: #333;
  background: white;
  transition: all 0.2s;
}

.modal-content input[type="text"]:focus,
.modal-content input[type="number"]:focus {
  outline: none;
  border-color: #0071E3;
  box-shadow: 0 0 0 3px rgba(0, 113, 227, 0.1);
}

.modal-content .row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

/* Payment Section */
.payment-section {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 2px solid #eee;
}

.payment-section h4 {
  margin-bottom: 15px;
  color: #333;
  font-weight: 600;
}

.checkbox-container {
  margin-bottom: 20px;
  margin-top: 15px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 6px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #333;
  font-weight: 500;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  margin: 0;
  cursor: pointer;
}

.total-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #f0f7ff;
  border: 2px solid #0071E3;
  border-radius: 8px;
  font-size: 1.1rem;
}

.total-label {
  color: #333;
  font-weight: 500;
}

.total-amount {
  font-weight: 700;
  color: #0071E3;
  font-size: 1.4rem;
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.cancel-btn, .submit-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.2s;
}

.cancel-btn {
  background: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

.submit-btn {
  background: #0071E3;
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 113, 227, 0.3);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-msg {
  color: #d32f2f;
  margin-top: 12px;
  padding: 10px;
  background: #ffebee;
  border-radius: 6px;
  font-size: 0.9rem;
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
