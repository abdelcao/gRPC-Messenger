import type { GroupConv, PrivateConv } from '@/grpc/chat/chat_pb'
import type { User } from '@/grpc/user/user_pb'
import type { Message } from '@bufbuild/protobuf'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  const currentChat = ref<User | null>(null)
  const currentConv = ref<PrivateConv | null>(null)
  const privateConv = ref<Record<string, PrivateConv>>({})
  const groupConv = ref<Record<number, GroupConv[]>>({})

  function setPrivConc(list: PrivateConv[]) {
    const newObj: Record<string, PrivateConv> = {}
    list.forEach((conv) => {
      newObj[conv.id.toString()] = conv
    })
    privateConv.value = newObj // Replace whole object — triggers reactivity
  }

  function addPrivateConv(newConv: PrivateConv) {
    privateConv.value = {
      ...privateConv.value,
      [newConv.id.toString()]: newConv,
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

  return {
    currentChat,
    currentConv,
    privateConv,
    groupConv,

    setPrivConc,
    updatePrivConv,
    setCurrentConv,
    addPrivateConv
  }
})
