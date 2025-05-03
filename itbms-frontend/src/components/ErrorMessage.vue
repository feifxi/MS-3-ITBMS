<template>
    <div class="bg-red-50 border border-red-100 rounded-lg p-8 text-center max-w-lg mx-auto my-10 shadow-md">
      <div class="mb-4 text-red-500">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>
      <h2 class="text-xl font-bold text-gray-800 mb-2">Item Not Found</h2>
      <p class="text-lg mb-6 text-gray-600">The requested sale item does not exist.</p>
      <button 
        class="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-md text-base font-medium cursor-pointer transition-colors duration-200"
        @click="$emit('goBack')"
      >
        OK
      </button>
    </div>
  </template>
  