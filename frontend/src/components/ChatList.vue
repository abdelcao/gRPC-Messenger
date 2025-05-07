<!-- src/components/ChatList.vue -->
<template>
  <InputText v-model="search" placeholder="Search..." class="w-full mb-4 h-10" />
  <ul class="flex flex-col gap-2">
<<<<<<< HEAD
    <RouterLink
      v-for="chat in chats"
      :key="chat.id"
      :to="{
        name: 'chat',
        params: {
          id: chat.id,
        },
      }"
    >
      <li
        class="flex items-start gap-3 grow-0 py-2 px-2 rounded cursor-pointer"
        :class="
          chat.unread > 0
            ? 'bg-lime-500/25'
            : 'bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600'
        "
      >
        <img :src="chat.avatar" class="w-10 h-10 rounded-full" />
        <div class="flex-1">
          <div class="flex items-center justify-between gap-2 mb-1">
            <div class="font-semibold text-sm">{{ chat.name }}</div>
            <div class="text-xs" :class="chat.unread > 0 ? ' text-lime-500' : 'text-gray-400'">
              {{ chat.time }}
            </div>
          </div>
          <div class="flex items-center">
            <span
              class="text-xs w-full line-clamp-1"
              :class="chat.unread > 0 ? ' text-lime-500 font-semibold' : 'text-gray-500'"
              >{{ chat.message }}</span
            >
            <Badge v-if="chat.unread > 0" :value="chat.unread" size="small"></Badge>
          </div>
        </div>
      </li>
    </RouterLink>
=======
    <ChatItem v-for="(chat, n) in chats" :key="n" :chat="chat" />
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
  </ul>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import InputText from 'primevue/inputtext'
<<<<<<< HEAD
import Badge from 'primevue/badge'
import { RouterLink } from 'vue-router'
=======
import ChatItem from './ChatItem.vue'
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0

const search = ref('')
const chats = ref([
  {
    id: 1,
    name: 'TechPulse Company',
    lastMessage: 'Reminder that we have a project meet zefkuhilnl',
    avatar: '/images/default_avatar.jpg',
    time: '13:02',
<<<<<<< HEAD
    unread: 5,
=======
    unread: 15,
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
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
<<<<<<< HEAD
    message: 'Just finished a workout and feeli',
=======
    lastMessage: 'Just finished a workout and feeli',
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
    avatar: '/images/default_avatar.jpg',
    time: '13:02',
    unread: 2,
  },
  // Add other chat entries...
])
</script>

<style scoped></style>
