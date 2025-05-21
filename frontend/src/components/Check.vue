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
import { ref } from 'vue'
import { useAdminService } from '@/composables/useAdminService'

const client = useAdminService()
// Form state
const inputData = ref('')
const response = ref<{ status: boolean; message: string } | null>(null)

const callCheck = async () => {
  try {
    const res = await client.doCheck({ data: inputData.value })
    console.log(res);
    response.value = res
  } catch (error) {
    console.error('Error calling check:', error)
    response.value = { status: false, message: 'Error calling check' }
  }
}
</script>
