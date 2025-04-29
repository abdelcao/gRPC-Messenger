import { createClient } from '@connectrpc/connect'
import { createConnectTransport } from '@connectrpc/connect-web'
import { AdminService } from '@/grpc/admin/admin_pb'

export function useAdminService() {
  const transport = createConnectTransport({
    baseUrl: import.meta.env.ENVOY_URL, // your Envoy port
  })

  const client = createClient(AdminService, transport)
  return client
}
