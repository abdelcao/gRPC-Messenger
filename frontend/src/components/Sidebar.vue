<template>
  <div class="w-16 bg-white dark:bg-gray-800 border-r border-gray-500 flex flex-col items-center py-4">
    <img src="/images/default_avatar.jpg" class="w-10 h-10 rounded-full mb-6" alt="User Avatar" />
    <div class="space-y-6 flex flex-col">
      <div class="p-4 bg-gray-200 dark:bg-gray-700 rounded-full flex items-center justify-center">
        <i class="pi pi-comments text-xl" />
      </div>
      <div class="p-4 bg-gray-200 dark:bg-gray-700 rounded-full flex items-center justify-center">
        <i class="pi pi-briefcase text-xl text-blue-600" />
      </div>
      <div class="p-4 bg-gray-200 dark:bg-gray-700 rounded-full flex items-center justify-center">
        <i class="pi pi-heart text-xl" />
      </div>
      <div class="p-4 bg-gray-200 dark:bg-gray-700 rounded-full flex items-center justify-center">
        <i class="pi pi-bookmark text-xl" />
      </div>
      <div class="p-4 bg-gray-200 dark:bg-gray-700 rounded-full flex items-center justify-center">
        <i class="pi pi-calendar text-xl" />
      </div>
      <div class="p-4 bg-gray-200 dark:bg-gray-700 rounded-full flex items-center justify-center">
        <i class="pi pi-folder text-xl" />
      </div>
    </div>
    <div class="mt-auto">
      <i class="pi pi-cog text-xl" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';


const sidebarLinks = ref([

])

</script>
