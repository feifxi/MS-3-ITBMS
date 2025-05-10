<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue';
import ToastMessage from './components/ToastMessage.vue';

</script>

<template>
  <main class="relative">
    <Navbar />
    <RouterView />
    <ToastMessage />
  </main>
</template>

<style scoped></style>
