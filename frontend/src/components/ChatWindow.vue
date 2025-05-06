<!-- src/components/ChatWindow.vue -->
<template>
  <div class="flex-1 flex flex-col">
    <!-- Header -->
    <ChatHeader />

    <!-- Messages -->
    <div class="flex-1 p-4 overflow-y-auto space-y-2">
      <MessageBubble time="12:38">Hi, I’ve just finished my report.</MessageBubble>
      <MessageBubble :seen="true" :isOwn="true" time="12:42"
        >Great job! How long did it take you?</MessageBubble
      >
      <MessageBubble time="12:49">About two hours. I had to check some data…</MessageBubble>
      <MessageBubble :isOwn="true" time="13:05">I have a question about the report…</MessageBubble>
    </div>

    <!-- Input -->
    <ChatSender />
  </div>
</template>

<script setup lang="ts">
import MessageBubble from './MessageBubble.vue'
import ChatSender from './ChatSender.vue'
import ChatHeader from './ChatHeader.vue'
</script>
