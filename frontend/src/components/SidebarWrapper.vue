<script setup lang="ts">
import { useAppStore } from '@/stores/app';
import ChatList from './ChatList.vue';
import NotificationList from './NotificationList.vue';
<<<<<<< HEAD
=======
import GroupList from './GroupList.vue';
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0

const appStore = useAppStore()
</script>

<template>
  <div class="w-72 bg-white dark:bg-gray-800 border-r border-gray-500 p-4 overflow-y-auto">
    <ChatList v-if="appStore.activePanel === 'chat'" />
    <NotificationList  v-if="appStore.activePanel === 'notification'" />
<<<<<<< HEAD
  </div>
</template>
=======
    <GroupList  v-if="appStore.activePanel === 'group'" />
  </div>
</template>


>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
