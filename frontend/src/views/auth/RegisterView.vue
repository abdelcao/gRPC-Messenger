<template>
  <AuthLayout title="Create Account" subtitle="Get started for free">
    <form @submit.prevent="handleRegister" class="space-y-6">
      <div>
        <label for="name" class="block text-sm font-medium text-gray-700 dark:text-gray-300"
          >Username</label
        >
        <InputText
          id="name"
          type="text"
          v-model="username"
          class="mt-1 block w-full"
          required
          placeholder="JohnDoe"
        />
      </div>
      <div>
        <label for="email-reg" class="block text-sm font-medium text-gray-700 dark:text-gray-300"
          >Email address</label
        >
        <InputText
          id="email-reg"
          type="email"
          v-model="email"
          class="mt-1 block w-full"
          required
          placeholder="you@example.com"
        />
      </div>

      <div>
        <label for="password-reg" class="block text-sm font-medium text-gray-700 dark:text-gray-300"
          >Password</label
        >
        <Password
          id="password-reg"
          v-model="password"
          class="mt-1 block w-full"
          required
          toggle-mask
          placeholder="Create a Password"
          :feedback="true"
        />
      </div>
      <div>
        <label
          for="password-confirm"
          class="block text-sm font-medium text-gray-700 dark:text-gray-300"
          >Confirm Password</label
        >
        <Password
          id="password-confirm"
          v-model="passwordConfirm"
          class="mt-1 block w-full"
          required
          placeholder="Confirm Password"
          :feedback="false"
        />
      </div>

      <Button
        type="submit"
        label="Sign Up"
        class="w-full !bg-indigo-600 hover:!bg-indigo-700 text-white"
        :loading="isLoading"
      />
    </form>

    <template #footer>
      <router-link
        to="/login"
        class="font-medium text-indigo-600 hover:text-indigo-500 dark:text-indigo-400 dark:hover:text-indigo-300"
      >
        Already have an account? Sign In
      </router-link>
    </template>
  </AuthLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthLayout from '@/layouts/AuthLayout.vue' // Adjust path

// Import PrimeVue components
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import { useAuthService } from '@/composables/useAuthService'
import { useToast } from 'primevue/usetoast'

const router = useRouter()

const username = ref('yusef')
const email = ref('email@mail.com')
const password = ref('password')
const passwordConfirm = ref('password')
const isLoading = ref(false)

const toast = useToast()

const handleRegister = async () => {
  if (password.value !== passwordConfirm.value) {
    toast.add({
      severity: 'warn',
      summary: 'Password Mismatch',
      detail: 'Passwords do not match',
      life: 2000,
    })
    return
  }
  isLoading.value = true
  console.log('Registering:', username.value, email.value)

  // --- Your Registration Logic Here ---
  // Call API, handle success/errors
  const auth = useAuthService()
  try {
    const res = await auth.register({
      email: email.value,
      password: password.value,
      username: username.value,
    })

    console.log('register success:', res)

    toast.add({
      severity: 'success',
      summary: 'Register',
      detail: 'Your account has been created!',
      life: 2000,
    })

    // save token to localStorage
    

    await router.push({ name: 'dashboard' })
  } catch (error: any) {
    toast.add({
      severity: 'error',
      summary: 'hhh',
      detail: error?.response?.data?.message,
      life: 2000,
    })

    console.log(error)
  } finally {
    // ---
    isLoading.value = false
  }
}
</script>
<style>
/* Ensure password inputs fill width */
.p-password input {
  width: 100%;
}
</style>
