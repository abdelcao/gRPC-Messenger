<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import {ref} from "vue";

const message = ref('')

function sendMessage() {
  if (message.value.trim()) {
    // Send logic here...
    message.value = ''
  }
}
</script>

<template>
  <div class="border-t border-gray-500 flex items-center">
    <input v-model="message" placeholder="Your message" class="bg-gray-900 ps-4 focus-within:outline-none flex-1 h-16 rounded-none" />
    <button class="flex items-center justify-center bg-lime-500/25 h-full w-16 cursor-pointer hover:bg-lime-500 hover:text-lime-900" @click="sendMessage">
      <i class="pi pi-arrow-right" style="font-size: 1.5rem;"></i>
    </button>
  </div>
</template>
