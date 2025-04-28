<script setup lang="ts">
import { ref } from "vue";

// Import from your generated gRPC files
import { createPromiseClient } from '@connectrpc/connect-web';
import { AdminService } from "@/grpc/protos/admin/admin_service_connect";
import { CheckRequest } from "@/grpc/protos/admin/admin_service_pb";

// Create a gRPC-Web Promise client
const client = createPromiseClient(AdminService, {
  baseUrl: "http://localhost:8080", // envoy
  useHttpGet: false,                // POST requests (standard gRPC-Web)
});

// Reactive data
const inputData = ref("");
const status = ref<boolean | null>(null);
const message = ref<string | null>(null);

// Call AdminService.Check RPC
const callCheck = async () => {
  try {
    const request = new CheckRequest({ data: inputData.value });

    const response = await client.check(request);

    status.value = response.status;
    message.value = response.message;
  } catch (error) {
    console.error("gRPC error:", error);
    message.value = "gRPC call failed.";
  }
};
</script>

<template>
  <div class="p-6 max-w-md mx-auto">
    <h1 class="text-2xl font-bold mb-4">gRPC AdminService Check</h1>

    <input
      v-model="inputData"
      type="text"
      placeholder="Enter some data"
      class="border px-3 py-2 w-full rounded mb-4"
    />

    <button
      @click="callCheck"
      class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded w-full"
    >
      Send to AdminService
    </button>

    <div v-if="message" class="mt-6 p-4 bg-gray-100 rounded">
      <p><strong>Status:</strong> {{ status }}</p>
      <p><strong>Message:</strong> {{ message }}</p>
    </div>
  </div>
</template>
