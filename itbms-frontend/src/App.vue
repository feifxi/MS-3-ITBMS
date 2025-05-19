<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue';
import ToastMessage from './components/ToastMessage.vue';
import MockUIView from './views/MockUIView.vue';

</script>

<template>
  <main class="relative">
    <Navbar />
    <RouterView />
    <ToastMessage />
    <MockUIView />
  </main>
</template>

<style scoped></style>
