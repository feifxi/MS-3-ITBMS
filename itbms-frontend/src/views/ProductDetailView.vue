<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchSaleItemById } from '../api/index' //
import ErrorMessage from "../components/ErrorMessage.vue";

const route = useRoute()
const router = useRouter()
const item = ref(null)
const fetchData = ref(true)

const fetchItem = async () => {
  try {
    const id = route.params.id
    const data = await fetchSaleItemById(id)

    if (data && data.price !== undefined) {
      item.value = data
    } else {
      item.value = null
    }
  } catch (err) {
    item.value = null
  } finally {
    fetchData.value = false
  }
}


const goBack = () => {
  router.push('/sale-items')
}

const formatNumber = (num) => {
  return new Intl.NumberFormat().format(num)
}

onMounted(() => {
  fetchItem()
})
</script>


<template>
 <button 
    @click="goBack"
    class=" hover:bg-black hover:text-white  text-black px-6 py-2 rounded-md text-sm font-medium cursor-pointer transition-colors duration-200 flex items-center gap-2 mb-4"
  >
    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
    </svg>

  </button>

    <div class="max-w-6xl mx-auto px-4 font-sans text-gray-800">
      <!-- Main Content -->

      <div class="py-8">

        <div v-if="item" class="flex flex-wrap gap-12 bg-white rounded-lg shadow-lg p-6">
          <!-- Product Images -->
          <div class="flex-1 min-w-[300px]">
            <div class="mb-6 text-center overflow-hidden rounded-lg shadow-md">
              <img :src="item.image || 'https://via.placeholder.com/500x500'" alt="product" class="w-full h-auto hover:scale-105 transition-transform duration-300" />
            </div>
          </div>
          
          <!-- Product Info -->
          <div class="flex-1 min-w-[300px]">
            <div class="mb-6 pb-4 border-b border-gray-200">
              <h2 class="Itbms-model text-2xl font-bold text-gray-900 mb-2">{{item.model }}</h2>
              <div class="flex items-center gap-2">
                <span class="Itbms-brand bg-blue-100 text-blue-800 font-semibold px-2 py-1 rounded-md text-sm">{{item.brand || 'No Brand'}}</span>
              </div>
            </div>
            
            <div class="mb-8">
              <div class="flex items-center gap-2 mb-4">
                <span class="Itbms-price text-3xl font-bold text-gray-900">{{formatNumber(item.price || 0)}}</span>
                <span class="Itbms-price-unit text-gray-600">Baht</span>
              </div>
              

              
              <div class="flex items-center gap-3 mb-6">
                <span class="inline-flex items-center gap-1">
                  <span class="w-3 h-3 rounded-full bg-green-500"></span>
                  <span class="text-green-600 font-medium">In Stock</span>
                </span>
                <span class="text-gray-500">|</span>
                <div class="text-gray-600">
                    <span class="Itbms-quantity	">{{item.quantity ||0}} units available</span>
                    <span class="Itbms-quantity-unit"> units available</span>
                    <span >  available</span>
                </div>
                
                
              </div>

            </div>
            
            <div class="bg-gray-50 rounded-lg p-4 mb-6">
              <h3 class="font-medium text-gray-800 mb-2">Description</h3>
              <p class="Itbms-description text-gray-600">{{item.description }}</p>
            </div>
            
            <div class="space-y-4">
              <div class="flex flex-wrap gap-x-8 gap-y-2">
                <div class="flex items-center gap-2 min-w-[140px]">
                  <span class="w-3 h-3 rounded-full bg-blue-100 border border-blue-200"></span>
                  <span class="text-gray-600">Color:</span>
                  <span class="Itbms-color font-medium">{{item.color || 'No Color'}}</span>
                </div>
                
                <div class="flex items-center gap-2 min-w-[140px]">
                  <span class="w-3 h-3 rounded-full bg-blue-100 border border-blue-200"></span>
                  <span class="text-gray-600">RAM:</span>
                  <span class="Itbms-ramGb font-medium">{{item.ramGb || 0}} </span>
                  <span class="Itbms-ramGb-unit font-medium"> GB</span>

                </div>
              </div>
              
              <div class="flex flex-wrap gap-x-8 gap-y-2">
                <div class="flex items-center gap-2 min-w-[140px]">
                  <span class="w-3 h-3 rounded-full bg-blue-100 border border-blue-200"></span>
                  <span class="text-gray-600">Storage:</span>
                  <span class="Itbms-storageGb font-medium">{{item.storageGb || 0}} </span>
                  <span class="Itbms-storageGb-unit font-medium"> GB</span>

                </div>
                
                <div class="flex items-center gap-2 min-w-[140px]">
                  <span class="w-3 h-3 rounded-full bg-blue-100 border border-blue-200"></span>
                  <span class="text-gray-600">Screen:</span>
                  <span class="Itbms-screenSizeInch	font-medium">{{item.screenSizeInch || 0}} </span>
                  <span class="Itbms-screenSizeInch-unit font-medium"> Inch</span>

                </div>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Error Message Box -->
        <ErrorMessage v-if="!fetchData && item === null" @goBack="goBack" />

      </div>
    </div>
  </template>
  