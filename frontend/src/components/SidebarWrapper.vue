<script setup lang="ts">
import { useAppStore } from '@/stores/app';
import ChatList from './ChatList.vue';
import NotificationList from './NotificationList.vue';

const appStore = useAppStore()
</script>

<template>
  <div class="w-72 bg-white dark:bg-gray-800 border-r border-gray-500 p-4 overflow-y-auto">
    <ChatList v-if="appStore.activePanel === 'chat'" />
    <NotificationList  v-if="appStore.activePanel === 'notification'" />
  </div>
</template>


