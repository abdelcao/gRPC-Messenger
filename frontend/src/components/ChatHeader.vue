<script setup lang="ts">
import { ref } from 'vue'
import type { User } from '@/grpc/user/user_pb'
import Avatar from 'primevue/avatar'
import Button from 'primevue/button'
import Menu from 'primevue/menu'

const props = defineProps<{
  user?: User
}>()

const menu = ref()
const menuItems = [
  {
    label: 'View Profile',
    icon: 'pi pi-user',
    command: () => handleViewProfile()
  },
  {
    label: 'Block User',
    icon: 'pi pi-ban',
    command: () => handleBlockUser()
  },
  {
    label: 'Clear Chat',
    icon: 'pi pi-trash',
    command: () => handleClearChat()
  }
]

const getInitials = (name?: string) => {
  if (!name) return ''
  return name
    .split(' ')
    .map(word => word[0])
    .join('')
    .toUpperCase()
}

const handleCall = () => {
  // Implement call functionality
  console.log('Call clicked')
}

const handleVideoCall = () => {
  // Implement video call functionality
  console.log('Video call clicked')
}

const toggleMenu = (event: Event) => {
  menu.value?.toggle(event)
}

const handleViewProfile = () => {
  // Implement view profile functionality
  console.log('View profile clicked')
}

const handleBlockUser = () => {
  // Implement block user functionality
  console.log('Block user clicked')
}

const handleClearChat = () => {
  // Implement clear chat functionality
  console.log('Clear chat clicked')
}
</script>

<template>
  <div class="p-4 border-b flex items-center justify-between">
    <div class="flex items-center gap-3">
      <Avatar
        :label="getInitials(user?.username)"
        class="bg-primary"
        shape="circle"
      />
      <div>
        <h3 class="text-lg font-semibold">{{ user?.username }}</h3>
        <p class="text-sm text-gray-500">{{ user?.email }}</p>
      </div>
    </div>
    <div class="flex gap-2">
      <Button
        icon="pi pi-phone"
        text
        rounded
        aria-label="Call"
        @click="handleCall"
      />
      <Button
        icon="pi pi-video"
        text
        rounded
        aria-label="Video Call"
        @click="handleVideoCall"
      />
      <Button
        icon="pi pi-ellipsis-v"
        text
        rounded
        aria-label="More"
        @click="toggleMenu"
      />
      <Menu ref="menu" :model="menuItems" :popup="true" />
    </div>
  </div>
</template>
