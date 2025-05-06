import type { User } from "@/grpc/user/user_pb";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useChatStore = defineStore("chat", () => {

  const currentChat = ref<User>({} as User)

  return {
    currentChat
  }
<<<<<<< HEAD
})
=======
})
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
