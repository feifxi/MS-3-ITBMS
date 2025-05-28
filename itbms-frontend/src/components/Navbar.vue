<script setup>
import { Menu } from 'lucide-vue-next';
import { RouterLink } from 'vue-router'
</script>

<template>
  <header class="sticky top-0 z-50 bg-gradient-to-r from-purple-500 to-rose-500 text-white">
    <nav class="px-8 sm:px-16">
      <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <RouterLink to="/">
          <div class="flex items-center gap-2">
            <div class="text-white">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-smartphone"><rect width="14" height="20" x="5" y="2" rx="2" ry="2"/><path d="M12 18h.01"/></svg>
            </div>
            <h1 class="font-bold text-xl">ITBMS-MS3</h1>
          </div>
        </RouterLink>

        <div class="max-sm:hidden flex items-center gap-4">
          <div class="relative">
            <input 
              type="text" 
              placeholder="Search phones..." 
              class="input !bg-white !rounded-full text-black"
            >
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-search absolute right-3 top-2 text-gray-400"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
          </div>

          <button class="relative cursor-pointer">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-shopping-cart"><circle cx="8" cy="21" r="1"/><circle cx="19" cy="21" r="1"/><path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"/></svg>
            <span class="absolute -top-2 -right-2 bg-rose-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">3</span>
          </button>

          <button class="cursor-pointer w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center text-black">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-user"><circle cx="12" cy="8" r="5"/><path d="M20 21a8 8 0 0 0-16 0"/></svg>
          </button>
        </div>
        <div class="sm:hidden">
          <Menu />
        </div>
      </div>
    </nav>
  </header>
</template>

<style scoped>
</style>