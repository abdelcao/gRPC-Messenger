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
        :current-user-id="currentUserId"
        :last-message="getLastMessage(conversation)"
        :other-user-name="conversationNames.get(conversation.id.toString()) || 'Loading...'"
        @click="handleConversationSelect(conversation)"
      />
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useChatService } from '@/composables/useChatService'
import { useUserService } from '@/composables/useUserService'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import type { Conversation, PrivateConversation, GroupConversation } from '@/grpc/chat/chat_pb'
import type { User } from '@/grpc/user/user_pb'
import InputText from 'primevue/inputtext'
import ProgressSpinner from 'primevue/progressspinner'
import ChatItem from './ChatItem.vue'

// Services and stores
const chatService = useChatService()
const userService = useUserService()
const chatStore = useChatStore()
const authStore = useAuthStore()

// State
const search = ref('')
const loading = ref(false)
const error = ref<string | null>(null)
const userCache = ref<Map<number, User>>(new Map())
const conversationNames = ref<Map<string, string>>(new Map())
const conversationCache = ref<Map<string, Conversation>>(new Map())

// Computed
const conversations = computed(() => chatStore.conversations)
const currentConversation = computed(() => chatStore.currentConversation)
const currentUserId = computed(() => {
  if (!authStore.user?.id) return undefined
  return typeof authStore.user.id === 'bigint' ? Number(authStore.user.id) : authStore.user.id
})

// Filter conversations based on search
const filteredConversations = computed(() => {
  const convs = conversations.value || []
  if (!search.value.trim()) return convs

  const searchLower = search.value.toLowerCase()
  return convs.filter((conv) => {
    if ('name' in conv) {
      // Group conversation
      return conv.name.toLowerCase().includes(searchLower)
    } else if ('receiverId' in conv) {
      // Private conversation
      const username = conversationNames.value.get(conv.id.toString())
      return username ? username.toLowerCase().includes(searchLower) : false
    }
    return false
  })
})

// Get the other user's ID in a private conversation
async function getOtherUserId(conversation: PrivateConversation): Promise<number | undefined> {
  if (!currentUserId.value) return undefined
  
  const receiverId = typeof conversation.receiverId === 'bigint' ? Number(conversation.receiverId) : conversation.receiverId
  
  // If the current user is the receiver, we need to get the owner from the conversation
  if (receiverId === currentUserId.value) {
    try {
      // Get conversation details if not in cache
      if (!conversationCache.value.has(conversation.conversationId.toString())) {
        const conv = await chatService.getConversation(conversation.conversationId)
        conversationCache.value.set(conversation.conversationId.toString(), conv)
      }
      
      const conv = conversationCache.value.get(conversation.conversationId.toString())
      if (!conv) return undefined
      
      return typeof conv.ownerId === 'bigint' ? Number(conv.ownerId) : conv.ownerId
    } catch (err) {
      console.error('Error fetching conversation:', err)
      return undefined
    }
  }
  
  return receiverId
}

// Get conversation name (group name or other user's username)
async function getConversationName(conversation: Conversation | PrivateConversation | GroupConversation): Promise<void> {
  if ('name' in conversation) {
    // Group conversation
    conversationNames.value.set(conversation.id.toString(), conversation.name)
  } else if ('receiverId' in conversation) {
    // Private conversation
    const otherUserId = await getOtherUserId(conversation as PrivateConversation)
    if (!otherUserId) {
      conversationNames.value.set(conversation.id.toString(), 'Loading...')
      return
    }

    // Check cache first
    const cachedUser = userCache.value.get(otherUserId)
    if (cachedUser) {
      conversationNames.value.set(conversation.id.toString(), cachedUser.username)
      return
    }

    try {
      // Fetch user info if not in cache
      const user = await userService.getUser(BigInt(otherUserId))
      userCache.value.set(otherUserId, user)
      conversationNames.value.set(conversation.id.toString(), user.username)
    } catch (err) {
      console.error('Error fetching user:', err)
      conversationNames.value.set(conversation.id.toString(), `User ${otherUserId}`)
    }
  }
}

// Load conversations and usernames
async function loadData() {
  if (!currentUserId.value) {
    error.value = 'User not authenticated'
    return
  }

  loading.value = true
  error.value = null

  try {
    // Load conversations
    const userConversations = await chatService.getUserConversations(BigInt(currentUserId.value))
    chatStore.setConversations(userConversations)

    // Load usernames for private conversations
    for (const conv of userConversations) {
      await getConversationName(conv)
    }
  } catch (err) {
    console.error('Error loading data:', err)
    if (err instanceof Error) {
      error.value = `Failed to load data: ${err.message}`
    } else if (typeof err === 'string') {
      error.value = err
    } else {
      error.value = 'Failed to load data. Please try again later.'
    }
  } finally {
    loading.value = false
  }
}

// Handle conversation selection
function handleConversationSelect(conversation: Conversation | PrivateConversation | GroupConversation) {
  chatStore.setCurrentConversation(conversation)
}

// Get last message (placeholder for now)
function getLastMessage(conversation: Conversation | PrivateConversation | GroupConversation): string {
  return (conversation as any).lastMessage || 'No messages yet'
}

// Initialize
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.overflow-y-auto {
  max-height: calc(100vh - 200px); /* Adjust based on your layout */
}
</style>
