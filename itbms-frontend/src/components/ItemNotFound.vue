<script setup lang="ts">
import { CircleAlert } from 'lucide-vue-next';

</script>

<template>
  <div class="bg-red-50 border border-red-100 rounded-lg p-8 text-center max-w-lg mx-auto my-10 shadow-md">
    <div class="mb-4 text-red-500 flex justify-center">
      <CircleAlert class="size-20" />
    </div>
    <h2 class="text-xl font-bold text-gray-800 mb-2">Item Not Found</h2>
    <p class="itbms-message text-lg mb-6 text-gray-600">The requested sale item does not exist.</p>
    <button
      class="itbms-button bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-md text-base font-medium cursor-pointer transition-colors duration-200"
      @click="$emit('goBack')">
      OK
    </button>
  </div>
</template>