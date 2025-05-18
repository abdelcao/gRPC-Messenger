<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h2 class="text-lg font-semibold text-gray-800 dark:text-white">Mentions</h2>
      <span class="text-lime-500/50 hover:text-lime-500 cursor-pointer">Mark all as read</span>
    </div>

    <ul class="space-y-2">
      <RouterLink v-for="notif in notifications" :key="notif.id" class="block" :to="notif.link">
        <li
          class="flex items-start p-4 rounded-xl shadow space-x-4 hover:bg-gray-50 dark:hover:bg-gray-700 transition"
          :class="notif.unread ? 'bg-lime-500/50 dark:bg-lime-500/25':'bg-white dark:bg-gray-700'"
        >
          <div class="flex-1">
            <span class="font-medium text-gray-900 dark:text-white">@{{ notif.from }}</span>
            <p class="text-sm text-gray-700 dark:text-gray-300">
              {{ notif.preview }}
            </p>
            <div class="text-xs text-gray-400 mt-1">{{ timeAgo(notif.createdAt) }}</div>
          </div>

        </li>
      </RouterLink>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { timeAgo } from '@/libs/utils'
import { RouterLink } from 'vue-router'

const notifications = [
  {
    id: 1,
    username: 'Admin',
    preview: "You're account has been suspended for violating our policies",
    link: '#',
    createdAt: '2025-02-15T15:35:14.145Z',
    unread: true,
  },
  {
    id: 2,
    username: 'Invitation',
    preview: '@john Invited you to @tradin group chat',
    link: '#',
    createdAt: '2025-05-15T15:35:14.145Z',
    unread: false,
  },
]
</script>
