<template>
  <header class="navbar">
    <div class="container nav-content">
      <router-link to="/" class="logo">
        <img src="@/assets/logo.png" alt="TicketBlock Logo" style="height: 70px; width: auto; object-fit: contain;" />
      </router-link>
      <nav>
        <router-link to="/events">Eventi</router-link>
        <router-link v-if="isAuthenticated && !isOrganizer" to="/my-tickets">I Miei Biglietti</router-link>
        <router-link v-if="isOrganizer" to="/my-events">I Miei Eventi</router-link>
        <router-link v-if="isOrganizer" to="/create-event">Crea Evento</router-link>
        <router-link v-if="!isAuthenticated" to="/login">Accedi</router-link>
        <router-link v-if="!isAuthenticated" to="/register" class="cta-button">Registrati</router-link>
        <span v-if="isAuthenticated" class="user-name">{{ userName }}</span>
        <a v-if="isAuthenticated" @click="logout" href="#" class="cta-button">Esci</a>
      </nav>
    </div>
  </header>
  <main class="main-content">
    <div v-if="globalError" style="color: red; padding: 20px; text-align: center; border: 2px solid red; margin: 20px;">
      {{ globalError }}
    </div>
    <router-view v-else></router-view>
  </main>
</template>

<script setup>
import { ref, watch, onMounted, onErrorCaptured } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import authService from '@/services/authService';

const router = useRouter();
const route = useRoute();

const isAuthenticated = ref(false);
const isOrganizer = ref(false);
const userName = ref('');
const globalError = ref('');

const checkAuth = () => {
  const token = localStorage.getItem('token');
  isAuthenticated.value = !!token;
  isOrganizer.value = authService.getUserRole() === 'ORGANIZER';
  userName.value = authService.getUserName() || '';
};

watch(route, () => {
  checkAuth();
  globalError.value = ''; // Clear error on nav
});

onMounted(() => {
  checkAuth();
});

onErrorCaptured((err) => {
  console.error("Global Error Caught:", err);
  globalError.value = `Critical Error: ${err.message}`;
  return false; // Stop propagation
});

const logout = () => {
  authService.logout();
  router.push('/');
  // Force reload to update state (simple approach)
  window.location.reload();
};
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid rgba(0,0,0,0.1);
  height: 80px;
  display: flex;
  align-items: center;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.logo {
  font-weight: 600;
  font-size: 1.1rem;
  color: var(--text-primary);
  letter-spacing: -0.01em;
  text-decoration: none;
  display: flex;
  align-items: center;
}

nav a {
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-left: 24px;
  text-decoration: none;
  font-weight: 400;
}

nav a:hover {
  color: var(--text-primary);
  text-decoration: none;
}

nav a.router-link-exact-active {
  color: var(--text-primary);
}

.user-name {
  font-size: 0.8rem;
  color: var(--text-primary);
  margin-left: 24px;
  font-weight: 500;
  padding: 6px 14px;
  background: var(--bg-secondary);
  border-radius: 980px;
  display: inline-block;
}

.cta-button {
  background: var(--accent-color);
  color: white !important;
  padding: 6px 14px;
  border-radius: 980px;
  transition: opacity 0.2s;
}

.cta-button:hover {
  opacity: 0.8;
}

.main-content {
  padding-top: 40px;
  min-height: 100vh;
  background-color: var(--bg-color);
}
</style>
