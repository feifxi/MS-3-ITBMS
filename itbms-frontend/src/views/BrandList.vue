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
  <main class="px-10 py-8">
    <h1 class=" text-3xl font-bold text-center mb-6">🏷️ All Brands</h1>
    <Button @click="goToAddBrand" class="itbms-add-button">➕ Add Brand</Button>


    <div v-if="isLoading" class="text-center text-blue-500 text-xl animate-pulse">Loading...</div>
    <div v-else-if="brands.length === 0" class="text-center text-gray-500 text-lg">No brand found.</div>

    <div v-else class="space-y-3">
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
