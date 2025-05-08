<!-- src/components/ChatList.vue -->
<template>
  <InputText v-model="search" placeholder="Search..." class="w-full mb-4 h-10" />
  <ul class="flex flex-col gap-2">
    <ChatItem v-for="(chat, n) in chats" :key="n" :chat="chat" />
  </ul>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import InputText from 'primevue/inputtext'
import ChatItem from './ChatItem.vue'

const search = ref('')
const chats = ref([
  {
    id: 1,
    name: 'TechPulse Company',
    lastMessage: 'Reminder that we have a project meet zefkuhilnl',
    avatar: '/images/default_avatar.jpg',
    time: '13:02',
    unread: 15,
  },
  {
    id: 2,
    name: 'Michelle Davis',
    lastMessage: 'Just finished a workout and feeli',
    avatar: '/images/default_avatar.jpg',
    time: '13:02',
    unread: 0,
  },
  {
    id: 2,
    name: 'Michelle Davis',
    lastMessage: 'Just finished a workout and feeli',
    avatar: '/images/default_avatar.jpg',
    time: '13:02',
    unread: 2,
  },
  // Add other chat entries...
])
</script>

<style scoped></style>
