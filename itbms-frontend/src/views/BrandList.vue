<script setup>
import { ref, onMounted } from 'vue'
import { fetchAllBrands, fetchAllSaleItems } from '@/api/index.js'
import CardBrandList from '../components/CardBrandList.vue'
import Button from '@/components/Button.vue'

import { useRouter } from 'vue-router'

const router = useRouter()
const brands = ref([])
const saleItems = ref([])
const isLoading = ref(true)

onMounted(async () => {
  try {
    const [brandRes, saleItemRes] = await Promise.all([
      fetchAllBrands(),
      fetchAllSaleItems()
    ])
    if (!brandRes.ok || !saleItemRes.ok) throw new Error('Failed to fetch')
    brands.value = await brandRes.json()
    saleItems.value = await saleItemRes.json()
  } catch (err) {
    console.error(err)
    brands.value = []
    saleItems.value = []
  } finally {
    isLoading.value = false
  }
})
function goToAddBrand() {
  router.push('/brands/add')
}
function handleDeleted(deletedBrandId) {
  brands.value = brands.value.filter(b => b.id !== deletedBrandId)
}

</script>
<template>
  <main class="p-4 md:p-8 max-w-7xl mx-auto">
    <div class="flex items-center justify-between mb-6 flex-wrap gap-4">
      <h1 class="text-2xl md:text-3xl font-bold text-gray-800">🏷️ All Brands</h1>
      <Button @click="goToAddBrand" variant="primary" class="itbms-add-button">➕ Add Brand</Button>
    </div>

    <div v-if="isLoading" class="text-center text-blue-500 text-xl animate-pulse">Loading...</div>
    <div v-else-if="brands.length === 0" class="text-center text-gray-500 text-lg">No brand found.</div>

    <div v-else class="overflow-x-auto shadow rounded-lg">
      <!-- Headings -->
    <div class="hidden md:grid grid-cols-8 gap-3 bg-gradient-to-r from-rose-300 to-rose-200 p-4 font-semibold text-gray-700 text-center">
      <div>ID</div>
      <div>Name</div>
      <div class="col-span-2">Website</div>
      <div>Status</div>
      <div>Country</div>
      <div class="col-span-2">Actions</div>
    </div>

      <!-- Brand Rows -->
      <CardBrandList
        v-for="brand in brands"
        :key="brand.id"
        :brand="brand"
        :sale-items="saleItems"
        @deleted="handleDeleted"
      />
    </div>
  </main>
</template>
