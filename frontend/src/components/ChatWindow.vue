<!-- src/components/ChatWindow.vue -->
<template>
  <div class="flex-1 flex flex-col bg-white dark:bg-gray-800">
    <!-- Header -->
    <div class="flex items-center justify-between px-4 py-3 border-b border-gray-500">
      <div class="flex items-center gap-2">
        <img src="/images/default_avatar.jpg" class="w-10 h-10 rounded-full" />
        <div class="flex-1">
          <div class="font-semibold text-sm">Selected Chat</div>
          <div class="text-xs text-gray-400">Online</div>
        </div>
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
    <ChatSender />

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import MessageBubble from './MessageBubble.vue'
import ChatSender from './ChatSender.vue'

</script>
