<!-- src/components/ChatLayout.vue -->
<template>
  <div class="flex h-screen bg-blue-200">
    <Sidebar />
    <ChatList />
    <ChatWindow />
    <!-- <ChatInfoPanel /> -->
  </div>
</template>

<script setup lang="ts">
import Sidebar from '@/components/Sidebar.vue'
import ChatList from '@/components/ChatList.vue'
import ChatWindow from '@/components/ChatWindow.vue'
import ChatInfoPanel from '@/components/ChatInfoPanel.vue'
</script>
