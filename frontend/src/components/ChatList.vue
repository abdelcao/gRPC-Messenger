<!-- src/components/ChatList.vue -->
<template>
  <div class="flex flex-col h-full">
    <InputText v-model="search" placeholder="Search conversations..." class="w-full mb-4 h-10" />

    <div v-if="loading" class="flex justify-center p-4">
      <i class="pi pi-spinner animate-spin"></i>
    </div>

    <div v-else-if="error" class="text-red-500 text-center p-4">
      {{ error }}
    </div>

    <!-- <div v-else-if="filteredConversations.length === 0" class="text-center text-gray-500 p-4">
      No conversations found
    </div> -->

    <div v-else-if="searchRes.length > 0">
      <ul>
        <li v-for="res in searchRes" :key="res.id.toString()">
          <span>
            {{ res.username }}
          </span>
        </li>
      </ul>
    </div>

    <ul v-else class="flex flex-col gap-2 overflow-y-auto">
      <!-- <ChatItem
        v-for="conversation in filteredConversations"
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
import { useUserService } from '@/composables/useUserService'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import type { Conversation, PrivateConversation, GroupConversation } from '@/grpc/chat/chat_pb'
import type { User } from '@/grpc/user/user_pb'
import InputText from 'primevue/inputtext'
import ProgressSpinner from 'primevue/progressspinner'
import ChatItem from './ChatItem.vue'
import { throttle } from '@/libs/utils'

// Services and stores
const chatService = useChatService()
const userService = useUserService()
const chatStore = useChatStore()
const authStore = useAuthStore()

// State
const search = ref('')
const searchRes = ref<User[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

watch(
  search,
  throttle(async () => {
    loading.value = true
    try {
      // get users list by username or email
      if (!search.value) {
        searchRes.value = []
        return
      }
      const res = await userService.searchUsers({ searchTerm: search.value })
      console.log(res)
      searchRes.value = res.users
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  }, 500),
)
</script>

<style scoped>
.overflow-y-auto {
  max-height: calc(100vh - 200px); /* Adjust based on your layout */
}
</style>
