<template>
  <div class="container my-events-page">
    <div class="header">
      <h1>I Miei Eventi</h1>
      <p>Gestisci gli eventi che hai creato.</p>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <router-link to="/create-event" class="cta-button">+ Crea un Evento</router-link>
    </div>

    <div v-else-if="events.length === 0" class="empty-state">
      <p>Non hai ancora creato nessun evento.</p>
      <router-link to="/create-event" class="cta-button">Crea il tuo primo evento</router-link>
    </div>

    <div v-else class="event-grid">
      <div v-for="item in events" :key="item.event.id" class="card event-card">
        <div class="card-image-wrapper">
           <img v-if="item.event.imageUrl" :src="getImageUrl(item.event.imageUrl)" :alt="item.event.name" class="card-image" />
           <div v-else class="card-image-placeholder"></div>
        </div>
        <div class="card-content">
          <div class="event-date">{{ new Date(item.event.date).toLocaleDateString('it-IT', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' }) }}</div>
          <h3 class="event-title">{{ item.event.name }}</h3>
          <p class="event-location" v-if="item.event.venue">
             {{ item.event.venue.name }} - {{ item.event.venue.address?.city || item.event.venue.city || '' }}
          </p>

          <div class="stats-container">
             <div class="stat-row">
                 <span class="stat-label">Venduti Totali</span>
                 <span class="stat-value">{{ (item.standardTicketsSold || 0) + (item.vipTicketsSold || 0) }}</span>
             </div>
             
             <div class="revenue-breakdown">
                <div class="stat-item">
                    <span class="sub-label">Standard</span>
                    <span class="sub-value">{{ currency( (item.standardTicketsSold || 0) * (item.event.standardTicketPrice || 0) ) }}</span>
                </div>
                <div class="stat-item">
                     <span class="sub-label">VIP</span>
                     <span class="sub-value">{{ currency( (item.vipTicketsSold || 0) * (item.event.vipTicketPrice || 0) ) }}</span>
                 </div>
             </div>

             <div class="total-revenue">
                <span>Totale Incasso</span>
                <span class="total-value">{{ currency(item.totalSales || 0) }}</span>
             </div>
          </div>

          <div class="card-footer">
            <button class="action-btn" @click="$router.push(`/events/${item.event.id}`)">Dettagli</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import eventService from '@/services/eventService';

const events = ref([]);
const loading = ref(true);
const error = ref('');

// Recupera gli eventi creati dall'organizzatore corrente
const fetchEvents = async () => {
    loading.value = true;
    error.value = '';
    try {
        const response = await eventService.getOrganizerEvents();
        events.value = response.data;
    } catch (err) {
        console.error(err);
        error.value = 'Impossibile caricare i tuoi eventi.';
    } finally {
        loading.value = false;
    }
};

const getImageUrl = (path) => {
  if (!path) return null;
  if (path.startsWith('http')) return path;
  return `http://localhost:8080${path}`;
};

const currency = (val) => {
    return Number(val).toFixed(2) + '€';
}

onMounted(fetchEvents);
</script>

<style scoped>
.my-events-page {
  padding-top: 40px;
  padding-bottom: 60px;
}

.header {
  margin-bottom: 40px;
  text-align: center;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 30px;
}

.event-card {
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.event-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.12);
}

.card-image-wrapper {
  height: 180px;
  width: 100%;
  overflow: hidden;
  background: #f5f5f7;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-image-placeholder {
  height: 100%;
  background: linear-gradient(135deg, #f5f5f7 0%, #e1e1e6 100%);
}

.card-content {
  padding: 20px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.event-date {
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--accent-color);
  font-weight: 600;
  margin-bottom: 6px;
}

.event-title {
  font-size: 1.3rem;
  margin-bottom: 4px;
  line-height: 1.2;
}

.event-location {
    font-size: 0.9rem;
    color: var(--text-secondary);
    margin-bottom: 16px;
}

.stats-container {
    background: #f9f9fb;
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 20px;
    font-size: 0.9rem;
}

.stat-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-weight: 500;
}

.revenue-breakdown {
    display: flex;
    justify-content: flex-start;
    gap: 40px;
    padding: 8px 0;
    border-top: 1px solid #e0e0e0;
    border-bottom: 1px solid #e0e0e0;
    margin-bottom: 10px;
    color: #555;
    font-size: 0.85rem;
}

.stat-item {
    display: flex;
    flex-direction: column;
}

.total-revenue {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 700;
    color: var(--accent-color);
    font-size: 1rem;
}

.card-footer {
  margin-top: auto;
  text-align: right;
}

.action-btn {
  background-color: #333;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.action-btn:hover {
  background-color: #000;
}

.loading-state, .error-state, .empty-state {
  text-align: center;
  padding: 60px;
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.spinner {
  border: 3px solid rgba(0,0,0,0.1);
  border-radius: 50%;
  border-top: 3px solid var(--accent-color);
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

.cta-button {
  background-color: var(--accent-color);
  color: white;
  padding: 10px 24px;
  border-radius: 50px;
  text-decoration: none;
  font-weight: 600;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
