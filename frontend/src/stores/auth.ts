import type { User } from '@/grpc/user/user_pb'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>({} as User)

  const setUser = (data: User | null) => {
    user.value = data
  }

  return {
    user,
    setUser,
  }
})
