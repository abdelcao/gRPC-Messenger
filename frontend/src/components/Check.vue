<template>
  <div class="p-4 space-y-4">
    <input v-model="inputData" class="border p-2 rounded w-full" placeholder="Enter check string" />
    <button @click="callCheck" class="bg-blue-600 text-white px-4 py-2 rounded">Check</button>
    <div v-if="response">
      <p>Status: {{ response.status }}</p>
      <p>Message: {{ response.message }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createClient } from '@connectrpc/connect'
import { createConnectTransport } from '@connectrpc/connect-web'
import { AdminService } from '@/grpc/admin/admin_pb'

const transport = createConnectTransport({
  baseUrl: 'http://localhost:9090',
})

const client = createClient(AdminService, transport) // this gives you a Promise-based client
// Form state
const inputData = ref('')
const response = ref<{ status: boolean; message: string } | null>(null)

const callCheck = async () => {
  try {
    const res = await client.check({ data: inputData.value })
    response.value = res
  } catch (error) {
    console.error('Error calling check:', error)
    response.value = { status: false, message: 'Error calling check' }
  }
}
</script>
