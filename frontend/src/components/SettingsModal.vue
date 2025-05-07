<script setup lang="ts">
<<<<<<< HEAD
import { useAppStore } from '@/stores/app';
import Dialog from 'primevue/dialog';

const appStore = useAppStore()

=======
import { useAppStore } from '@/stores/app'
import Dialog from 'primevue/dialog'
import { ref } from 'vue'
import InputText from 'primevue/inputtext'
import InputSwitch from 'primevue/inputswitch'
import Password from 'primevue/password'
import Button from 'primevue/button'

const appStore = useAppStore()

const settings = ref({
  notifications: true,
  username: 'yusef',
  password: ''
})

const logout = () => {
  console.log('Logging out...')
  // add logout logic
}

const disableAccount = () => {
  console.log('Disabling account...')
  // confirm + disable logic
}
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
</script>

<template>
  <Dialog
    v-model:visible="appStore.settingsModalVisible"
    modal
<<<<<<< HEAD
    header="Header"
    :style="{ width: '50vw' }"
    :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
  >
    <p class="m-0">
      Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut
      labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco
      laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in
      voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat
      non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
    </p>
=======
    header="Settings"
    :style="{ width: '50vw' }"
    :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
  >
    <div class="max-w-xl mx-auto space-y-6">
      <h2 class="text-2xl font-semibold text-gray-800 dark:text-white">Settings</h2>

      <!-- Notifications Switch -->
      <div class="flex items-center justify-between">
        <label class="text-sm text-gray-700 dark:text-gray-300">Enable Notifications</label>
        <InputSwitch v-model="settings.notifications" />
      </div>

      <!-- Username -->
      <div>
        <label class="block text-sm text-gray-700 dark:text-gray-300 mb-1">Username</label>
        <InputText v-model="settings.username" class="w-full" />
      </div>

      <!-- Password -->
      <div>
        <label class="block text-sm text-gray-700 dark:text-gray-300 mb-1">Change Password</label>
        <Password v-model="settings.password" toggleMask class="w-full" :feedback="false" />
      </div>

      <!-- Danger Zone -->
      <div class="border-t border-gray-200 dark:border-gray-700 pt-4 space-y-3">
        <Button
          label="Disable Account"
          icon="pi pi-user-minus"
          severity="danger"
          outlined
          class="w-full"
          @click="disableAccount"
        />
        <Button
          label="Logout"
          icon="pi pi-sign-out"
          severity="secondary"
          class="w-full"
          @click="logout"
        />
      </div>
    </div>
>>>>>>> 9799454b1651a2a9038d76d767e3aa35c6fb6cd0
  </Dialog>
</template>
