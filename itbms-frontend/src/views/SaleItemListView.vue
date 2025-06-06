<script setup>
import { ref, onMounted } from 'vue'
import { fetchAllSaleItems } from '@/api/index.js'
import CardItemList from '@/components/CardItemList.vue'
import Button from '@/components/Button.vue'

import { useRouter } from 'vue-router'
import {CirclePlus,Package,ChartNoAxesGantt} from 'lucide-vue-next';


const router = useRouter()

const saleItems = ref([])
const isLoading = ref(true)

onMounted(async () => {
  try {
    const res = await fetchAllSaleItems()
    if (!res.ok) throw new Error('Failed to fetch')
    saleItems.value = await res.json()
    saleItems.value.sort((a, b) => new Date(a.createdOn) - new Date(b.createdOn))
  } catch (err) {
    console.error(err)
    saleItems.value = []
  } finally {
    isLoading.value = false
  }
})

function goToManageBrand() {
  router.push('/brands')
}
function handleDeleted(deletedItemId) {
  saleItems.value = saleItems.value.filter(item => item.id !== deletedItemId)
}

</script>


<template>
  <main class="p-4 md:p-8 max-w-7xl mx-auto">
    <!-- Header Section -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
      <!-- Heading -->
      <h1 class="flex items-center gap-2 text-2xl md:text-3xl font-bold text-gray-800 justify-center md:justify-start">
        <Package />
        <span>All Brands</span>
      </h1>

      <!-- Buttons -->
      <div class="flex gap-2 flex-wrap justify-center md:justify-end">
        <RouterLink to="/sale-items/add">
          <Button variant="primary" class="itbms-sale-item-add">
            <CirclePlus />
            Add Sale Item
          </Button>
        </RouterLink>
        <Button @click="goToManageBrand" variant="secondary" class="itbms-manage-brand">
          <ChartNoAxesGantt />
          Manage Brand
        </Button>
      </div>
    </div>


    <!-- Loading / Empty / Table -->
    <div v-if="isLoading" class="text-center text-blue-500 text-xl animate-pulse">Loading...</div>
    <div v-else-if="saleItems.length === 0" class="text-center text-gray-500 text-lg">No sale item found.</div>
    <div v-else class="overflow-x-auto shadow rounded-lg">
      
      <!-- Headings (Desktop Only) -->
      <div class="hidden md:grid grid-cols-10 gap-3 bg-gradient-to-r from-rose-200 to-rose-100 p-4 font-semibold text-gray-700 text-center">
        <div>ID</div>
        <div>Brand</div>
        <div class="col-span-2">Model</div>
        <div>RAM</div>
        <div>Storage</div>
        <div>Color</div>
        <div>Price</div>
        <div class="col-span-2">Actions</div>
      </div>

      <!-- Mobile Header -->
      <div class="md:hidden bg-gradient-to-r from-rose-200 to-rose-100 p-4 font-semibold text-gray-700 text-center">
        Sale Items ({{ saleItems.length }})
      </div>
      

      <!-- Sale Item Rows -->
      <CardItemList
        v-for="item in saleItems"
        :key="item.id"
        :item="item"
        @deleted="handleDeleted"
      />
    </div>
  </main>
</template>