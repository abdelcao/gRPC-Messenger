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
async function getConversationName(conversation: Conversation): Promise<void> {
  try {
    // Try to get private conversation details
    const privateConv = await chatService.getPrivateConversation(conversation.id)
    if (privateConv) {
      const otherUserId = await getOtherUserId(privateConv)
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

      // Fetch user info if not in cache
      const user = await userService.getUser(BigInt(otherUserId))
      userCache.value.set(otherUserId, user)
      conversationNames.value.set(conversation.id.toString(), user.username)
      return
    }
  } catch (err) {
    // If not a private conversation, try to get group conversation
    try {
      const groupConv = await chatService.getGroupConversation(conversation.id)
      if (groupConv) {
        conversationNames.value.set(conversation.id.toString(), groupConv.name)
        return
      }
    } catch (groupErr) {
      console.error('Error fetching group conversation:', groupErr)
    }
  }

  // Fallback if neither private nor group conversation is found
  conversationNames.value.set(conversation.id.toString(), `Conversation ${conversation.id}`)
}

// Update or add a conversation in the chatStore
function updateOrAddConversation(newConv: Conversation) {
  const idx = chatStore.conversations.findIndex(c => c.id === newConv.id);
  if (idx !== -1) {
    chatStore.conversations.splice(idx, 1, newConv);
  } else {
    chatStore.conversations.push(newConv);
  }
   // Sort conversations: most recent lastMessage (or updatedAt) first
   chatStore.conversations.sort((a, b) => {
  const aLast = (a as any).lastMessage;
  const bLast = (b as any).lastMessage;
  const aTime = (aLast && aLast.createdAt) ? new Date(aLast.createdAt).getTime() : new Date(a.updatedAt).getTime();
  const bTime = (bLast && bLast.createdAt) ? new Date(bLast.createdAt).getTime() : new Date(b.updatedAt).getTime();
  return bTime - aTime;
});
}

// Load conversations and usernames (streaming version)
async function loadData() {
  if (!currentUserId.value) {
    error.value = 'User not authenticated'
    return
  }

  loading.value = true
  error.value = null

  try {
    // Start streaming conversations
    const stream = chatService.getUserConversations(BigInt(currentUserId.value))
    // Process each conversation as it arrives
    for await (const conversation of stream) {
      updateOrAddConversation(conversation);
      // Hide loading as soon as we get the first conversation
      if (loading.value && chatStore.conversations.length > 0) {
        loading.value = false;
      }
      // Optionally update conversation name cache
      await getConversationName(conversation);
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

<style scoped></style>
