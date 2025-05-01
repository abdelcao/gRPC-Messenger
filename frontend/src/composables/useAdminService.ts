import { createClient } from '@connectrpc/connect'
import { createConnectTransport } from '@connectrpc/connect-web'
import { AdminService } from '@/grpc/admin/admin_pb'

export function useAdminService() {
  const transport = createConnectTransport({
    baseUrl: "http://localhost:8080", // your Envoy port
  })

  const client = createClient(AdminService, transport)
  return client
}
