import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const panelInforOpened = ref(false)
  const settingsModalVisible = ref(false)
  const activePanel = ref<'chat' | 'notification' | 'group'>('chat')

  return {
    panelInforOpened,
    settingsModalVisible,
    activePanel,
  }
<<<<<<< HEAD
})
=======
})
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
