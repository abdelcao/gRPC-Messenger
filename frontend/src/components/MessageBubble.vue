<!-- src/components/MessageBubble.vue -->
<template>
  <div :class="['mb-2 flex', isOwn ? 'justify-end' : 'justify-start']">
    <div
      :class="[
        'rounded-lg px-4 py-2 text-sm max-w-[70%]',
        isOwn ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-800'
      ]"
    >
      <slot />
      <div class="text-[10px] text-right mt-1" v-if="time">{{ time }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  isOwn?: boolean
  time?: string
}>()
</script>
