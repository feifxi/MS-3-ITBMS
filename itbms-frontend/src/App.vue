<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue';
import ToastMessage from './components/ToastMessage.vue';
</script>

<template>
  <main class="relative bg-gradient-to-br from-rose-50 to-purple-50">
    <Navbar />
    <RouterView />
    <ToastMessage />
  </main>
</template>

<style scoped></style>
