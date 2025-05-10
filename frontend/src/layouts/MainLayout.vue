<!-- src/components/ChatLayout.vue -->
<template>
  <div class="flex h-screen bg-blue-200">
    <Sidebar />
    <SidebarWrapper />
    <main class="flex-1 flex flex-col bg-white dark:bg-gray-800">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import Sidebar from '@/components/TheSidebar.vue'
import ChatInfoPanel from '@/components/ChatInfoPanel.vue'
import SidebarWrapper from '@/components/SidebarWrapper.vue';
</script>
