<script setup lang="ts">
import { onMounted } from 'vue'
import Button from 'primevue/button';

const check = () => {
  fetch('http://localhost:8091/', {
    headers: {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
      "Content-Type": "application/json",
    }
  })
    .then((res) => console.log(res))
    .catch(err => console.error(err))
}
</script>

<template>
  <div>
    <h1>App.vue</h1>
    <h2>Check</h2>
    <Button @click="check">Check</Button>
    <p>Check the console for the response.</p>
  </div>
  <RouterView />
</template>
