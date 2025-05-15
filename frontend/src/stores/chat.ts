import type { Conversation, GroupConversation, PrivateConversation } from '@/grpc/chat/chat_pb'
import type { User } from '@/grpc/user/user_pb'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  const currentConversation = ref<Conversation | PrivateConversation | GroupConversation>(
    {} as Conversation,
  )
  function setCurrentConversation(chat: Conversation | PrivateConversation | GroupConversation) {
    currentConversation.value = chat
  }

  const conversationList = ref<Conversation[]>([]);
  function setConversationList (list: Conversation[]) {
    conversationList.value = list
  }


  return {
    currentConversation,
    setCurrentConversation,
    conversationList,
    setConversationList

  }
})
