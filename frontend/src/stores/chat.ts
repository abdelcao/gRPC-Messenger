import type { User } from "@/grpc/user/user_pb";
import type { Message, Conversation, PrivateConversation, GroupConversation } from "@/grpc/chat/chat_pb";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useChatStore = defineStore("chat", () => {
  const currentChat = ref<User>({} as User);
  const messages = ref<Message[]>([]);
  const conversations = ref<(Conversation | PrivateConversation | GroupConversation)[]>([]);
  const currentConversation = ref<Conversation | PrivateConversation | GroupConversation | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const setCurrentChat = (user: User) => {
    currentChat.value = user;
  };

  const setMessages = (newMessages: Message[]) => {
    messages.value = newMessages;
  };

  const addMessage = (message: Message) => {
    messages.value.push(message);
  };

  const setConversations = (newConversations: (Conversation | PrivateConversation | GroupConversation)[]) => {
    conversations.value = newConversations;
  };

  const setCurrentConversation = (conversation: Conversation | PrivateConversation | GroupConversation | null) => {
    currentConversation.value = conversation;
  };

  const setLoading = (value: boolean) => {
    loading.value = value;
  };

  const setError = (value: string | null) => {
    error.value = value;
  };

  const clearChat = () => {
    currentChat.value = {} as User;
    messages.value = [];
    currentConversation.value = null;
    error.value = null;
  };

  const updateMessage = (id: string | number | bigint, newData: Partial<Message>) => {
    const idx = messages.value.findIndex(m => m.id === id)
    if (idx !== -1) {
      messages.value[idx] = { ...messages.value[idx], ...newData }
    }
  }

  const removeMessage = (id: string | number | bigint) => {
    const idx = messages.value.findIndex(m => m.id === id)
    if (idx !== -1) {
      messages.value.splice(idx, 1)
    }
  }

  return {
    currentChat,
    messages,
    conversations,
    currentConversation,
    loading,
    error,
    setCurrentChat,
    setMessages,
    addMessage,
    setConversations,
    setCurrentConversation,
    setLoading,
    setError,
    clearChat,
    updateMessage,
    removeMessage
  };
});
