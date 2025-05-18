<script setup lang="ts">
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()

const getInitials = (name?: string) => {
  if (!name) return ''
  return name
    .split(' ')
    .map(word => word[0])
    .join('')
    .toUpperCase()
}
</script>

<template>
  <div class="flex items-center justify-between px-4 py-3 border-b border-gray-500">
    <div class="flex items-center gap-3">
      <Avatar
        :label="getInitials(user?.username)"
        class="bg-primary"
        shape="circle"
      />
      <div>
        <h3 class="text-lg font-semibold">{{ user?.username }}</h3>
        <p class="text-sm text-gray-500">{{ user?.email }}</p>
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
