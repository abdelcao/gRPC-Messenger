import { createClient } from '@connectrpc/connect'
import { createGrpcWebTransport } from '@connectrpc/connect-web'
import { UserService } from '@/grpc/user/user_pb'
import type { User } from '@/grpc/user/user_pb'

export function useUserService() {
  const transport = createGrpcWebTransport({
    baseUrl: 'http://localhost:8080',
    useBinaryFormat: true
  })

  const client = createClient(UserService, transport)

  return {
    getUser: async (userId: bigint) => {
      try {
        const response = await client.getUser({ id: userId })
        if (response.success && response.user) {
          return response.user
        }
        throw new Error(response.message || 'Failed to get user')
      } catch (error) {
        console.error('Error fetching user:', error)
        throw error
      }
    }
  }
} 