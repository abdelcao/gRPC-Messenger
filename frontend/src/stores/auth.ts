import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: {
      name: 'Yusef',
      email: 'yusef@example.com'
    },
    isAuthenticated: true
  }),
  actions: {
    logout() {
      this.isAuthenticated = false
      // Optionally clear tokens or redirect
    }
  }
})
