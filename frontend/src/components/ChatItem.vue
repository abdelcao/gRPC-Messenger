<script setup lang="ts">
import { computed } from 'vue'
import type { Conversation, PrivateConversation, GroupConversation } from '@/grpc/chat/chat_pb'
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
  conversation: Conversation | PrivateConversation | GroupConversation
  isActive?: boolean
  currentUserId?: number
  lastMessage?: string
  otherUserName?: string
}>()

defineEmits<{
  (e: 'click'): void
}>()

const getConversationName = computed(() => {
  if ('name' in props.conversation) {
    // Group conversation
    return props.conversation.name
  } else if (props.otherUserName) {
    // Private conversation, name provided
    return props.otherUserName
  } else {
    // Fallback
    return 'Private Chat'
  }
})

const getLastMessage = computed(() => {
  return props.lastMessage || 'No messages yet'
})

const unreadCount = computed(() => {
  // You would typically get this from the conversation's unread count
  return 0 // Replace with actual unread count
})

const formatTime = (timestamp: string) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  // If less than 24 hours, show time
  if (diff < 24 * 60 * 60 * 1000) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }

  // If less than 7 days, show day name
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return date.toLocaleDateString([], { weekday: 'short' })
  }

  // Otherwise show date
  return date.toLocaleDateString([], { month: 'short', day: 'numeric' })
}

const getInitials = (name: string) => {
  return name
    .split(' ')
    .map(word => word[0])
    .join('')
    .toUpperCase()
}
</script>

<template>
  <RouterLink
    :to="{
      name: 'chat',
      params: {
        id: conversation.id.toString(),
      },
    }"
  >
    <div
      class="flex items-center gap-3 p-3 rounded-lg cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800"
      :class="{ 'bg-gray-100 dark:bg-gray-800': isActive }"
      @click="$emit('click')"
    >
      <Avatar
        :label="getInitials(getConversationName)"
        class="bg-primary"
        shape="circle"
      />
      <div class="flex-1 min-w-0">
        <div class="flex justify-between items-start">
          <h4 class="font-medium truncate">{{ getConversationName }}</h4>
          <span class="text-xs text-gray-500">{{ formatTime(conversation.createdAt) }}</span>
        </div>
        <p class="text-sm text-gray-500 truncate">
          {{ getLastMessage }}
        </p>
      </div>
      <div v-if="unreadCount > 0" class="flex-shrink-0">
        <Badge :value="unreadCount" severity="primary" />
      </div>
    </div>
  </RouterLink>
</template>
