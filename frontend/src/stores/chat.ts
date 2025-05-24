import type { GroupConv, Message, PrivateConv } from '@/grpc/chat/chat_pb'
import type { User } from '@/grpc/user/user_pb'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  const currentChat = ref<User | null>(null)
  const currentConv = ref<PrivateConv | null>(null)

  // this is for sidebar list
  const privateConv = ref<Record<string, PrivateConv>>({})
  const groupConv = ref<Record<number, GroupConv[]>>({})


  // conversations data
  const messages = ref<Record<number, Message[]>>({})

  // Stream management
  const activeStreams = ref({
    conversationStream: null as any,
    messageStreams: new Map<number, any>(), // convId -> stream reference
    streamControllers: new Map<number, AbortController>(), // convId -> AbortController
  })

  function setMessages (msgList: Message[], convId: bigint) {
    const id = Number(convId);
    const newObj: Record<number, Message[]> = {}
    newObj[id] = msgList
    messages.value = newObj
  }

  function pushMessage (msg: Message, convId: bigint) {
    const id = Number(convId);
    messages.value[id].push(msg)
  }


  function setPrivConc(list: PrivateConv[]) {
    const newObj: Record<string, PrivateConv> = {}
    list.forEach((conv) => {
      newObj[conv.id.toString()] = conv
    })
    privateConv.value = newObj // Replace whole object — triggers reactivity
  }

  function addPrivateConv(newConv: PrivateConv) {
    privateConv.value = {
      [newConv.id.toString()]: newConv,
      ...privateConv.value,
    }
  }

  function updatePrivConv(conv: PrivateConv) {
    privateConv.value[conv.id.toString()] = conv
  }

  function setCurrentConv(conv: PrivateConv) {
    if (conv && conv.otherUser) {
      currentChat.value = conv.otherUser
      currentConv.value = conv
    }
  }

  // Stream management methods
  function setConversationStream(stream: any) {
    activeStreams.value.conversationStream = stream
  }

  function addMessageStream(convId: bigint, stream: any, controller: AbortController) {
    const id = Number(convId)
    activeStreams.value.messageStreams.set(id, stream)
    activeStreams.value.streamControllers.set(id, controller)
  }

  function removeMessageStream(convId: bigint) {
    const id = Number(convId)
    const controller = activeStreams.value.streamControllers.get(id)
    if (controller) {
      controller.abort()
    }
    activeStreams.value.messageStreams.delete(id)
    activeStreams.value.streamControllers.delete(id)
  }

  // Cleanup all streams
  function cleanupAllStreams() {
    console.log('Cleaning up all chat streams...')

    // Close conversation stream
    if (activeStreams.value.conversationStream) {
      try {
        activeStreams.value.conversationStream.cancel?.()
      } catch (error) {
        console.warn('Error closing conversation stream:', error)
      }
      activeStreams.value.conversationStream = null
    }

    // Close all message streams
    for (const [convId, controller] of activeStreams.value.streamControllers) {
      try {
        controller.abort()
      } catch (error) {
        console.warn(`Error closing message stream for conversation ${convId}:`, error)
      }
    }

    activeStreams.value.messageStreams.clear()
    activeStreams.value.streamControllers.clear()
  }

  // Reset store state (for logout)
  function resetChatState() {
    cleanupAllStreams()
    currentChat.value = null
    currentConv.value = null
    privateConv.value = {}
    groupConv.value = {}
    messages.value = {}
  }


  return {
    currentChat,
    currentConv,
    privateConv,
    groupConv,
    messages,
    activeStreams,

    setPrivConc,
    updatePrivConv,
    setCurrentConv,
    addPrivateConv,
    setMessages,
    pushMessage,

    // Stream management
    setConversationStream,
    addMessageStream,
    removeMessageStream,
    cleanupAllStreams,
    resetChatState,
  }
})
