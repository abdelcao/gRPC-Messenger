<script setup lang="ts">
import Avatar from 'primevue/avatar'
import Badge from 'primevue/badge'
import { useRouter } from 'vue-router'
import type { PrivateConv } from '@/grpc/chat/chat_pb'
import { timeAgo } from '@/libs/utils'
import { useChatStore } from '@/stores/chat'

defineProps<{
  conversation: PrivateConv
}>()

const chatStore = useChatStore()
const router = useRouter()

function handleClick(conv: PrivateConv) {
  chatStore.setCurrentConv(conv)
  router.push({ name: 'chat', params: { id: Number(conv.id) } })
}
</script>

<template>
  <div
    class="flex items-center gap-3 p-3 rounded-lg cursor-pointer hover:bg-gray-500/25 dark:hover:bg-gray-800/25"
    :class="{
      'bg-gray-100 dark:bg-gray-800':
        chatStore.currentChat && chatStore.currentChat.id === conversation.id,
    }"
    @click="handleClick(conversation)"
  >
    <Avatar image="/images/default_avatar.jpg" class="bg-primary" shape="circle" />
    <div class="flex-1 min-w-0">
      <div class="flex justify-between items-start">
        <h4 class="font-medium truncate">{{ conversation.otherUser?.username }}</h4>
        <span v-if="conversation.lastUpdate?.seconds" class="text-xs text-gray-500">{{
          timeAgo(conversation.lastUpdate?.seconds)
        }}</span>
      </div>
      <p class="text-sm text-gray-500 truncate">
        {{ conversation.lastMessage }}
      </p>
    </div>
    <div v-if="conversation.unreadCount > 0" class="flex-shrink-0">
      <Badge :value="conversation.unreadCount" severity="primary" />
    </div>
  </div>
</template>
