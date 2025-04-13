<script setup lang="ts">
import { onMounted } from 'vue';

onMounted(() => {
  fetch('http://localhost:9090/api/hello')
    .then(res => res.text())
    .then(console.log)
    .catch(console.error);
});
</script>

<template>
  <RouterView />
</template>
