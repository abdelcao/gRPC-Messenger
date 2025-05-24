<script setup lang="ts">
import { ref } from 'vue'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'


const message = ref('')
const loading = ref(false)

const handleSubmit = async () => {
  if (!message.value.trim()) return
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
        class="w-16 shrink-0 bg-lime-700 flex items-center justify-center cursor-pointer"
        :loading="loading"
        :disabled="!message.trim()"
      ><i class="pi pi-send" style="font-size: 1.25rem;"></i></button>
    </form>
  </div>
</template>
