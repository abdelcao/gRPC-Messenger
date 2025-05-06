<script setup lang="ts">
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
</script>

<template>
  <div class="flex items-center justify-between px-4 py-3 border-b border-gray-500">
    <div class="flex items-center gap-2">
      <img src="/images/default_avatar.jpg" class="w-10 h-10 rounded-full" />
      <div class="flex-1">
        <div class="font-semibold text-sm">Selected Chat</div>
        <div class="text-xs text-lime-400">Online</div>
      </div>
    </div>

    <div>
      <i
        @click="() => (appStore.panelInforOpened = !appStore.panelInforOpened)"
        style="font-size: 1.25rem"
        class="pi pi-info-circle cursor-pointer p-2 flex items-center justify-center hover:bg-gray-500 rounded-full"
      ></i>
    </div>
  </div>
</template>
