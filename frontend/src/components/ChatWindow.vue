<!-- src/components/ChatWindow.vue -->
<template>
  <div class="flex-1 flex flex-col h-full">
    <!-- Header -->
    <ChatHeader :user="currentChat" />

    <!-- Messages -->
    <div 
      class="flex-1 p-4 overflow-y-auto space-y-4" 
      ref="messagesContainer"
      @scroll="handleScroll"
    >
      <div v-if="loading" class="flex justify-center items-center h-full">
        <ProgressSpinner />
      </div>
      <div v-else-if="error" class="text-red-500 text-center p-4">
        {{ error }}
      </div>
      <div v-else-if="messagesLength === 0" class="flex justify-center items-center h-full text-gray-500">
        No messages yet. Start the conversation!
      </div>
      <template v-else>
        <!-- Add debug info -->
        <div class="text-xs text-gray-500 mb-2">
          Messages count: {{ messagesLength }}
        </div>
        <!-- Loading more indicator -->
        <div v-if="isLoadingMore" class="flex justify-center py-2">
          <ProgressSpinner style="width: 2rem; height: 2rem;" />
        </div>
        
        <div v-for="(message, index) in messages" :key="message.id.toString()" class="flex flex-col">
          <!-- Date separator -->
          <div v-if="shouldShowDateSeparator(index)" class="flex justify-center my-4">
            <span class="text-sm text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
              {{ formatDate(message.createdAt) }}
            </span>
          </div>
          
          <!-- Message bubble -->
          <MessageBubble 
            :text="message.text"
            :time="formatTime(message.createdAt)"
            :seen="message.status === MessageStatus.READ"
            :isOwn="message.userId === currentUserId"
            :isEdited="message.edited"
            :status="getMessageStatus(message)"
            :messageId="message.id.toString()"
          >
            {{ message.text }}
          </MessageBubble>
        </div>
      </template>
    </div>

    <!-- Input -->
    <div class="border-t border-gray-200 p-4">
      <ChatSender 
        @send="handleSendMessage" 
        :disabled="!currentConversation"
        :placeholder="getInputPlaceholder()"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed, onUnmounted } from 'vue'
import { useChatService } from '@/composables/useChatService'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import type { Message } from '@/grpc/chat/chat_pb'
import { MessageStatus } from '@/grpc/chat/chat_pb'
import MessageBubble from './MessageBubble.vue'
import ChatSender from './ChatSender.vue'
import ChatHeader from './ChatHeader.vue'
import ProgressSpinner from 'primevue/progressspinner'
import { storeToRefs } from 'pinia'

const chatService = useChatService()
const chatStore = useChatStore()
const authStore = useAuthStore()
const messagesContainer = ref<HTMLElement | null>(null)
const isLoadingMore = ref(false)
const hasMoreMessages = ref(true)
const PAGE_SIZE = 50

const { currentChat, messages, loading, error, currentConversation } = storeToRefs(chatStore)

const currentUserId = computed(() => {
  if (!authStore.user?.id) return undefined
  return authStore.user.id
})

// Add debug watcher for messages
watch(messages, (newMessages) => {
  console.log('Messages updated in ChatWindow:', newMessages)
}, { deep: true })

// Add debug computed for messages length
const messagesLength = computed(() => {
  console.log('Computing messages length:', messages.value.length)
  return messages.value.length
})

const formatTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const formatDate = (timestamp: string) => {
  const date = new Date(timestamp)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  if (date.toDateString() === today.toDateString()) {
    return 'Today'
  } else if (date.toDateString() === yesterday.toDateString()) {
    return 'Yesterday'
  } else {
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' })
  }
}

const shouldShowDateSeparator = (index: number) => {
  if (index === 0) return true
  
  const currentMessage = messages.value[index]
  const previousMessage = messages.value[index - 1]
  
  const currentDate = new Date(currentMessage.createdAt).toDateString()
  const previousDate = new Date(previousMessage.createdAt).toDateString()
  
  return currentDate !== previousDate
}

const getMessageStatus = (message: Message): string => {
  if (!currentUserId.value || message.userId !== currentUserId.value) return ''
  
  switch (message.status) {
    case MessageStatus.SENT:
      return 'sent'
    case MessageStatus.DELIVERED:
      return 'delivered'
    case MessageStatus.READ:
      return 'read'
    default:
      return ''
  }
}

const getInputPlaceholder = () => {
  if (!currentConversation) return 'Select a conversation to start chatting'
  if ('name' in currentConversation) {
    return `Message ${currentConversation.name}`
  }
  return 'Type a message...'
}

const handleSendMessage = async (text: string) => {
  if (!currentUserId.value || !chatStore.currentConversation) return

  try {
    const message = await chatService.sendMessage(
      currentUserId.value,
      BigInt(chatStore.currentConversation.id),
      text
    )
    chatStore.addMessage(message)
    scrollToBottom()
  } catch (err) {
    chatStore.setError(err instanceof Error ? err.message : 'Failed to send message')
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// Add scroll handler for pagination
const handleScroll = async (event: Event) => {
  const container = event.target as HTMLElement
  if (!container || isLoadingMore.value || !hasMoreMessages.value) return

  // Load more when user scrolls to top
  if (container.scrollTop === 0) {
    await loadMoreMessages()
  }
}

// Load more messages
const loadMoreMessages = async () => {
  if (!chatStore.currentConversation || isLoadingMore.value || !hasMoreMessages.value) return

  isLoadingMore.value = true
  const currentMessages = [...messages.value]
  const oldestMessageId = currentMessages[0]?.id

  try {
    const stream = chatService.getConversationMessages(BigInt(chatStore.currentConversation.id))
    const newMessages: Message[] = []
    let messageCount = 0
    
    for await (const message of stream) {
      // Only add messages older than the oldest message we have
      if (oldestMessageId && message.id >= oldestMessageId) {
        continue
      }
      
      newMessages.push(message)
      messageCount++
      
      // Stop if we've loaded enough messages
      if (messageCount >= PAGE_SIZE) {
        break
      }
    }
    
    // Update hasMoreMessages based on the number of messages received
    hasMoreMessages.value = messageCount === PAGE_SIZE
    
    // Sort and combine messages
    const allMessages = [...newMessages, ...currentMessages].sort((a, b) => 
      new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
    )
    
    chatStore.setMessages(allMessages)
    
    // Maintain scroll position
    if (messagesContainer.value) {
      const scrollHeight = messagesContainer.value.scrollHeight
      messagesContainer.value.scrollTop = scrollHeight - messagesContainer.value.clientHeight
    }
  } catch (err) {
    console.error('Error loading more messages:', err)
    chatStore.setError('Failed to load more messages')
  } finally {
    isLoadingMore.value = false
  }
}

const loadMessages = async () => {
  if (!chatStore.currentConversation) return
  chatStore.setLoading(true)
  chatStore.setError(null)
  chatStore.setMessages([])

  try {
    const stream = chatService.getConversationMessages(BigInt(chatStore.currentConversation.id))
    const newMessages: Message[] = []
    for await (const message of stream) {
      if (!message || !message.id) continue
      newMessages.push(message)
    }
    if (newMessages.length === 0) {
      chatStore.setMessages([])
      return
    }
    // Sort messages by creation time
    const sortedMessages = newMessages.sort((a, b) =>
      new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
    )
    chatStore.setMessages(sortedMessages)
    console.log('Messages set in store:', sortedMessages)
    scrollToBottom()
  } catch (err) {
    console.error('Error loading messages:', err)
    chatStore.setError('Failed to load messages. Please try again later.')
  } finally {
    chatStore.setLoading(false)
  }
}

watch(() => chatStore.currentConversation, () => {
  loadMessages()
})

onMounted(() => {
  loadMessages()
})

// Add cleanup when component is unmounted
onUnmounted(() => {
  chatStore.setMessages([])
  chatStore.setError(null)
})
</script>

<style scoped>
.messages-container {
  scroll-behavior: smooth;
}
</style>
