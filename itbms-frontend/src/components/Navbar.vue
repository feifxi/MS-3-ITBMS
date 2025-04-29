<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header class="bg-black text-white flex gap-3">
    <h2 class="">ITBMS MS-3</h2>

    <div class="flex gap-3">
      <nav>
        <RouterLink to="/">Home</RouterLink>
      </nav>
      <nav>
        <RouterLink to="/">Muhaha</RouterLink>
      </nav>
    </div>
  </header>
</template>

<style scoped>
</style>
