import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const panelInforOpened = ref(false)
  const settingsModalVisible = ref(false)
  const activePanel = ref<'chat' | 'notification' | 'group'>('chat')

  const setSettingsModalVisible = (value: boolean) => (settingsModalVisible.value = value)

  return {
    panelInforOpened,
    settingsModalVisible,
    activePanel,
    setSettingsModalVisible,
  }
})
