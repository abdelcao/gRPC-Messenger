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
      <div v-else-if="error" class="text-rose-500 text-center p-4">
        {{ error }}
        <Button v-if="error" @click="retryConnection" class="mt-2" severity="secondary" size="small">
          Retry Connection
        </Button>
      </div>
      <div v-else-if="messagesLength === 0" class="flex justify-center items-center h-full text-gray-500">
        No messages yet. Start the conversation!
      </div>
      <template v-else>
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
            :username="messageUsernames.get(message.id.toString()) || 'Loading...'"
          >
            {{ message.text }}
          </MessageBubble>
        </div>
      </template>
    </div>

    <!-- Input -->
    <div class=" h-16">
      <ChatSender
        @send="handleSendMessage"
        :disabled="!currentConversation || !isConnected"
        :placeholder="getInputPlaceholder()"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed, onUnmounted } from 'vue'
import { useChatService } from '@/composables/useChatService'
import { useUserService } from '@/composables/useUserService'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import type { Message } from '@/grpc/chat/chat_pb'
import { MessageStatus } from '@/grpc/chat/chat_pb'
import type { User } from '@/grpc/user/user_pb'
import MessageBubble from './MessageBubble.vue'
import ChatSender from './ChatSender.vue'
import ChatHeader from './ChatHeader.vue'
import ProgressSpinner from 'primevue/progressspinner'
import Button from 'primevue/button'
import { storeToRefs } from 'pinia'

const chatService = useChatService()
const userService = useUserService()
const chatStore = useChatStore()
const authStore = useAuthStore()
const messagesContainer = ref<HTMLElement | null>(null)
const isLoadingMore = ref(false)
const hasMoreMessages = ref(true)
const isConnected = ref(true)
const PAGE_SIZE = 50
const RECONNECT_DELAY = 5000 // 5 seconds
const MAX_RECONNECT_ATTEMPTS = 3

const { currentChat, messages, loading, error, currentConversation } = storeToRefs(chatStore)

const currentUserId = computed(() => {
  if (!authStore.user?.id) return undefined
  return authStore.user.id
})

const messagesLength = computed(() => messages.value.length)

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
  if (!currentConversation.value) return 'Select a conversation to start chatting'
  if (!isConnected.value) return 'Reconnecting...'
  if ('name' in currentConversation.value) {
    return `Message ${currentConversation.value.name}`
  }
  return 'Type a message...'
}

const handleSendMessage = async (text: string) => {
  if (!currentUserId.value || !chatStore.currentConversation || !isConnected.value) return

  // Create a temporary message
  const tempId = 'temp-' + Date.now()
  const tempMessage = {
    id: tempId as unknown as bigint,
    userId: currentUserId.value,
    conversationId: BigInt(chatStore.currentConversation.id),
    text,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    status: MessageStatus.SENT,
    edited: false,
    $typeName: "chat.Message",
  } as Message

  // Add temporary message to store
  chatStore.addMessage(tempMessage)
  scrollToBottom()

  try {
    const message = await chatService.sendMessage(
      currentUserId.value,
      BigInt(chatStore.currentConversation.id),
      text
    )

    // Remove the temporary message and add the real one
    chatStore.removeMessage(tempId as unknown as bigint)
    chatStore.addMessage(message)
    scrollToBottom()
  } catch (err) {
    // Update the temporary message status to indicate failure
    chatStore.updateMessage(tempId as unknown as bigint, {
      status: MessageStatus.DELIVERED,
      text: text + ' (Failed to send)'
    })
    chatStore.setError(err instanceof Error ? err.message : 'Failed to send message')
    isConnected.value = false
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const handleScroll = async (event: Event) => {
  const container = event.target as HTMLElement
  if (!container || isLoadingMore.value || !hasMoreMessages.value) return

  // Load more when user scrolls to top
  if (container.scrollTop === 0) {
    await loadMoreMessages()
  }
}

const loadMoreMessages = async () => {
  if (!chatStore.currentConversation || isLoadingMore.value || !hasMoreMessages.value) return

  isLoadingMore.value = true
  const currentMessages = [...messages.value]
  const oldestMessageId = currentMessages[0]?.id
  const scrollHeight = messagesContainer.value?.scrollHeight || 0

  try {
    const stream = chatService.getConversationMessages(BigInt(chatStore.currentConversation.id))
    const newMessages: Message[] = []
    let messageCount = 0

    for await (const message of stream) {
      if (oldestMessageId && message.id >= oldestMessageId) {
        continue
      }

      newMessages.push(message)
      messageCount++

      if (messageCount >= PAGE_SIZE) {
        break
      }
    }

    hasMoreMessages.value = messageCount === PAGE_SIZE

    const allMessages = [...newMessages, ...currentMessages].sort((a, b) =>
      new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
    )

    chatStore.setMessages(allMessages)

    // Maintain scroll position
    if (messagesContainer.value) {
      const newScrollHeight = messagesContainer.value.scrollHeight
      messagesContainer.value.scrollTop = newScrollHeight - scrollHeight
    }
  } catch (err) {
    console.error('Error loading more messages:', err)
    chatStore.setError('Failed to load more messages')
  } finally {
    isLoadingMore.value = false
  }
}

let messageStreamCancel: (() => void) | null = null
let reconnectAttempts = 0
let reconnectTimeout: number | null = null

const startMessageStream = async (conversationId: bigint) => {
  if (messageStreamCancel) {
    console.log('Cancelling previous message stream')
    messageStreamCancel()
  }

  try {
    const stream = chatService.getConversationMessages(conversationId)
    console.log('Starting message stream for conversation', conversationId)

    messageStreamCancel = () => {
      if (typeof (stream as any).cancel === 'function') {
        (stream as any).cancel()
        console.log('Message stream cancelled')
      }
    }

    isConnected.value = true
    reconnectAttempts = 0
    chatStore.setError(null)

    for await (const message of stream) {
      console.log('Received message from stream:', message)
      // Check if message is from current conversation
      if (message.conversationId === conversationId) {
        // Check if this is a temporary message that needs to be replaced
        const isTemporaryMessage = messages.value.some(m =>
          m.id.toString().startsWith('temp-') &&
          m.userId === message.userId &&
          m.text === message.text
        )

        if (isTemporaryMessage) {
          // Remove the temporary message
          const tempMessage = messages.value.find(m =>
            m.id.toString().startsWith('temp-') &&
            m.userId === message.userId &&
            m.text === message.text
          )
          if (tempMessage) {
            chatStore.removeMessage(tempMessage.id)
          }
        }

        // Only add the message if it doesn't already exist
        if (!messages.value.some(m => m.id === message.id)) {
          chatStore.addMessage(message)
          // Only scroll to bottom if the new message is from the current user
          if (message.userId === currentUserId.value) {
            scrollToBottom()
          }
        }
      }
    }
  } catch (err) {
    console.error('Message stream error:', err)
    isConnected.value = false
    chatStore.setError('Connection lost. Attempting to reconnect...')

    if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
      reconnectAttempts++
      reconnectTimeout = window.setTimeout(() => {
        if (chatStore.currentConversation) {
          startMessageStream(BigInt(chatStore.currentConversation.id))
        }
      }, RECONNECT_DELAY * reconnectAttempts)
    } else {
      chatStore.setError('Connection failed after multiple attempts. Please try again.')
    }
  }
}

const retryConnection = () => {
  if (reconnectTimeout) {
    clearTimeout(reconnectTimeout)
  }
  reconnectAttempts = 0
  if (chatStore.currentConversation) {
    startMessageStream(BigInt(chatStore.currentConversation.id))
  }
}

// Add a new method to handle message updates
const handleMessageUpdate = (message: Message) => {
  if (message.conversationId === BigInt(chatStore.currentConversation?.id || 0)) {
    chatStore.updateMessage(message.id, message)
  }
}

// Add a new method to handle message deletion
const handleMessageDelete = (messageId: bigint) => {
  chatStore.removeMessage(messageId)
}

// Update the watch to handle conversation changes more robustly
watch(() => chatStore.currentConversation, (conv) => {
  if (conv) {
    // Clear existing messages when switching conversations
    chatStore.setMessages([])
    startMessageStream(BigInt(conv.id))
  }
}, { immediate: true })

// Add user cache
const userCache = ref<Map<number, User>>(new Map())

// Function to get user from cache or fetch from database
const getUser = async (userId: bigint): Promise<User | null> => {
  const numericId = Number(userId)

  // Check cache first
  const cachedUser = userCache.value.get(numericId)
  if (cachedUser) {
    return cachedUser
  }

  try {
    // Fetch user if not in cache
    const user = await userService.getUser(userId)
    userCache.value.set(numericId, user)
    return user
  } catch (err) {
    console.error('Error fetching user:', err)
    return null
  }
}

// Update getMessageUsername to use the database
const getMessageUsername = async (message: Message): Promise<string> => {
  if (message.userId === currentUserId.value) {
    return 'You'
  }

  // If it's a group conversation, show the group name
  if (chatStore.currentConversation && 'name' in chatStore.currentConversation) {
    return chatStore.currentConversation.name
  }

  // Get user from cache or database
  const user = await getUser(message.userId)
  return user?.username || 'User ' + message.userId.toString()
}

// Add a computed property for message usernames
const messageUsernames = ref<Map<string, string>>(new Map())

// Function to update message usernames
const updateMessageUsernames = async () => {
  const newUsernames = new Map<string, string>()

  for (const message of messages.value) {
    if (!messageUsernames.value.has(message.id.toString())) {
      const username = await getMessageUsername(message)
      newUsernames.set(message.id.toString(), username)
    }
  }

  messageUsernames.value = new Map([...messageUsernames.value, ...newUsernames])
}

// Watch for new messages to update usernames
watch(messages, async () => {
  await updateMessageUsernames()
}, { deep: true })

onMounted(async () => {
  if (chatStore.currentConversation) {
    startMessageStream(BigInt(chatStore.currentConversation.id))
    await updateMessageUsernames()
  }
})

onUnmounted(() => {
  if (messageStreamCancel) {
    messageStreamCancel()
  }
  if (reconnectTimeout) {
    clearTimeout(reconnectTimeout)
  }
  chatStore.setMessages([])
  chatStore.setError(null)
})
</script>

<style scoped>
.messages-container {
  scroll-behavior: smooth;
}
</style>
