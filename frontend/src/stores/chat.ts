import type { User } from "@/grpc/user/user_pb";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useChatStore = defineStore("chat", () => {

  const currentChat = ref<User>({} as User)

  return {
    currentChat
  }
})