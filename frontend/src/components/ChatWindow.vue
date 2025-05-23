<!-- src/components/ChatWindow.vue -->
<template>
  <div class="flex-1 flex flex-col h-full">
    <!-- Header -->
    <ChatHeader v-if="chatStore.currentChat" :user="chatStore.currentChat" />

    <!-- Messages -->
    <!-- <div
      class="flex-1 p-4 overflow-y-auto space-y-4"
      ref="messagesContainer"
      @scroll="handleScroll"
    > -->
      <!-- <div v-if="loading" class="flex justify-center items-center h-full">
        <i class="pi pi-spinner animate-spin"></i>
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
      <template v-else> -->

        <!-- <div v-for="(message, index) in messages" :key="message.id.toString()" class="flex flex-col">

          <div v-if="shouldShowDateSeparator(index)" class="flex justify-center my-4">
            <span class="text-sm text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
              {{ formatDate(message.createdAt) }}
            </span>
          </div>

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
    </div> -->

    <!-- Input -->
    <!-- <div class=" h-16">
      <ChatSender
        @send="handleSendMessage"
        :disabled="!currentConversation || !isConnected"
        :placeholder="getInputPlaceholder()"
      />
    </div> -->
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


</script>

<style scoped>
.messages-container {
  scroll-behavior: smooth;
}
</style>
