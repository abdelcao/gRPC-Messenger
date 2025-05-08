<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h2 class="text-lg font-semibold text-gray-800 dark:text-white">Mentions</h2>
      <span class="text-lime-500/50 hover:text-lime-500 cursor-pointer">Mark all as read</span>
    </div>

    <ul class="space-y-3">
      <li
        v-for="notif in notifications"
        :key="notif.id"
        class="flex items-start p-4 bg-white dark:bg-gray-800 rounded-xl shadow space-x-4 hover:bg-gray-50 dark:hover:bg-gray-700 transition"
      >
        <img :src="notif.avatar" class="w-10 h-10 rounded-full object-cover" alt="avatar" />

        <div class="flex-1">
          <p class="text-sm text-gray-700 dark:text-gray-300">
            <span class="font-medium text-gray-900 dark:text-white">@{{ notif.mention }}</span>
            &nbsp;mentioned you in
            <span class="font-semibold text-blue-500">#{{ notif.group }}</span
            >: "{{ notif.preview }}"
          </p>
          <div class="text-xs text-gray-400 mt-1">{{ notif.timeAgo }}</div>
        </div>

        <span v-if="notif.unread" class="w-2 h-2 rounded-full bg-blue-500 mt-1" />
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
const notifications = [
  {
    id: 1,
    avatar: '/images/default_avatar.jpg',
    mention: 'alice',
    group: 'dev-team',
    preview: 'Can you check this logic @you?',
    timeAgo: '2 minutes ago',
    unread: true,
  },
  {
    id: 2,
    avatar: '/images/default_avatar.jpg',
    mention: 'john',
    group: 'marketing',
    preview: 'Hey @you, what do you think?',
    timeAgo: '1 hour ago',
    unread: false,
  },
]
</script>
