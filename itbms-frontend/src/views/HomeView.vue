<script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <div class="landing-page">
    <!-- Main content -->
    <div class="container mx-auto px-4 py-8 sm:py-12 md:py-16">
      <!-- Hero section -->
      <div class="border border-gray-200 rounded-xl shadow-lg overflow-hidden bg-gray-100">
        <div class="bg-gradient-to-r from-blue-500 to-purple-600 py-16 sm:py-24 md:py-32 px-4 sm:px-6 md:px-8">
          <div class="max-w-5xl mx-auto text-center">
            <h1 class="text-4xl sm:text-5xl md:text-6xl font-light text-white mb-4 sm:mb-6 tracking-wide">
              Landing Page
            </h1>
            <p class="text-base sm:text-lg md:text-xl text-blue-100 max-w-xl sm:max-w-2xl mx-auto mb-6 sm:mb-8 px-2">
              Discover amazing products and experiences designed just for you
            </p>
            
            <!-- Animated divider -->
            <div class="flex justify-center mb-6 sm:mb-10">
              <div class="w-16 sm:w-20 md:w-24 h-1 bg-white bg-opacity-50 rounded"></div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Shop button container with animation -->
      <div class="flex justify-center mt-8 sm:mt-10 md:mt-12">
        <RouterLink to="/sale-items" class="text-gray-700 hover:text-gray-300 font-medium text-lg transition duration-300">
          <button class="bg-gradient-to-r from-yellow-400 to-orange-500 text-white font-medium py-3 sm:py-4 px-8 sm:px-10 md:px-12 rounded-full text-base sm:text-lg shadow-lg transform transition duration-300 hover:scale-105 hover:shadow-xl flex items-center">
            <span class="Itbms-shopnow mr-2">SHOP NOW</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 sm:h-5 sm:w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10.293 5.293a1 1 0 011.414 0l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414-1.414L12.586 11H5a1 1 0 110-2h7.586l-2.293-2.293a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </RouterLink>
      </div>
      
      <!-- Features section -->
      <div class="mt-16 sm:mt-20 md:mt-24 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 md:gap-8">
        <div class="bg-white p-6 sm:p-8 rounded-lg shadow-md transform transition duration-300 hover:-translate-y-1 hover:shadow-lg">
          <div class="text-blue-500 text-3xl sm:text-4xl mb-3 sm:mb-4 flex justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 sm:h-12 sm:w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
            </svg>
          </div>
          <h3 class="text-lg sm:text-xl font-semibold text-center mb-2">Premium Products</h3>
          <p class="text-gray-600 text-center text-sm sm:text-base">Only the best items for our customers</p>
        </div>
        
        <div class="bg-white p-6 sm:p-8 rounded-lg shadow-md transform transition duration-300 hover:-translate-y-1 hover:shadow-lg">
          <div class="text-blue-500 text-3xl sm:text-4xl mb-3 sm:mb-4 flex justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 sm:h-12 sm:w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 class="text-lg sm:text-xl font-semibold text-center mb-2">Fast Delivery</h3>
          <p class="text-gray-600 text-center text-sm sm:text-base">Quick and reliable shipping to your doorstep</p>
        </div>
        
        <div class="bg-white p-6 sm:p-8 rounded-lg shadow-md transform transition duration-300 hover:-translate-y-1 hover:shadow-lg sm:col-span-2 md:col-span-1 mx-auto sm:mx-0 max-w-sm sm:max-w-full">
          <div class="text-blue-500 text-3xl sm:text-4xl mb-3 sm:mb-4 flex justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 sm:h-12 sm:w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 class="text-lg sm:text-xl font-semibold text-center mb-2">24/7 Support</h3>
          <p class="text-gray-600 text-center text-sm sm:text-base">We're always here to help you</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@media (max-width: 640px) {
  .landing-page {
    overflow-x: hidden;
  }
}
</style>