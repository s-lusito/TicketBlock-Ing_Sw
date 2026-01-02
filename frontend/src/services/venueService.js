import axios from '@/plugins/axios';

export default {
    // Recupera le informazioni di un luogo specifico
    getVenueById(id) {
        return axios.get(`/venues/${id}`);
    },
    // Restituisce la lista di tutti i luoghi disponibili
    getAllVenues() {
        return axios.get('/venues');
    },
    // Controlla gli slot orari disponibili per un luogo in una data specifica
    getVenueAvailableSlots(id, date) {
        return axios.get(`/venues/${id}/available-slots`, {
            params: { date }
        });
    }
};
