<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-4xl font-bold">Users</h2>
    </div>
    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <StatCard
        v-for="(st, n) in stats"
        :key="n"
        :label="st.label"
        :value="st.value"
        :icon="st.icon"
        :iconColor="st.iconColor"
      />
    </div>

    <!-- Users filter -->
    <div class="flex justify-start gap-2">
        <InputText placeholder="Search users..." class="w-72 pe-4" />
        <Button icon="pi pi-search" ></Button>
    </div>

    <!-- Users Table -->
    <DataTable class="rounded-lg" :value="users" :paginator="true" :rows="10" responsiveLayout="scroll">
      <Column field="id" header="ID" />
      <Column field="username" header="Username" />
      <Column field="email" header="Email" />
      <Column field="isEmailVerified" header="Email Verified">
        <template #body="{ data }">
          <Tag
            :value="data.isEmailVerified ? 'Yes' : 'No'"
            :severity="data.isEmailVerified ? 'success' : 'warning'"
          />
        </template>
      </Column>
      <Column field="isSuspended" header="Suspended">
        <template #body="{ data }">
          <Tag
            :value="data.isSuspended ? 'Yes' : 'No'"
            :severity="data.isSuspended ? 'danger' : 'success'"
          />
        </template>
      </Column>
      <Column field="isActivated" header="Activated">
        <template #body="{ data }">
          <Tag
            :value="data.isActivated ? 'Yes' : 'No'"
            :severity="data.isActivated ? 'success' : 'secondary'"
          />
        </template>
      </Column>
      <Column header="Actions">
        <template #body="{ data }">
          <Button icon="pi pi-ellipsis-v" @click="showMenu($event, data)" class="p-button-text" />
        </template>
      </Column>
    </DataTable>

    <!-- Shared Menu -->
    <Menu ref="menu" :model="menuItems" popup />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'
import Menu from 'primevue/menu'
import Button from 'primevue/button'
import StatCard from '@/components/StatCard.vue'
import InputText from 'primevue/inputtext'

interface User {
  id: number
  username: string
  email: string
  isEmailVerified: boolean
  isSuspended: boolean
  isActivated: boolean
}

const stats = [
  {
    label: 'Total Users',
    value: 10000,
    icon: 'pi-users',
    iconColor: 'text-blue-500',
  },
  {
    label: 'Active',
    value: 9945,
    icon: 'pi-check-circle',
    iconColor: 'text-lime-500',
  },
  {
    label: 'Suspended',
    value: 40,
    icon: 'pi-ban',
    iconColor: 'text-red-500',
  },
  {
    label: 'Unverified email',
    value: 5,
    icon: 'pi-envelope',
    iconColor: 'text-amber-500',
  },
]

const users = ref<User[]>([
  {
    id: 1,
    username: 'yusef',
    email: 'yusef@example.com',
    isEmailVerified: true,
    isSuspended: false,
    isActivated: true,
  },
  {
    id: 2,
    username: 'john',
    email: 'john@example.com',
    isEmailVerified: false,
    isSuspended: true,
    isActivated: false,
  },
  // ...more
])

const menu = ref()
const selectedUser = ref<User | null>(null)

const showMenu = (event: Event, user: User) => {
  selectedUser.value = user
  menu.value.show(event)
}

const menuItems = ref([
  {
    label: 'Edit',
    icon: 'pi pi-pencil',
    command: () => console.log('Edit', selectedUser.value),
  },
  {
    label: 'Delete',
    icon: 'pi pi-trash',
    command: () => console.log('Delete', selectedUser.value),
  },
  {
    label: 'Toggle Suspend',
    icon: 'pi pi-ban',
    command: () => console.log('Toggle Suspend', selectedUser.value),
  },
  {
    label: 'Toggle Activate',
    icon: 'pi pi-power-off',
    command: () => console.log('Toggle Activate', selectedUser.value),
  },
])
</script>

<style scoped>
/* Optional custom styling */
</style>
