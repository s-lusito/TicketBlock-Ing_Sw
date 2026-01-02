import axios from '@/plugins/axios';

export default {
    // Registra un nuovo utente inviando i dati al backend
    register(user) {
        return axios.post('/auth/register', user);
    },
    // Effettua il login e restituisce il token JWT
    login(credentials) {
        return axios.post('/auth/authenticate', credentials);
    },
    // Esegue il logout rimuovendo il token dal localStorage
    logout() {
        localStorage.removeItem('token');
    },
    // Decodifica il token JWT per ottenere il ruolo dell'utente (es. USER, ORGANIZER)
    getUserRole() {
        const token = localStorage.getItem('token');
        if (!token) return null;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.role;
        } catch (e) {
            return null;
        }
    }
};
