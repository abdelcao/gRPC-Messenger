<!-- src/components/ChatWindow.vue -->
<template>
  <div class="flex-1 flex flex-col">
    <!-- Header -->
    <ChatHeader :user="currentChat" />

    <!-- Messages -->
    <div class="flex-1 p-4 overflow-y-auto space-y-2" ref="messagesContainer">
      <div v-if="loading" class="flex justify-center">
        <ProgressSpinner />
      </div>
      <div v-else-if="error" class="text-red-500 text-center">
        {{ error }}
      </div>
      <template v-else>
        <MessageBubble 
          v-for="message in messages" 
          :key="message.id.toString()"
          :text="message.text"
          :time="formatTime(message.createdAt)"
          :seen="message.status === 2"
          :isOwn="message.userId === currentUserId"
        />
      </template>
    </div>

    <!-- Input -->
    <ChatSender @send="handleSendMessage" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useChatService } from '@/composables/useChatService'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import type { Message } from '@/grpc/chat/chat_pb'
import MessageBubble from './MessageBubble.vue'
import ChatSender from './ChatSender.vue'
import ChatHeader from './ChatHeader.vue'
import ProgressSpinner from 'primevue/progressspinner'

const chatService = useChatService()
const chatStore = useChatStore()
const authStore = useAuthStore()
const messagesContainer = ref<HTMLElement | null>(null)

const currentUserId = authStore.user?.id
const { currentChat, messages, loading, error } = chatStore

const formatTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const handleSendMessage = async (text: string) => {
  if (!currentUserId || !chatStore.currentConversation) return

  try {
    const message = await chatService.sendMessage(
      BigInt(currentUserId),
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

const loadMessages = async () => {
  if (!chatStore.currentConversation) return

  chatStore.setLoading(true)
  chatStore.setError(null)

  try {
    const stream = chatService.getConversationMessages(BigInt(chatStore.currentConversation.id))
    const newMessages: Message[] = []
    
    for await (const message of stream) {
      newMessages.push(message)
    }
    
    chatStore.setMessages(newMessages)
    scrollToBottom()
  } catch (err) {
    chatStore.setError(err instanceof Error ? err.message : 'Failed to load messages')
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
</script>

<style scoped>
.messages-container {
  scroll-behavior: smooth;
}
</style>
