<template>
  <div class="container create-event-page">
    <div class="form-wrapper">
      <div class="header">
        <h1>Crea Nuovo Evento</h1>
        <p>Inserisci i dettagli per pubblicare il tuo evento.</p>
      </div>

      <form @submit.prevent="handleSubmit" class="card form-card">
        <div class="form-section">
          <h3>Informazioni Base</h3>
          <div class="form-group">
            <label>Nome Evento</label>
            <input type="text" v-model="form.name" maxlength="50" required placeholder="es. Finale Champions League" />
          </div>
          
          <div class="row">
            <div class="form-group">
              <label>Data</label>
              <input type="date" v-model="form.date" required />
            </div>
            <div class="form-group">
              <label>Inizio Vendite</label>
              <input type="date" v-model="form.saleStartDate" required />
              <small class="hint">Almeno 3 giorni prima dell'evento</small>
            </div>
             <div class="form-group">
               <label>Luogo</label>
               <select v-model="form.venueId" required class="full-width">
                  <option value="" disabled>Seleziona un luogo</option>
                  <option v-for="venue in venues" :key="venue.id" :value="venue.id">
                    {{ venue.name }} ({{ venue.address?.city }})
                  </option>
               </select>
               <small v-if="venueLoading" class="hint">Caricamento luoghi...</small>
               <small v-if="venueError" class="error-msg" style="display:block; text-align:left; margin-top:5px;">
                 Errore caricamento luoghi: {{ venueError }}
               </small>
             </div>
           </div>

          <div class="row">
            <div class="form-group">
              <label>Durata</label>
              <select v-model="form.duration" required class="full-width">
                 <option value="" disabled>Seleziona durata</option>
                 <option v-for="d in durations" :key="d.value" :value="d.value">
                   {{ d.label }}
                 </option>
              </select>
            </div>
            <div class="form-group">
              <label>Orario Inizio</label>
              <select v-model="form.startTimeSlot" :disabled="!form.venueId || !form.date || !form.duration" required class="full-width">
                 <option value="" disabled>Seleziona orario</option>
                 <option v-for="slot in validStartSlots" :key="slot.id" :value="slot.id">
                   {{ slot.label }}
                 </option>
              </select>
              <small class="hint" v-if="!form.venueId || !form.date">Seleziona prima data e luogo</small>
            </div>
          </div>
        </div>

        <div class="form-section">
          <h3>Biglietti e Prezzi</h3>
          <div class="row">
            <div class="form-group">
              <label>Prezzo Standard (EUR)</label>
              <input type="number" step="0.01" v-model="form.standardTicketPrice" required />
            </div>
            <div class="form-group">
              <label>Prezzo VIP (EUR)</label>
              <input type="number" step="0.01" v-model="form.vipTicketPrice" required />
            </div>
          </div>
          
          <div class="form-group">
            <label>Descrizione</label>
            <textarea v-model="form.description" maxlength="2000" placeholder="Inserisci una descrizione dell'evento..."></textarea>
          </div>
        </div>

        <div class="form-section">
          <h3>Media</h3>
          <div class="form-group">
            <label>Immagine Evento</label>
            <input type="file" @change="handleFileChange" accept="image/*" />
            <small class="hint">Carica un'immagine dal tuo dispositivo</small>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="$router.push('/events')">Annulla</button>
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? 'Creazione...' : 'Pubblica Evento' }}
          </button>
        </div>
        
        <p v-if="error" class="error-msg">{{ error }}</p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'; // Added computed, watch
import { useRouter } from 'vue-router';
import eventService from '@/services/eventService';
import venueService from '@/services/venueService';
import imageService from '@/services/imageService';

const router = useRouter();
const loading = ref(false);
const error = ref('');
const venues = ref([]);
const venueLoading = ref(false);
const venueError = ref('');
const selectedFile = ref(null);

// Carica la lista dei luoghi disponibili
const fetchVenues = async () => {
    venueLoading.value = true;
    venueError.value = '';
    try {
        const response = await venueService.getAllVenues();
        venues.value = response.data;
    } catch (err) {
        console.error("Failed to load venues", err);
        venueError.value = err.message + (err.response ? ' (' + err.response.status + ')' : '');
    } finally {
        venueLoading.value = false;
    }
};

onMounted(fetchVenues);

  const form = ref({
  name: '',
  description: '',
  date: '',
  saleStartDate: '',
  startTimeSlot: '', // Changed from startTime
  duration: '',      // Changed from endTime
  venueId: '',
  standardTicketPrice: '',
  vipTicketPrice: '',
  imageUrl: ''
});

// Slot Logic
const availableSlots = ref({});
// Calcola gli slot di inizio validi in base alla durata selezionata e alla disponibilità del luogo
const validStartSlots = computed(() => {
    if (!form.value.duration || Object.keys(availableSlots.value).length === 0) return [];
    
    const duration = parseInt(form.value.duration);
    const valid = [];
    
    // Slots go from 32 (08:00) to 92 (23:00)
    for (let i = 32; i <= 92; i++) {
        let fit = true;
        for (let k = 0; k < duration; k++) {
            if (i + k > 92 || !availableSlots.value[i + k]) {
                fit = false;
                break;
            }
        }
        if (fit) {
           valid.push({ id: i, label: slotIndexToTime(i) });
        }
    }
    return valid;
});

const durations = [
    { value: 2, label: '30 min' },
    { value: 4, label: '1 ora' },
    { value: 6, label: '1.5 ore' },
    { value: 8, label: '2 ore' },
    { value: 10, label: '2.5 ore' },
    { value: 12, label: '3 ore' },
    { value: 16, label: '4 ore' }
];

const slotIndexToTime = (index) => {
    const totalMinutes = index * 15;
    const h = Math.floor(totalMinutes / 60);
    const m = totalMinutes % 60;
    return `${h.toString().padStart(2,'0')}:${m.toString().padStart(2,'0')}`;
};

// Aggiorna gli slot disponibili quando cambiano luogo o data
watch([() => form.value.venueId, () => form.value.date], async ([newVenue, newDate]) => {
    if (newVenue && newDate) {
        try {
            const res = await venueService.getVenueAvailableSlots(newVenue, newDate);
            availableSlots.value = res.data.slot; 
        } catch (e) {
            console.error("Failed to fetch slots", e);
            availableSlots.value = {};
        }
    } else {
        availableSlots.value = {};
    }
});

// Gestisce il caricamento del file immagine
const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedFile.value = file;
  }
};

// Invia i dati del form per creare l'evento
const handleSubmit = async () => {
  loading.value = true;
  error.value = '';

  try {
    // 1. Upload image if selected
    if (selectedFile.value) {
        const imageResponse = await imageService.uploadImage(selectedFile.value);
        form.value.imageUrl = imageResponse.data.url;
    }
  
    // 2. Prepare payload
    // Backend expects startTimeSlot and duration as Integers, NOT startTime/endTime strings
    const payload = {
      ...form.value,
      startTimeSlot: parseInt(form.value.startTimeSlot),
      duration: parseInt(form.value.duration),
      venueId: parseInt(form.value.venueId),
      standardTicketPrice: parseFloat(form.value.standardTicketPrice),
      vipTicketPrice: parseFloat(form.value.vipTicketPrice)
    };
    
    // Remove fields not in DTO if necessary, but backend usually ignores extras.
    // However, to be safe and clean:
    // delete payload.startTime; // Not in form originally but just in case
    // delete payload.endTime;

    await eventService.createEvent(payload);
    router.push('/events');
  } catch (err) {
    const msg = err.response?.data?.userMessage || err.response?.data?.detail || err.message || 'Controlla i dati inseriti.';
    error.value = 'Impossibile creare l\'evento. ' + msg;
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.create-event-page {
  padding-top: 40px;
  padding-bottom: 60px;
  background-color: var(--bg-secondary);
  min-height: 100vh;
}

.form-wrapper {
  max-width: 700px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.header h1 {
  font-size: 2.5rem;
}

.header p {
  color: var(--text-secondary);
}

.form-card {
  padding: 40px;
}

.form-section {
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.form-section h3 {
  font-size: 1.1rem;
  margin-bottom: 20px;
  color: var(--text-primary);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 8px;
  color: var(--text-secondary);
}

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.hint {
  display: block;
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-top: 6px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.cancel-btn {
  background: transparent;
  color: var(--text-secondary);
}

.cancel-btn:hover {
  background: rgba(0,0,0,0.05);
  color: var(--text-primary);
  transform: none;
}

.error-msg {
  color: var(--error-color);
  margin-top: 20px;
  text-align: center;
}
</style>
