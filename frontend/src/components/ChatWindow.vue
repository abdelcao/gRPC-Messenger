<!-- src/components/ChatWindow.vue -->
<template>
<<<<<<< HEAD

  <!-- Header -->
  <ChatHeader />

  <!-- Messages -->
  <div class="flex-1 p-4 overflow-y-auto space-y-2">
    <MessageBubble time="12:38">Hi, I’ve just finished my report.</MessageBubble>
    <MessageBubble :seen="true" :isOwn="true" time="12:42">Great job! How long did it take you?</MessageBubble>
    <MessageBubble time="12:49">About two hours. I had to check some data…</MessageBubble>
    <MessageBubble :isOwn="true" time="13:05">I have a question about the report…</MessageBubble>
=======
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
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
  </div>

  <!-- Input -->
  <ChatSender />
</template>

<script setup lang="ts">
import MessageBubble from './MessageBubble.vue'
<<<<<<< HEAD
import ChatSender from './ChatSender.vue';
import ChatHeader from './ChatHeader.vue';

=======
import ChatSender from './ChatSender.vue'
import ChatHeader from './ChatHeader.vue'
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
</script>
