<!-- src/components/ChatWindow.vue -->
<template>
  <div class="flex-1 flex flex-col h-full">
    <!-- Header -->
    <ChatHeader v-if="chatStore.currentChat" :user="chatStore.currentChat" />

    <!-- Messages -->
    <div class="flex-1 p-4 overflow-y-auto space-y-4" ref="messagesContainer">
      <div v-if="loading" class="flex justify-center items-center h-full">
        <i class="pi pi-spinner animate-spin"></i>
      </div>
      <div v-else-if="error" class="text-rose-500 text-center p-4">
        {{ error }}
        <Button v-if="error" class="mt-2" severity="secondary" size="small">
          Retry Connection
        </Button>
      </div>
      <div v-else-if="true" class="flex justify-center items-center h-full text-gray-500">
        No messages yet. Start the conversation!
      </div>
      <template v-else>
        <div v-for="(message, index) in messages" :key="index" class="flex flex-col">
          <!-- <div v-if="shouldShowDateSeparator(index)" class="flex justify-center my-4">
            <span class="text-sm text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
              {{ formatDate(message.createdAt) }}
            </span>
          </div> -->

          <MessageBubble
            :text="message.text"
            :time="formatTime(message.createdAt)"
            :seen="message.status === MessageStatus.READ"
            :isOwn="message.userId === authStore.user?.id"
            :isEdited="message.edited"
            :status="getMessageStatus(message)"
            :username="
              message.userId === authStore.user?.id
                ? authStore.user?.username
                : chatStore.currentChat?.username
            "
          >
            {{ message.text }}
          </MessageBubble>
        </div>
      </template>
    </div>

    <!-- Input -->
    <div class="h-16">
      <ChatSender />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useChatService } from '@/composables/useChatService'
import { useUserService } from '@/composables/useUserService'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import ChatSender from './ChatSender.vue'
import ChatHeader from './ChatHeader.vue'
import Button from 'primevue/button'
import { MessageStatus, type Message } from '@/grpc/chat/chat_pb'
import MessageBubble from './MessageBubble.vue'

const loading = ref(false)
const error = ref(null)
const messages = ref<Message[]>([])

const chatService = useChatService()
const userService = useUserService()
const chatStore = useChatStore()
const authStore = useAuthStore()

onMounted(async () => {
  try {
    // get all messages by convId
    const res = await chatService.getConvMessage({ convId: chatStore.currentConv?.id })
    if (!res.success) {
      throw Error(res.message)
    }
    console.log(res.messageList)
    messages.value = res.messageList
  } catch (error) {
    console.log(error)
  }
})

const formatTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const getMessageStatus = (message: Message): string => {
  if (!authStore.user?.id || message.userId !== authStore.user?.id) return ''

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

const scrollToBottom = () => {
  console.log('scroll')
}

const handleScroll = async (event: Event) => {
  console.log(scroll)
}
</script>

<style scoped>
.messages-container {
  scroll-behavior: smooth;
}
</style>
