<!-- src/components/ChatList.vue -->
<template>
  <div class="flex flex-col h-full">
    <InputText v-model="search" placeholder="Search conversations..." class="w-full mb-4 h-10" />

    <div v-if="loading" class="flex justify-center p-4">
      <ProgressSpinner />
    </div>

    <div v-else-if="error" class="text-red-500 text-center p-4">
      <i class="pi pi-exclamation-triangle" style="font-size: 1.25rem"></i>
    </div>

    <!-- <div
      v-else-if="(countAsyncIterable(chatStore.conversationList)) === 0"
      class="text-center text-gray-500 p-4"
    >
      Search & Chat
    </div> -->

    <ul v-else class="flex flex-col gap-2 overflow-y-auto">
      <!-- <ChatItem
        v-for="conversation in chatStore.conversationList"
        :key="conversation.id.toString()"
        :conversation="conversation"
        :is-active="currentConversation?.id === conversation.id"
        :current-user-id="currentUserId"
        :last-message="getLastMessage(conversation)"
        :other-user-name="conversationNames.get(conversation.id.toString()) || 'Loading...'"
        @click="handleConversationSelect(conversation)"
      /> -->
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useChatService } from '@/composables/useChatService'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import { useUserService } from '@/composables/useUserService'
import type { Conversation, PrivateConversation, GroupConversation } from '@/grpc/chat/chat_pb'
import type { User } from '@/grpc/user/user_pb'
import InputText from 'primevue/inputtext'
import ProgressSpinner from 'primevue/progressspinner'
import ChatItem from './ChatItem.vue'
import { countAsyncIterable, throttle } from '@/libs/utils'
import { useToast } from 'primevue/usetoast'

// Services and stores
const chatService = useChatService()
const userService = useUserService()
const toast = useToast()
const chatStore = useChatStore()
const authStore = useAuthStore()

// State
const search = ref('')
const loading = ref(false)
const error = ref()

// Computed
const currentConversation = computed(() => chatStore.currentConversation)

onMounted(async () => {
  const userId = authStore.getUserId()
  if (!userId) return
  const conversations = chatService.getUserConversations({ userId: userId })
  for await (const conv of conversations) {
    chatStore.conversationList.push(conv)
  }
})

/**
 * GetUserConversations: {
 *    private: {
 *      id
 *      user: User
 *      lastMessage: Message
 *      unreadCount
 *    }[],
 *    groupe: {
 *      id
 *      lastMessage: Message
 *      unreadCount
 *    }[],
 * }
 *
 */

watch(
  search,
  throttle(() => {
    console.log(search.value)
  }, 500),
)
</script>

<style scoped></style>
