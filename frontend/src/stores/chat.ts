import type { GroupConv, PrivateConv } from "@/grpc/chat/chat_pb";
import type { User } from "@/grpc/user/user_pb";
import type { Message } from "@bufbuild/protobuf";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useChatStore = defineStore("chat", () => {

  const currentChat = ref<User | null>(null)
  const privateConv = ref<Record<string ,PrivateConv>>({})
  const groupConv = ref<Record<number, GroupConv[]>>({})

  function setPrivConc (list: PrivateConv[]) {
    list.forEach(conv => {
      privateConv.value[conv.id.toString()] = conv
    })
  }

  function updatePrivConv (conv: PrivateConv) {
    privateConv.value[conv.id.toString()] = conv
  }

  return {
    currentChat,
    privateConv,
    groupConv,

    setPrivConc,
    updatePrivConv,
  };
});
