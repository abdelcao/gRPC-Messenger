<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import type { User } from '../grpc/user/user_pb';

defineProps<{
  user: User
}>()

const appStore = useAppStore()


</script>

<template>
  <div class="flex items-center justify-between px-4 py-3 border-b border-gray-500">
    <div class="flex items-center gap-3">
      <Avatar
        image="/images/default_avatar.jpg"
        class="bg-primary"
        shape="circle"
      />
      <div>
        <h3 class="text-lg font-semibold">{{ user.username }}</h3>
      </div>
    </div>

    <div>
      <i
        @click="() => (appStore.panelInforOpened = !appStore.panelInforOpened)"
        style="font-size: 1.25rem"
        class="pi pi-info-circle cursor-pointer p-2 flex items-center justify-center hover:bg-gray-500 rounded-full"
      ></i>
    </div>
  </div>
</template>
