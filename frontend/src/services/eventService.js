import axios from '@/plugins/axios';

export default {
    // Recupera tutti gli eventi, filtrandoli opzionalmente per stato (es. ONGOING, SOLD_OUT)
    getAllEvents(statusList = []) {
        const params = new URLSearchParams();
        statusList.forEach(status => params.append('saleStatus', status));
        return axios.get('/events', { params });
    },
    // Ottiene i dettagli completi di un singolo evento tramite ID
    getEventById(id) {
        return axios.get(`/events/${id}`);
    },
    // Crea un nuovo evento (solo per Organizzatori)
    createEvent(event) {
        return axios.post('/organizer/events', event);
    },
    // Elimina un evento esistente
    deleteEvent(id) {
        return axios.delete(`/organizer/events/${id}`);
    },
    // Recupera la lista degli eventi creati dall'organizzatore loggato
    getOrganizerEvents() {
        return axios.get('/organizer/events');
    },
    // Ottiene i dettagli specifici per l'organizzatore (inclusi dati di vendita)
    getOrganizerEventDetails(id) {
        return axios.get(`/organizer/events/${id}`);
    }
};
