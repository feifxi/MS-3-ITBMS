<script setup>
  import { ref } from 'vue';
  import { RouterLink } from 'vue-router'
  import mockPhone from '@/assets/image/mockPhone.webp'
  import mockPhone2 from '@/assets/image/mock2.png' 

  
  const categories = ref([
    { id: 1, name: 'iPhone', color: 'rose' },
    { id: 2, name: 'Samsung', color: 'purple' },
    { id: 3, name: 'Google', color: 'green' },
    { id: 4, name: 'Xiaomi', color: 'orange' },
    { id: 5, name: 'OnePlus', color: 'red' },
    { id: 6, name: 'Oppo', color: 'blue' },
  ]);

  const products = ref([
    {
      id: 1,
      name: 'iPhone 15 Pro Max',
      price: 1099,
      oldPrice: 1199,
      rating: 5,
      reviews: 128,
      image: mockPhone
    },
    {
      id: 2,
      name: 'Samsung Galaxy S23 Ultra',
      price: 999,
      oldPrice: null,
      rating: 4,
      reviews: 95,
      image: mockPhone
    },
    {
      id: 3,
      name: 'Google Pixel 8 Pro',
      price: 899,
      oldPrice: 999,
      rating: 4,
      reviews: 76,
      image: mockPhone2
    },
    {
      id: 4,
      name: 'OnePlus 11',
      price: 699,
      oldPrice: 799,
      rating: 4,
      reviews: 64,
      image: mockPhone
    },
    {
      id: 5,
      name: 'Xiaomi 13 Pro',
      price: 799,
      oldPrice: null,
      rating: 4,
      reviews: 42,
      image: mockPhone2
    },
    {
      id: 6,
      name: 'Nothing Phone (2)',
      price: 599,
      oldPrice: 649,
      rating: 4,
      reviews: 38,
      image: mockPhone
    },
    {
      id: 7,
      name: 'Motorola Edge 40 Pro',
      price: 649,
      oldPrice: null,
      rating: 3,
      reviews: 29,
      image: mockPhone2
    },
    {
      id: 8,
      name: 'Oppo Find X6 Pro',
      price: 899,
      oldPrice: 999,
      rating: 4,
      reviews: 31,
      image: mockPhone2
    }
  ]);
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-rose-50 to-purple-50">
    <!-- Hero Section -->
    <div class="container mx-auto px-4 py-8">
      <div class="relative overflow-hidden rounded-3xl bg-gradient-to-r from-rose-500 to-purple-600 h-80 md:h-96">
        <div class="absolute inset-0 bg-[url('/placeholder.svg?height=600&width=800')] bg-cover opacity-10"></div>
        <div class="relative z-10 h-full flex flex-col md:flex-row items-center justify-between p-8">
          <div class="text-white max-w-md">
            <h2 class="text-3xl md:text-4xl font-bold mb-4">iPhone 15 Pro</h2>
            <p class="mb-6 text-white/80">Experience the future with our most powerful iPhone yet. Titanium design, A17 Pro chip, and revolutionary camera system.</p>
            <div class="flex gap-4">
              <RouterLink to="/sale-items" class="itbms-shopnow">
                <button class="cursor-pointer bg-white text-rose-600 px-6 py-2 rounded-full font-medium hover:bg-opacity-90 transition-all transform hover:scale-105">
                  Buy Now
                </button>
              </RouterLink>
              <RouterLink to="/sale-items/list" class="itbms-shopnow">
                <button class="cursor-pointer bg-white/20 backdrop-blur-sm text-white border border-white/30 px-6 py-2 rounded-full font-medium hover:bg-white/30 transition-all">
                  Management
                </button>
              </RouterLink>
              
            </div>
          </div>
          <div class="mt-6 md:mt-0 transform rotate-12 drop-shadow-2xl">
            <img src="/placeholder.svg?height=400&width=200" alt="iPhone 15 Pro" class="h-48 md:h-64 object-contain" />
          </div>
        </div>
      </div>
    </div>

    <!-- Categories -->
    <div class="container mx-auto px-4 py-8">
      <h2 class="text-xl font-bold mb-4">Browse Categories</h2>
      <div class="flex gap-4 overflow-x-auto pb-4 scrollbar-hide">
        <div v-for="category in categories" :key="category.id" 
          class="flex-shrink-0 flex flex-col items-center justify-center p-4 rounded-xl bg-white shadow-sm border border-gray-100 w-24 h-24 hover:shadow-md transition-all cursor-pointer">
          <div :class="`text-${category.color}-500`">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-smartphone"><rect width="14" height="20" x="5" y="2" rx="2" ry="2"/><path d="M12 18h.01"/></svg>
          </div>
          <span class="mt-2 text-sm font-medium">{{ category.name }}</span>
        </div>
      </div>
    </div>

    <!-- Featured Products -->
    <div class="container mx-auto px-4 py-8">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-xl font-bold">Featured Phones</h2>
        <div class="flex gap-2">
          <button class="px-3 py-1 text-sm rounded-full bg-rose-100 text-rose-600 font-medium">All</button>
          <button class="px-3 py-1 text-sm rounded-full bg-gray-100 text-gray-600 font-medium">New</button>
          <button class="px-3 py-1 text-sm rounded-full bg-gray-100 text-gray-600 font-medium">Popular</button>
        </div>
      </div>
      
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <div v-for="product in products" :key="product.id" 
          class="bg-white rounded-xl overflow-hidden border border-gray-100 hover:shadow-lg transition-all duration-300 transform hover:-translate-y-1">
          <div class="relative">
            <img :src="product.image" :alt="product.name" class="w-full h-48 object-contain p-4" />
            <button class="absolute top-2 right-2 w-8 h-8 rounded-full bg-white/80 backdrop-blur-sm flex items-center justify-center text-rose-500 hover:bg-rose-500 hover:text-white transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-heart"><path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/></svg>
            </button>
          </div>
          <div class="p-4">
            <div class="flex items-center gap-1 mb-1">
              <div class="flex">
                <svg v-for="i in 5" :key="i" xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="i <= product.rating ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-star text-amber-400"><path d="m12 2-2.2 6.6H3l5.5 4-2.1 6.6 5.6-4 5.6 4-2.1-6.6 5.5-4h-6.8Z"/></svg>
              </div>
              <span class="text-xs text-gray-500">{{ 128 }} reviews</span>
            </div>
            <h3 class="font-medium text-gray-900">{{ product.name }}</h3>
            <div class="flex items-center justify-between mt-2">
              <div>
                <span class="text-lg font-bold text-rose-600">${{ product.price }}</span>
                <span v-if="product.oldPrice" class="text-sm text-gray-400 line-through ml-2">${{ product.oldPrice }}</span>
              </div>
              <button class="w-8 h-8 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center hover:bg-rose-600 hover:text-white transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-plus"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- New Arrivals -->
    <div class="container mx-auto px-4 py-8">
      <h2 class="text-xl font-bold mb-6">New Arrivals</h2>
      <div class="relative overflow-hidden rounded-2xl bg-gradient-to-r from-purple-500 to-indigo-600 p-6">
        <div class="absolute inset-0 bg-[url('/placeholder.svg?height=600&width=800')] bg-cover opacity-10"></div>
        <div class="relative z-10 flex flex-col md:flex-row items-center justify-between">
          <div class="text-white max-w-md">
            <h3 class="text-2xl font-bold mb-2">Samsung Galaxy Z Fold 5</h3>
            <p class="mb-4 text-white/80">Experience the future of foldable technology with enhanced durability and performance.</p>
            <button class="bg-white text-purple-600 px-6 py-2 rounded-full font-medium hover:bg-opacity-90 transition-all">
              Pre-order Now
            </button>
          </div>
          <img :src="mockPhone2" alt="Samsung Galaxy Z Fold" class="mt-6 md:mt-0 h-40 md:h-48 object-contain" />
        </div>
      </div>
    </div>

    <!-- Footer -->
    <footer class="bg-white border-t border-gray-100 py-8 mt-8">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row justify-between gap-8">
          <div class="max-w-xs">
            <div class="flex items-center gap-2 mb-4">
              <div class="text-rose-500">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-smartphone"><rect width="14" height="20" x="5" y="2" rx="2" ry="2"/><path d="M12 18h.01"/></svg>
              </div>
              <h1 class="font-bold text-xl">ITBMS-MS3</h1>
            </div>
            <p class="text-gray-500 text-sm">Your one-stop destination for the latest smartphones and accessories with unbeatable prices and premium service.</p>
          </div>
          <div class="grid grid-cols-2 md:grid-cols-3 gap-8">
            <div>
              <h3 class="font-medium mb-3">Shop</h3>
              <ul class="space-y-2 text-sm text-gray-500">
                <li>Smartphones</li>
                <li>Accessories</li>
                <li>Wearables</li>
                <li>Tablets</li>
              </ul>
            </div>
            <div>
              <h3 class="font-medium mb-3">Support</h3>
              <ul class="space-y-2 text-sm text-gray-500">
                <li>Contact Us</li>
                <li>FAQs</li>
                <li>Shipping</li>
                <li>Returns</li>
              </ul>
            </div>
            <div>
              <h3 class="font-medium mb-3">Company</h3>
              <ul class="space-y-2 text-sm text-gray-500">
                <li>About Us</li>
                <li>Careers</li>
                <li>Blog</li>
                <li>Press</li>
              </ul>
            </div>
          </div>
        </div>
        <div class="border-t border-gray-100 mt-8 pt-8 flex flex-col md:flex-row justify-between items-center">
          <p class="text-sm text-gray-500">© 2025 ITBMS-MS3. All rights reserved.</p>
          <div class="flex gap-4 mt-4 md:mt-0">
            <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-facebook"><path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"/></svg>
            </div>
            <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-twitter"><path d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z"/></svg>
            </div>
            <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-instagram"><rect width="20" height="20" x="2" y="2" rx="5" ry="5"/><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"/><line x1="17.5" x2="17.51" y1="6.5" y2="6.5"/></svg>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>