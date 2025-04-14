<!-- src/components/ChatInfoPanel.vue -->
<template>
  <div class="w-72 bg-white dark:bg-gray-800 border-l border-gray-200 p-4 overflow-y-auto">
    <div class="flex items-center gap-2 mb-2">
      <img src="/images/default_avatar.jpg" class="w-10 h-10 rounded-full" />
      <div>
        <div class="font-semibold">TechPulse Company</div>
        <div class="text-xs text-gray-500">32 members</div>
      </div>
    </div>

    <p class="text-xs text-gray-600 mb-2">
      Company Team Chat: Working together, sharing ideas and communicating effectively!
    </p>

    <a href="#" class="text-blue-500 text-sm">@techpulsecom</a>

    <!-- Members -->
    <h3 class="font-semibold text-sm mt-6 mb-2">Members</h3>
    <div class="flex -space-x-2">
      <img v-for="n in 5" :key="n" src="/images/default_avatar.jpg" class="w-8 h-8 rounded-full border-2 border-white" />
      <div class="w-8 h-8 rounded-full bg-gray-200 text-xs flex items-center justify-center border-2 border-white">+27</div>
    </div>

    <!-- Photos -->
    <h3 class="font-semibold text-sm mt-6 mb-2">Photos and Videos</h3>
    <div class="grid grid-cols-3 gap-2">
      <img v-for="n in 5" :key="n" src="/images/default_avatar.jpg" class="w-full h-16 object-cover rounded" />
      <div class="w-full h-16 bg-gray-200 flex items-center justify-center rounded text-xs">+178</div>
    </div>

    <!-- Files -->
    <h3 class="font-semibold text-sm mt-6 mb-2">Shared Files</h3>
    <ul class="text-xs text-gray-600 space-y-1">
      <li><i class="pi pi-file mr-2 text-blue-500"></i> terms_of_reference.docx</li>
      <li><i class="pi pi-file-excel mr-2 text-green-500"></i> contracting_agreement.xls</li>
      <li><i class="pi pi-file mr-2 text-orange-500"></i> clientlogo.svg</li>
    </ul>

    <!-- Links -->
    <h3 class="font-semibold text-sm mt-6 mb-2">Shared Links</h3>
    <ul class="text-xs text-blue-500 space-y-1">
      <li><a href="#">Google Meet</a></li>
      <li><a href="#">Behance</a></li>
    </ul>
  </div>
</template>

<script setup lang="ts">
</script>
