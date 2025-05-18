import { ChatService } from '@/grpc/chat/chat_pb';
import { createClient } from '@connectrpc/connect'
import { createGrpcWebTransport } from '@connectrpc/connect-web'

export function useChatService() {
  // const authInterceptor = useConnectInterceptor();
  const transport = createGrpcWebTransport({
    baseUrl: "http://localhost:8080",
    useBinaryFormat: true,
    //interceptors: [authInterceptor]
  })

  const client = createClient(ChatService, transport)

  return {
    // Conversation operations
    createConversation: (ownerId: bigint) =>
      client.createConversation({ ownerId }),

    getConversation: (id: bigint) =>
      client.getConversation({ id }),

    // Message operations
    sendMessage: (userId: bigint, conversationId: bigint, text: string) =>
      client.sendMessage({ userId, conversationId, text }),

    editMessage: (messageId: bigint, newText: string) =>
      client.editMessage({ messageId, newText }),

    updateMessageStatus: (messageId: bigint, status: number) =>
      client.updateMessageStatus({ messageId, status }),

    getConversationMessages: (conversationId: bigint) =>
      client.getConversationMessages({ conversationId }),

    // Private conversation operations
    createPrivateConversation: (ownerId: bigint, receiverId: bigint) =>
      client.createPrivateConversation({ ownerId, receiverId }),

    getPrivateConversation: (conversationId: bigint) =>
      client.getPrivateConversation({ conversationId }),

    // Group conversation operations
    createGroupConversation: (ownerId: bigint, name: string) =>
      client.createGroupConversation({ ownerId, name }),

    getGroupConversation: (conversationId: bigint) =>
      client.getGroupConversation({ conversationId }),

    addMemberToGroup: (groupId: bigint, userId: bigint) =>
      client.addMemberToGroup({ groupId, userId }),

    removeMemberFromGroup: (groupId: bigint, userId: bigint) =>
      client.removeMemberFromGroup({ groupId, userId }),

    makeGroupAdmin: (groupId: bigint, userId: bigint) =>
      client.makeGroupAdmin({ groupId, userId }),

    // User conversations
    getUserConversations: (userId: bigint) =>
      client.getUserConversations({ userId }),
  }
}
