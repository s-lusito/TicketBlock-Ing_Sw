import axios from '@/plugins/axios';

export default {
    // Recupera i biglietti disponibili per un determinato evento
    getEventTickets(eventId, status) {
        const params = status ? { ticketStatus: status } : {};
        return axios.get(`/events/${eventId}/tickets`, { params });
    },
    // Invia la richiesta di acquisto biglietti al backend
    purchaseTickets(purchaseRequest) {
        return axios.post('/tickets/purchase', purchaseRequest);
    },
    // Restituisce la lista dei biglietti acquistati dall'utente corrente
    getMyTickets() {
        return axios.get('/tickets/mine');
    },
    // Mette in rivendita un biglietto acquistato
    resellTicket(ticketId) {
        return axios.post(`/tickets/${ticketId}/resell`);
    },
    // Invalida un biglietto (funzionalità amministrativa)
    invalidateTicket(ticketId) {
        return axios.post(`/tickets/${ticketId}/invalidate`);
    }
};
