<template>
  <div
    class="w-16 bg-white dark:bg-gray-800 border-r border-gray-500 flex flex-col items-center py-4"
  >
    <div class="space-y-4 flex flex-col">
      <div
        v-for="(elm, n) in elms.top"
        :key="n"
        class="cursor-pointer p-4  rounded-full flex items-center justify-center"
        :class="
          active === elm.name
            ? 'bg-lime-500/25 text-lime-200 '
            : 'bg-gray-200 dark:bg-gray-700 hover:bg-lime-500/25 hover:text-lime-200'
        "
        @click="elm.action"
      >
        <i :class="elm.icon" style="font-size: 1.125rem;" />
      </div>
    </div>
    <div class="mt-auto">
      <div
        v-for="(elm, n) in elms.bottom"
        :key="n"
        class="cursor-pointer p-4 bg-gray-200 dark:bg-gray-700 rounded-full flex items-center justify-center"
        :class="
          active === elm.name
            ? 'bg-lime-500/25 text-lime-200'
            : 'bg-gray-200 dark:bg-gray-700 hover:bg-lime-500/25 hover:text-lime-200'
        "
        @click="elm.action"
      >
        <i :class="elm.icon"  style="font-size: 1.125rem;" />
      </div>
    </div>
  </div>
  <SettingsModal />
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import SettingsModal from './SettingsModal.vue'
import { ref } from 'vue'

const appStore = useAppStore()
const active = ref('chat');

const elms = {
  top: [
    {
      icon: 'pi pi-bell',
      action: () => {
        active.value = 'notification'
        appStore.activePanel = 'notification'
      },
      name: 'notification'
    },
    {
      icon: 'pi pi-comments',
      action: () => {
        active.value = 'chat'
        appStore.activePanel = 'chat'
      },
      name: 'chat'
    },
    {
      icon: 'pi pi-users',
      action: () => {
        active.value = 'group'
        appStore.activePanel = 'group'
      },
      name: 'group'
    },
  ],
  bottom: [
    {
      icon: 'pi pi-cog',
      action: () => {
        appStore.settingsModalVisible = true
      },
      name: 'settings'
    },
  ],
}
</script>
