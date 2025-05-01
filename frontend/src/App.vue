<script setup lang="ts">
import { onMounted } from 'vue';

onMounted(() => {
  fetch('http://localhost:8888/')
    .then(res => res.text())
    .then(console.log)
    .catch(console.error);
});
</script>

<template>
  <RouterView />
</template>
