<script setup lang="ts">
import { ref } from "vue";
import { AdminServiceClient } from "@/grpc/admin/AdminServiceClientPb";
import { CheckRequest } from "@/grpc/admin/admin_service_pb";

// Create the gRPC client
const client = new AdminServiceClient("http://localhost:8080", null, null);

// Reactive data
const inputData = ref("");
const status = ref<boolean | null>(null);
const message = ref<string | null>(null);

// Function to call gRPC Check
const callCheck = () => {
  const request = new CheckRequest();
  request.setData(inputData.value);

  client.check(request, {}, (err, response) => {
    if (err) {
      console.error("gRPC error:", err.message);
      status.value = false;
      message.value = "Error occurred: " + err.message;
    } else {
      status.value = response.getStatus();
      message.value = response.getMessage();
    }
  });
};
</script>

<template>
  <div class="p-4">
    <h1 class="text-xl font-bold mb-4">gRPC AdminService Check</h1>

    <input
      v-model="inputData"
      type="text"
      placeholder="Enter some text"
      class="border px-2 py-1 mb-4 w-full rounded"
    />

    <button
      @click="callCheck"
      class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
    >
      Send to AdminService
    </button>

    <div v-if="message" class="mt-4">
      <p><strong>Status:</strong> {{ status }}</p>
      <p><strong>Message:</strong> {{ message }}</p>
    </div>
  </div>
</template>
