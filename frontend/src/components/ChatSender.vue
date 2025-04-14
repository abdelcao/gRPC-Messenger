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
  <div class="border-t border-gray-500 p-4 flex gap-2 items-center">
    <InputText v-model="message" placeholder="Your message" class="flex-1" />
    <Button icon="pi pi-send" @click="sendMessage" />
  </div>
</template>
