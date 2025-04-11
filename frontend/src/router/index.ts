import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ChatView from '@/views/ChatView.vue'
import SettingsView from '@/views/SettingsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name:"login",
      component: LoginView
    },
    {
      path: "/register",
      name:"register",
      component: RegisterView
    },
    {
      path: "/chat/:id",
      name:"chat",
      component: ChatView
    },
    {
      path: "/settings",
      name:"settings",
      component: SettingsView
    },
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
  ],
})

export default router
