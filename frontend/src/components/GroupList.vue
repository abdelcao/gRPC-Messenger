<template>
  <InputText v-model="search" placeholder="Search..." class="w-full mb-4 h-10" />
  <ul class="flex flex-col gap-2">
    <ChatItem v-for="(chat, n) in groupChats" :key="n" :chat="chat" />
  </ul>
</template>

<script setup lang="ts">
import InputText from 'primevue/inputtext';
import ChatItem from './ChatItem.vue'
import { ref } from 'vue';

const search = ref('')

const groupChats = [
  {
    id: 1,
    name: 'Team Alpha',
    lastMessage: 'Meeting starts in 10 mins!',
    time: '2:30 PM',
    unread: 3,
    avatar: '/images/default_avatar.jpg',
    unred: 6,
  },
  {
    id: 2,
    name: 'Hackathon Squad',
    lastMessage: "Let's deploy the app tonight.",
    time: '1:15 PM',
    unread: 0,
    avatar: '/images/default_avatar.jpg',
    unred: 0,
  },
  {
    id: 3,
    name: 'Vue Devs',
    lastMessage: 'Check out the new PR.',
    time: 'Yesterday',
    unread: 1,
    avatar: '/images/default_avatar.jpg',
    unred: 99,
  },
]
</script>
