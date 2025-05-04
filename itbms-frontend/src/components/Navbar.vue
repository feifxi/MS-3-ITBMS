<script setup>
import { ShoppingCart, User } from 'lucide-vue-next';
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header class="bg-black text-white flex justify-between items-center px-16 py-4">
    <div class="flex items-center gap-8">
      <RouterLink to="/" class="text-2xl font-bold">ITBMS</RouterLink>
    </div>

    <nav class="flex items-center gap-6">
      <button class="relative hover:text-gray-300">
        <ShoppingCart />
      </button>
      
      <button class="size-8 bg-gray-400 rounded-full overflow-hidden flex items-center justify-center">
        <User />
      </button>
    </nav>
  </header>
</template>

<style scoped>
</style>