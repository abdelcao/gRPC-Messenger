<!-- src/components/ChatList.vue -->
<template>
  <div class="flex flex-col h-full">
    <InputText 
      v-model="search" 
      placeholder="Search conversations..." 
      class="w-full mb-4 h-10" 
    />
    
    <div v-if="loading" class="flex justify-center p-4">
      <ProgressSpinner />
    </div>
    
    <div v-else-if="error" class="text-red-500 text-center p-4">
      {{ error }}
    </div>
    
    <div v-else-if="filteredConversations.length === 0" class="text-center text-gray-500 p-4">
      No conversations found
    </div>
    
    <ul v-else class="flex flex-col gap-2 overflow-y-auto">
      <ChatItem 
        v-for="conversation in filteredConversations" 
        :key="conversation.id.toString()"
        :conversation="conversation"
        :is-active="currentConversation?.id === conversation.id"
        :current-user-id="getCurrentUserId()"
        :last-message="getLastMessage(conversation)"
        :other-user-name="getOtherUserName(conversation)"
        @click="handleConversationSelect(conversation)"
      />
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useChatService } from '@/composables/useChatService'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import type { Conversation, PrivateConversation, GroupConversation } from '@/grpc/chat/chat_pb'
import InputText from 'primevue/inputtext'
import ProgressSpinner from 'primevue/progressspinner'
import ChatItem from './ChatItem.vue'

const chatService = useChatService()
const chatStore = useChatStore()
const authStore = useAuthStore()

const search = ref('')
const loading = ref(false)
const error = ref<string | null>(null)

const conversations = computed(() => chatStore.conversations)
const currentConversation = computed(() => chatStore.currentConversation)

const filteredConversations = computed(() => {
  const convs = conversations.value || []
  if (!search.value.trim()) return convs

  const searchLower = search.value.toLowerCase()
  return convs.filter((conv: Conversation | PrivateConversation | GroupConversation) => {
    if ('name' in conv) {
      // Group conversation
      return conv.name.toLowerCase().includes(searchLower)
    } else if ('receiverId' in conv) {
      // Private conversation
      const otherUserName = getOtherUserName(conv)
      return otherUserName ? otherUserName.toLowerCase().includes(searchLower) : false
    }
    return false
  })
})

const handleConversationSelect = (conversation: Conversation | PrivateConversation | GroupConversation) => {
  chatStore.setCurrentConversation(conversation)
}

const loadConversations = async () => {
  if (!authStore.user?.id) {
    error.value = 'User not authenticated'
    return
  }

  loading.value = true
  error.value = null

  try {
    const userConversations = await chatService.getUserConversations(BigInt(authStore.user.id))
    chatStore.setConversations(userConversations)
  } catch (err) {
    console.error('Error loading conversations:', err)
    if (err instanceof Error) {
      error.value = `Failed to load conversations: ${err.message}`
    } else if (typeof err === 'string') {
      error.value = err
    } else {
      error.value = 'Failed to load conversations. Please try again later.'
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadConversations()
})

function getLastMessage(conversation: Conversation | PrivateConversation | GroupConversation): string {
  // TODO: Replace with actual logic to get the last message for the conversation
  // For now, return a placeholder
  return (conversation as any).lastMessage || 'No messages yet';
}

function getOtherUserName(conversation: Conversation | PrivateConversation | GroupConversation): string | undefined {
  // For group conversations, return undefined
  if ('name' in conversation) return undefined;
  // For private conversations, resolve the other user's name
  if ('receiverId' in conversation && 'ownerId' in conversation && authStore.user?.id !== undefined) {
    const userIdNum = typeof authStore.user.id === 'bigint' ? Number(authStore.user.id) : authStore.user.id;
    const ownerIdNum = typeof conversation.ownerId === 'bigint' ? Number(conversation.ownerId) : conversation.ownerId;
    const receiverIdNum = typeof conversation.receiverId === 'bigint' ? Number(conversation.receiverId) : conversation.receiverId;
    const otherId = ownerIdNum === userIdNum ? receiverIdNum : ownerIdNum;
    return `User ${otherId}`;
  }
  return undefined;
}

function getCurrentUserId(): number | undefined {
  if (authStore.user?.id !== undefined) {
    return typeof authStore.user.id === 'bigint' ? Number(authStore.user.id) : authStore.user.id;
  }
  return undefined;
}
</script>

<style scoped>
.overflow-y-auto {
  max-height: calc(100vh - 200px); /* Adjust based on your layout */
}
</style>
