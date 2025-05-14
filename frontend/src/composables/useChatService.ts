import { createClient } from '@connectrpc/connect'
import { createGrpcWebTransport } from '@connectrpc/connect-web'
import { useConnectInterceptor } from './useConnectInterceptor';
import { ChatService } from '@/grpc/chat/chat_pb';

export function useChatService() {
  // const authInterceptor = useConnectInterceptor();
  const transport = createGrpcWebTransport({
    baseUrl: "http://localhost:8080",
    useBinaryFormat: true,
    //interceptors: [authInterceptor]
  })

  const client = createClient(ChatService, transport)
  return client
}
