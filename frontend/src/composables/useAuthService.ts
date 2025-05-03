import { createClient } from '@connectrpc/connect'
import { createGrpcWebTransport } from '@connectrpc/connect-web'
import { AuthService } from '@/grpc/auth/auth_pb'

export function useAuthService() {
  const transport = createGrpcWebTransport({
    baseUrl: "http://localhost:8080",
    useBinaryFormat: true,
  })

  const client = createClient(AuthService, transport)
  return client
}
