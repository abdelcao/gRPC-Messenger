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
  <div class="p-4 border-t">
    <form @submit.prevent="handleSubmit" class="flex gap-2">
      <InputText
        v-model="message"
        placeholder="Type a message..."
        class="flex-1"
        :disabled="loading"
      />
      <Button
        type="submit"
        icon="pi pi-send"
        :loading="loading"
        :disabled="!message.trim()"
      />
    </form>
  </div>
</template>
