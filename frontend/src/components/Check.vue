<script setup lang="ts">
import { ref } from "vue";

// Import from your generated gRPC files
import { createConnectTransport } from "@connectrpc/connect-web";
import { createClient } from "@connectrpc/connect";
import { AdminService } from "@/grpc/protos/admin/admin_service_pb";
import type { CheckRequest } from "@/grpc/protos/admin/admin_service_pb";

// Set up the gRPC-Web transport (http1 POST)
const transport = createConnectTransport({
  baseUrl: "http://localhost:8080",
});

// Create the gRPC Promise Client
const client = createClient(AdminService, transport);

// Reactive data
const inputData = ref("");
const status = ref<boolean | null>(null);
const message = ref<string | null>(null);

// Call AdminService.Check
const callCheck = async () => {
  try {
    const response = await client.check({
      data: inputData.value,
    } satisfies CheckRequest);

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
