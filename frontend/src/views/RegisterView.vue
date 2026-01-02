<template>
  <div class="auth-wrapper">
    <div class="card auth-card">
      <h2 class="auth-title">Crea il tuo account</h2>
      <p class="auth-subtitle">Unisciti a TicketBlock oggi</p>

      <form @submit.prevent="handleRegister">
        <div class="name-row">
          <div class="form-group">
            <input type="text" id="firstname" v-model="firstname" placeholder="Nome" required />
          </div>
          <div class="form-group">
            <input type="text" id="lastname" v-model="lastname" placeholder="Cognome" required />
          </div>
        </div>
        <div class="form-group">
          <input type="email" id="email" v-model="email" placeholder="Email" required />
        </div>
        <div class="form-group">
          <input type="password" id="password" v-model="password" placeholder="Password" required />
        </div>
        <div class="form-group">
          <select id="role" v-model="role">
            <option value="USER">Utente</option>
            <option value="ORGANIZER">Organizzatore</option>
          </select>
        </div>
        <button type="submit" class="submit-btn">Crea Account</button>
        <p v-if="error" class="error-msg">{{ error }}</p>
      </form>
      
      <div class="auth-footer">
        <p>Hai già un account? <router-link to="/login">Accedi</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import authService from '@/services/authService';
import { useRouter } from 'vue-router';

const firstname = ref('');
const lastname = ref('');
const email = ref('');
const password = ref('');
const role = ref('USER');
const error = ref('');
const router = useRouter();

// Gestisce la registrazione di un nuovo utente
const handleRegister = async () => {
  error.value = '';
  if (password.value.length < 8) {
      error.value = 'La password deve essere di almeno 8 caratteri.';
      return;
  }

  try {
    const response = await authService.register({
      firstName: firstname.value,
      lastName: lastname.value,
      email: email.value,
      password: password.value,
      role: role.value
    });
    // Salva il token e reindirizza alla home
    localStorage.setItem('token', response.data.token);
    router.push('/events');
  } catch (err) {
    console.error(err);
    if (err.response && err.response.data) {
        // If backend returns a string message or an object with message
        error.value = typeof err.response.data === 'string' 
            ? err.response.data 
            : (err.response.data.message || JSON.stringify(err.response.data));
    } else {
        error.value = 'Registrazione fallita. Il server potrebbe non essere raggiungibile.';
    }
  }
};
</script>

<style scoped>
.auth-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.auth-card {
  width: 100%;
  max-width: 440px;
  text-align: center;
  padding: 40px;
}

.auth-title {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
}

.auth-subtitle {
  color: var(--text-secondary);
  margin-bottom: 2rem;
}

.name-row {
  display: flex;
  gap: 10px;
}

.form-group {
  margin-bottom: 16px;
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
  font-size: 1rem;
}

.error-msg {
  color: var(--error-color);
  font-size: 0.9rem;
  margin-top: 1rem;
}

.auth-footer {
  margin-top: 2rem;
  font-size: 0.9rem;
  color: var(--text-secondary);
}
</style>
