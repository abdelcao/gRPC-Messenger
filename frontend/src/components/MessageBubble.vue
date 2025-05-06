<!-- src/components/MessageBubble.vue -->
<template>
  <div :class="['mb-2 flex', isOwn ? 'justify-end' : 'justify-start']">
    <div
      :class="[
        'rounded-lg px-2.5 py-2 text-sm max-w-[70%] font-semibold',
        isOwn ? 'bg-lime-500/25 text-white' : 'bg-gray-100 text-gray-900',
      ]"
    >
      <slot />
      <div
        class="flex items-center justify-end gap-3"
        :class="isOwn ? 'text-lime-500' : 'text-gray-500'"
      >
      <span v-if="time" class="text-xs font-semibold text-right">{{ time }}</span>
      <span v-if="seen" class="text-xs"> seen </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  isOwn?: boolean
  time?: string
  seen?: boolean
}>()
</script>
