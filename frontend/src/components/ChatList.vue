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
      <ul class="flex flex-col gap-3">
        <li
          v-for="res in searchRes"
          :key="res.id.toString()"
          class="bg-gray-500/50 px-2 py-2 rounded"
        >
          <div class="flex items-center justify-center gap-2">
            <span class="shrink-0 w-8 h-8">
              <Avatar size="normal" shape="circle" image="/images/default_avatar.jpg" />
            </span>
            <span class="w-full">
              {{ res.username }}
            </span>

            <span
              class="text-lime-400 w-6 h-6 cursor-pointer flex items-center justify-center bg-lime-500/25 px-2 rounded"
              @click="handleSearchClick(res)"
            >
              <i class="pi pi-arrow-right"></i>
            </span>
          </div>
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
import Avatar from 'primevue/avatar'
import { RouterLink, useRouter } from 'vue-router'

// Services and stores
const chatService = useChatService()
const userService = useUserService()
const chatStore = useChatStore()
const authStore = useAuthStore()
const router = useRouter()

// State
const search = ref('')
const searchRes = ref<User[]>([])
const loading = ref(false)
const error = ref<unknown>(null)

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

async function handleSearchClick(user: User) {
  try {
    if (!authStore.user?.id) {
      throw Error('User unauthenticated')
    }
    // set current chat user
    chatStore.currentChat = user
    // create new conversation
    const res = await chatService.createConversation(authStore.user?.id)
    console.log(res)

    // add conversation to store with otherUser.id as key
    chatStore.conversations[user.id.toString()] = res

    // route to chat/:id (id is conversation id)
    router.push({ name: 'chat', params: { id: res.id.toString() } })
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.overflow-y-auto {
  max-height: calc(100vh - 200px); /* Adjust based on your layout */
}
</style>
