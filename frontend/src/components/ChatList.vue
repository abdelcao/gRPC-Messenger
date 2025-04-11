<!-- src/components/ChatList.vue -->
<template>
  <div class="max-w-72 bg-white dark:bg-gray-800 border-r p-4 overflow-y-auto">
    <InputText v-model="search" placeholder="Search..." class="w-full mb-4" />

    <ul>
      <li
        v-for="chat in chats"
        :key="chat.id"
        class="flex items-start gap-3 grow-0 py-2 px-2 rounded hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer"
      >
        <img :src="chat.avatar" class="w-10 h-10 rounded-full" />
        <div class="flex-1">
          <div class="flex items-center justify-between gap-2 mb-1">
            <div class="font-semibold text-sm">{{ chat.name }}</div>
            <div class="text-xs text-gray-400">{{ chat.time }}</div>
          </div>
          <span class="text-xs text-gray-500 line-clamp-1">{{ chat.message }}</span>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import InputText from 'primevue/inputtext'

const search = ref('')
const chats = ref([
  {
    id: 1,
    name: 'TechPulse Company',
    message: 'Reminder that we have a project meet zefkuhilnl',
    avatar: '/images/default_avatar.jpg',
    time: '13:02',
  },
  {
    id: 2,
    name: 'Michelle Davis',
    message: 'Just finished a workout and feeli',
    avatar: '/images/default_avatar.jpg',
    time: '13:02',
  },
  // Add other chat entries...
])
</script>

<style scoped></style>
