<script setup>
import { ref } from 'vue'
import Popover from 'primevue/popover'

const op = ref()
const members = ref([
  { name: 'Amy Elsner', image: 'amyelsner.png', email: 'amy@email.com', role: 'Owner' },
  {
    name: 'Bernardo Dominic',
    image: 'bernardodominic.png',
    email: 'bernardo@email.com',
    role: 'Editor',
  },
  {
    name: 'Ioni Bowcher',
    image: 'ionibowcher.png',
    email: 'ioni@email.com',
    role: 'Viewer',
  },
])

const toggle = (event) => {
  op.value.toggle(event)
}
</script>

<template>
  <div class="flex items-center justify-between px-4 py-3 border-b border-gray-500">
    <div class="flex items-center gap-2">
      <img src="/images/default_avatar.jpg" class="w-10 h-10 rounded-full" />
      <div class="flex-1">
        <div class="font-semibold text-sm">Selected Chat</div>
        <div class="text-xs text-gray-400">Online</div>
      </div>
    </div>

    <div>
      <i
        class="pi pi-ellipsis-v p-2 flex items-center justify-center hover:bg-gray-500 rounded-full"
        @click="toggle"
      ></i>

      <Popover ref="op">
        <div class="flex flex-col gap-4 rounded-tr-none">
          <div>
            <span class="font-medium block mb-2">Report</span>
          </div>
        </div>
      </Popover>
    </div>
  </div>
</template>
