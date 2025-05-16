<script setup lang="ts">
import type { Conversation } from '@/grpc/chat/chat_pb'
import Avatar from 'primevue/avatar'
import Badge from 'primevue/badge'
import { RouterLink } from 'vue-router'

interface Chat {
  id: number
  unread: number
  avatar: string
  name: string
  time: string
  lastMessage: string
}

const props = defineProps<{
  chat: Conversation
}>()
</script>

<template>
  <RouterLink
    :to="{
      name: 'chat',
      params: {
        id: Number(chat.id),
      },
    }"
  >
    <li
      class="flex items-center  gap-3 grow-0 py-2 px-2 rounded cursor-pointer"
      :class="
        chat.unread > 0
          ? 'bg-lime-500/25'
          : 'bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600'
      "
    >
      <!-- <img :src="chat.avatar" class="w-10 h-10 rounded-full" /> -->
      <Avatar :image="chat.avatar" size="large" shape="circle" class="w-10 h-10" />
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
            >{{ chat.lastMessage }}</span
          >
          <Badge
            v-if="chat.unread > 0"
            :value="chat.unread < 9 ? chat.unread : '+9'"
            size="small"
          ></Badge>
        </div>
      </div>
    </li>
  </RouterLink>
</template>
