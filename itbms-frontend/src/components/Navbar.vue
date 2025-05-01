<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header class="bg-black text-white flex justify-between items-center px-6 py-4">
    <div class="flex items-center gap-8">
      <RouterLink to="/" class="text-2xl font-bold">ITBMS</RouterLink>
    </div>

    <div class="flex items-center gap-4">
      <button class="relative hover:text-gray-300">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
          <line x1="3" y1="6" x2="21" y2="6"></line>
          <path d="M16 10a4 4 0 0 1-8 0"></path>
        </svg>
      </button>
      
      <div class="flex items-center gap-2 cursor-pointer hover:text-gray-300">
        <div class="w-8 h-8 bg-gray-400 rounded-full overflow-hidden">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
        </div>
        <span>Profile</span>
      </div>
    </div>
  </header>
</template>

<style scoped>
/* เพิ่ม CSS เพิ่มเติมได้ตามต้องการ */
nav a.router-link-active {
  font-weight: bold;
  border-bottom: 2px solid white;
}
</style>