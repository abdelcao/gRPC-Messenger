<script setup lang="ts">
import { onMounted } from 'vue'
import Button from 'primevue/button';
import Toast from 'primevue/toast';
</script>

<template>
  <RouterView />
  <Toast position="bottom-center" />
</template>