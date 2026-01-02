import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';
import EventListView from '../views/EventListView.vue';

const routes = [
    {
        path: '/',
        redirect: '/events'
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView
    },
    {
        path: '/register',
        name: 'Register',
        component: RegisterView
    },
    {
        path: '/events',
        name: 'Events',
        component: EventListView
    },
    {
        path: '/events/:id',
        name: 'EventDetail',
        component: () => import('../views/EventDetailView.vue')
    },
    {
        path: '/create-event',
        name: 'CreateEvent',
        component: () => import('../views/CreateEventView.vue')
    },
    {
        path: '/my-tickets',
        name: 'MyTickets',
        component: () => import('../views/MyTicketsView.vue')
    },
    {
        path: '/my-events',
        name: 'MyEvents',
        component: () => import('../views/MyEventsView.vue')
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
