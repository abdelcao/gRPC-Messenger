<script setup lang="ts">
import { ref } from 'vue'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'

const emit = defineEmits<{
  (e: 'send', message: string): void
}>()

const message = ref('')
const loading = ref(false)

const handleSubmit = async () => {
  if (!message.value.trim()) return

  loading.value = true
  try {
    emit('send', message.value.trim())
    message.value = ''
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="h-full w-full">
    <form @submit.prevent="handleSubmit" class="flex">
      <input
        v-model="message"
        placeholder="Type a message..."
        class="w-full ps-8 bg-gray-900 rounded-none h-16"
        :disabled="loading"
      />
      <button
        type="submit"
        class="w-16 shrink-0 bg-lime-700 flex items-center justify-center"
        :loading="loading"
        :disabled="!message.trim()"
      ><i class="pi pi-send" style="font-size: 1.25rem;"></i></button>
    </form>
  </div>
</template>
