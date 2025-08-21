<script setup>
import { RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue';
import ToastMessage from './components/ToastMessage.vue';
import { useAuthStore } from './stores/auth';
import { onMounted } from 'vue';

const authStore = useAuthStore();

onMounted(() => {
  authStore.fetchUser()
})

</script>

<template>
  <!-- Auth Loading -->
  <main v-if="authStore.isLoadingAuth" class="text-center text-blue-500 animate-pulse text-3xl font-bold mt-10">
    Loading...
  </main>
  <!-- Page -->
  <main v-else class="relative bg-gradient-to-br from-rose-50 to-purple-50">
    <Navbar />
    <RouterView />
    <ToastMessage />
  </main>
</template>

<style scoped></style>
