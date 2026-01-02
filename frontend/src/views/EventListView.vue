<template>
  <div class="container events-page">
    <div class="page-header">
      <h1>Prossimi Eventi</h1>
      <p class="subtitle">Scopri le migliori esperienze intorno a te.</p>
      
      <div class="filters">
        <button 
          v-for="status in filterOptions" 
          :key="status.value"
          :class="['filter-btn', { active: activeFilters.includes(status.value) }]"
          @click="toggleFilter(status.value)"
        >
          {{ status.label }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
    </div>
    
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
    </div>
    
    <div v-else class="event-grid">
      <div v-for="event in events" :key="event.id" class="card event-card">
        <div class="card-image-wrapper">
           <img v-if="event.imageUrl" :src="getImageUrl(event.imageUrl)" :alt="event.name" class="card-image" />
           <div v-else class="card-image-placeholder"></div>
        </div>
        <div class="card-content">
          <div class="event-date">{{ new Date(event.date).toLocaleDateString('it-IT', { month: 'short', day: 'numeric' }) }}</div>
          <h3 class="event-title clickable-title" @click="goToDetails(event.id)">{{ event.name }}</h3>
          <p class="event-desc">{{ event.description }}</p>
          <div class="card-footer">
            <div class="price-container">
              <span class="price-label">A partire da</span>
              <span class="price">{{ Number(event.standardTicketPrice || event.price).toFixed(2) }}€</span>
            </div>
            <button 
              v-if="!isOrganizer" 
              class="buy-btn"
              :style="event.eventSaleStatus === 'NOT_STARTED' ? { backgroundColor: '#4a4a4a', cursor: 'not-allowed' } : {}"
              :disabled="event.eventSaleStatus === 'NOT_STARTED'"
              @click="event.eventSaleStatus !== 'NOT_STARTED' && goToDetails(event.id)"
            >
              {{ event.eventSaleStatus === 'NOT_STARTED' ? 'Presto Disponibile' : 'Acquista Biglietto' }}
            </button>
            <button v-else @click="goToDetails(event.id)" class="buy-btn" style="background-color: #666;">Vedi dettagli</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import eventService from '@/services/eventService';
import authService from '@/services/authService';

const router = useRouter();
const events = ref([]);
const loading = ref(true);
const error = ref('');
const activeFilters = ref(['ONGOING']); // Default to ONGOING
const isOrganizer = ref(false);

const filterOptions = [
  { label: 'In Corso', value: 'ONGOING' },
  { label: 'In Arrivo', value: 'NOT_STARTED' },
  { label: 'Sold Out', value: 'SOLD_OUT' },
  { label: 'Terminati', value: 'ENDED' }
];

// Carica la lista degli eventi dal backend filtrandoli per stato
const fetchEvents = async () => {
  loading.value = true;
  try {
    const response = await eventService.getAllEvents(activeFilters.value);
    // Ordina gli eventi per data crescente
    events.value = response.data.sort((a, b) => new Date(a.date) - new Date(b.date));
  } catch (err) {
    error.value = 'Impossibile caricare gli eventi.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// Gestisce la selezione/deselezione dei filtri di stato
const toggleFilter = (status) => {
  const index = activeFilters.value.indexOf(status);
  if (index === -1) {
    activeFilters.value.push(status);
  } else {
    activeFilters.value.splice(index, 1);
  }
  fetchEvents();
};

onMounted(() => {
  // Controlla il ruolo dell'utente per mostrare opzioni specifiche (es. admin)
  isOrganizer.value = authService.getUserRole() === 'ORGANIZER';
  fetchEvents();
});

// Genera l'URL completo per l'immagine dell'evento, gestendo percorsi relativi
const getImageUrl = (path) => {
  if (!path) return null;
  // Se è già un URL completo, lo restituisce
  if (path.startsWith('http')) return path;
  // Se è un percorso locale (/uploads/...), aggiunge il dominio del backend
  return `http://localhost:8080${path}`;
};

// Naviga alla pagina di dettaglio dell'evento
const goToDetails = (eventId) => {
  router.push(`/events/${eventId}`);
};
</script>

<style scoped>
.events-page {
  padding-bottom: 60px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  margin-top: 40px;
}

.filters {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.filter-btn {
  background: var(--bg-secondary);
  border: 1px solid transparent;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--text-secondary);
}

.filter-btn:hover {
  background: #e5e5ea;
}

.filter-btn.active {
  background: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

.page-header h1 {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 1.2rem;
  color: var(--text-secondary);
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
}

.event-card {
  padding: 0;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  display: flex;
  flex-direction: column;
}

.event-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.12);
}

.card-image-wrapper {
  height: 200px;
  width: 100%;
  overflow: hidden;
  background: #f5f5f7;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.event-card:hover .card-image {
  transform: scale(1.05);
}

.card-image-placeholder {
  height: 100%;
  background: linear-gradient(135deg, #f5f5f7 0%, #e1e1e6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-content {
  padding: 24px;
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
  margin-bottom: 8px;
}

.event-title {
  font-size: 1.4rem;
  margin-bottom: 8px;
  line-height: 1.2;
}

.clickable-title {
  cursor: pointer;
  transition: color 0.2s;
}

.clickable-title:hover {
  color: var(--accent-color);
}

.event-desc {
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin-bottom: 20px;
  flex-grow: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.price-container {
  display: flex;
  flex-direction: column;
}

.price-label {
  font-size: 0.75rem;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.price {
  font-weight: 600;
  font-size: 1.1rem;
}

.buy-btn {
  padding: 8px 20px;
  font-size: 0.9rem;
}

.loading-state, .error-state {
  text-align: center;
  padding: 60px;
  color: var(--text-secondary);
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

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
