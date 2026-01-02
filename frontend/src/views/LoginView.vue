<template>
  <div class="auth-wrapper">
    <div class="card auth-card">
      <h2 class="auth-title">Accedi a TicketBlock</h2>
      <p class="auth-subtitle">Bentornato</p>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <input type="email" id="email" v-model="email" placeholder="Email" required />
        </div>
        <div class="form-group">
          <input type="password" id="password" v-model="password" placeholder="Password" required />
        </div>
        <button type="submit" class="submit-btn">Accedi</button>
        <p v-if="error" class="error-msg">{{ error }}</p>
      </form>
      
      <div class="auth-footer">
        <p>Non hai un account? <router-link to="/register">Registrati</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import authService from '@/services/authService';
import { useRouter } from 'vue-router';

const email = ref('');
const password = ref('');
const error = ref('');
const router = useRouter();

// Gestisce il login dell'utente
const handleLogin = async () => {
  try {
    const response = await authService.login({ email: email.value, password: password.value });
    // Salva il token e reindirizza alla home
    localStorage.setItem('token', response.data.token);
    router.push('/events');
  } catch (err) {
    error.value = 'Credenziali non valide';
    console.error(err);
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
  max-width: 400px;
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
