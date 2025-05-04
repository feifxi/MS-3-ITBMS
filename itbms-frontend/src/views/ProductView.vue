<script setup>
import { fetchAllSaleItems } from '@/api/index.js'
import { ref,computed,onMounted } from 'vue'
import CardProduct  from '../components/CardProduct.vue'
const items = ref([])
const searchQuery = ref('')
const selectBrand = ref('')
const loading = ref(true)

onMounted(async () => {
    try{
        const result = await fetchAllSaleItems()
        items.value = Array.isArray(result) ? result : []
    } catch(err){
        console.error('Failed to fetch item: ',err)
        items.value = []
    } finally{
        loading.value = false
    }
})

const filterItems = computed(() => {
    return items.value.filter(item => {
        const matchesSearch = item.model.toLowerCase().includes(searchQuery.value.toLowerCase())
        const matchesBrand = selectBrand.value === '' || item.brand === selectBrand.value 
        return matchesSearch && matchesBrand
    })
})
</script>


<template>
    <div class="max-w-screen-xl mx-auto px-4 py-10 font-sans bg-gradient-to-br from-blue-50 to-white min-h-screen">
  <h1 class="text-3xl font-extrabold mb-8 text-gray-800 text-center">
    🛍️ All Sale Items
  </h1>

  <div class="flex flex-col md:flex-row md:items-center gap-4 mb-10 justify-center">
    <input 
      v-model="searchQuery"
      type="text"
      placeholder="🔍 Search by model..."
      class="w-full md:w-1/2 px-5 py-3 border border-gray-300 rounded-xl shadow-md focus:ring-2 focus:ring-blue-300 focus:outline-none transition duration-200"
    />
  </div>

  <div v-if="loading" class="text-center text-blue-500 text-lg animate-pulse">Loading...</div>
  <div v-else-if="items.length === 0" class="text-center text-gray-500 text-lg">No sale item.</div>
  <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
    <CardProduct
      v-for="item in filterItems"
      :key="item.id"
      :item="item" 
    />
  </div>
</div>

</template>
