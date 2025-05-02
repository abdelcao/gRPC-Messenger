import { createClient } from '@connectrpc/connect'
import { createGrpcWebTransport } from '@connectrpc/connect-web'
import { AdminService } from '@/grpc/admin/admin_pb'

export function useAdminService() {
  const transport = createGrpcWebTransport({
    baseUrl: "http://localhost:8080",
    useBinaryFormat: true,
  })

  const client = createClient(AdminService, transport)
  return client
}
