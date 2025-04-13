<script setup lang="ts">
import { onMounted } from 'vue';
import { RouterView } from 'vue-router'

onMounted(() => {
  fetch('http://localhost:8000/api/hello')
    .then(res => res.text())
    .then(console.log)
    .catch(console.error);
});
</script>

<template>
  <RouterView />
</template>
