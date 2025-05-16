<script setup>
import { ref, onMounted } from 'vue'
import { fetchAllSaleItems } from '@/api/index.js'
import CardItemList from '@/components/CardItemList.vue'
import Button from '@/components/Button.vue'

import { useRouter } from 'vue-router'

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
  <main class="px-10 py-8">
    
    <h1 class="text-3xl font-bold text-center mb-6">📦 All Discounted Sale Items</h1>
    <RouterLink to="/sale-items/add" class="itbms-sale-item-add">
        <button class="border-2 rounded p-2 cursor-pointer hover:bg-white hover:text-black hover:border-white font-bold  duration-300">Add Sale Item</button>
      </RouterLink>
    <Button @click="goToManageBrand" class="itbms-manage-brand">⚙️ Manage Brand</Button>

    <div v-if="isLoading" class="text-center text-blue-500 text-xl animate-pulse">Loading...</div>
    <div v-else-if="saleItems.length === 0" class="text-center text-gray-500 text-lg">No sale item.</div>

    <div v-else class="space-y-3">
      <CardItemList
        v-for="item in saleItems"
        :key="item.id"
        :item="item"
        @deleted="handleDeleted"
      />
    </div>
  </main>
</template>