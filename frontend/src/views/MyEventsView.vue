<template>
  <div class="container my-events-page">
    <div class="header">
      <h1>I Miei Eventi</h1>
      <p>Gestisci gli eventi che hai creato.</p>
    </div>

    <div v-if="loading" class="loading-state">
      <p>Caricamento eventi...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <p>Non hai ancora creato nessun evento.</p>
      <router-link to="/create-event" class="cta-button">+ Crea un Evento</router-link>
    </div>

    <div v-else-if="events.length === 0" class="empty-state">
      <p>Non hai ancora creato nessun evento.</p>
      <router-link to="/create-event" class="cta-button">Crea il tuo primo evento</router-link>
    </div>

    <div v-else class="events-grid">
      <div v-for="item in events" :key="item.event.id" class="card event-card">
        <div class="card-content">
          <div class="event-date">{{ new Date(item.event.date).toLocaleDateString('it-IT') }}</div>
          <h3 class="event-title">{{ item.event.name }}</h3>
          <div class="stats">
            <div class="stat">
              <span class="label">Venduti</span>
              <span class="value">{{ (item.standardTicketsSold || 0) + (item.vipTicketsSold || 0) }}</span>
            </div>
            <div class="stat">
              <span class="label">Incasso</span>
              <span class="value">{{ Number(item.totalSales || 0).toFixed(2) }}€</span>
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

onMounted(fetchEvents);
</script>

<style scoped>
.my-events-page {
  padding-top: 40px;
  padding-bottom: 60px;
}

.header {
  margin-bottom: 30px;
  text-align: center;
}

.events-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.event-card {
  padding: 20px;
}

.stats {
  display: flex;
  gap: 20px;
  margin: 15px 0;
  padding: 10px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat .label {
  font-size: 0.8rem;
  color: #666;
}

.stat .value {
  font-weight: 600;
  font-size: 1.1rem;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  background-color: #666;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.action-btn:hover {
  background-color: #555;
}

.error-state, .empty-state {
  text-align: center;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.cta-button {
  background-color: rgb(4, 131, 132); /* Using the specific teal color requested previously */
  color: white;
  padding: 10px 24px;
  border-radius: 50px;
  text-decoration: none;
  font-weight: 600;
  transition: opacity 0.2s;
}

.cta-button:hover {
  opacity: 0.9;
}
</style>
