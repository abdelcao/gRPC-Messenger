<!-- src/components/ChatWindow.vue -->
<template>
  <div class="flex-1 flex flex-col bg-white dark:bg-gray-800">
    <!-- Header -->
    <div class="flex items-center justify-between px-4 py-3 border-b">
      <div>
        <h2 class="font-semibold">TechPulse Company</h2>
        <p class="text-xs text-gray-500">32 members, 6 online</p>
      </div>
    </div>

    <!-- Messages -->
    <div class="flex-1 p-4 overflow-y-auto space-y-2">
      <MessageBubble time="12:38">Hi, I’ve just finished my report.</MessageBubble>
      <MessageBubble :isOwn="true" time="12:42">Great job! How long did it take you?</MessageBubble>
      <MessageBubble time="12:49">About two hours. I had to check some data…</MessageBubble>
      <MessageBubble :isOwn="true" time="13:05">I have a question about the report…</MessageBubble>
    </div>

    <!-- Input -->
    <div class="border-t p-4 flex gap-2 items-center">
      <InputText v-model="message" placeholder="Your message" class="flex-1" />
      <Button icon="pi pi-send" @click="sendMessage" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import MessageBubble from './MessageBubble.vue'

const message = ref('')

function sendMessage() {
  if (message.value.trim()) {
    // Send logic here...
    message.value = ''
  }
}
</script>
