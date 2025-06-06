<script setup>
import { ref, onMounted } from 'vue'
import { fetchAllBrands, fetchAllSaleItems } from '@/api/index.js'
import CardBrandList from '../components/CardBrandList.vue'
import Button from '@/components/Button.vue'
import BreadCrumb from '../components/BreadCrumb.vue';

import { useRouter } from 'vue-router'
import {CirclePlus,Package} from 'lucide-vue-next';


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
  <!-- Breadcrumb แสดงเฉพาะในจอ md ขึ้นไป -->
  <BreadCrumb
    :links="[
      { to: { name: 'SaleItemList' }, label: 'Sale Item List' },
      { to: '#', label: 'Brands list' },
    ]"
  class="hidden md:flex px-4 sm:px-16 py-8 space-x-2"
  />

  <main class="p-4 md:p-8 max-w-7xl mx-auto">
    <!-- Header: All Brands + Add Brand Button -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
      <h1 class="flex items-center gap-2 text-2xl md:text-3xl font-bold text-gray-800 justify-center md:justify-start">
        <Package />
        All Brands
      </h1>

      <div class="flex justify-center md:justify-end">
        <Button @click="goToAddBrand" variant="primary" class="itbms-add-button">
          <CirclePlus />
          Add Brand
        </Button>
      </div>
    </div>

    <!-- Loading / Empty States -->
    <div v-if="isLoading" class="text-center text-blue-500 text-xl animate-pulse">Loading...</div>
    <div v-else-if="brands.length === 0" class="text-center text-gray-500 text-lg">No brand found.</div>

    <!-- Table Content -->
    <div v-else class="overflow-x-auto shadow rounded-lg">
      <!-- Desktop Table Header -->
      <div class="hidden md:grid grid-cols-8 gap-3 bg-gradient-to-r from-rose-200 to-rose-100 p-4 font-semibold text-gray-700 text-center">
        <div>ID</div>
        <div>Name</div>
        <div class="col-span-2">Website</div>
        <div>Status</div>
        <div>Country</div>
        <div class="col-span-2">Actions</div>
      </div>

      <!-- Mobile Header -->
      <div class="md:hidden bg-gradient-to-r from-rose-200 to-rose-100 p-4 font-semibold text-gray-700 text-center">
        Brands ({{ brands.length }})
      </div>

      <!-- Brand Cards -->
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
