<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchSaleItemById } from '../api'
import { formatNumber } from '@/libs/utils';
import ItemNotFound from '../components/ItemNotFound.vue';
import mockPhone from '@/assets/image/mockPhone.webp'
import { MoveLeft } from 'lucide-vue-next';
import BreadCrumb from '@/components/BreadCrumb.vue';
import Button from '@/components/Button.vue';

const route = useRoute()
const router = useRouter()
const item = ref(null)
const isLoading = ref(true)
const isNotFound = ref(false)
const isDeleting = ref(false)

const fetchItem = async () => {
  isLoading.value = true
  try {
    const id = route.params.id
    const res = await fetchSaleItemById(id)
    if (!res.ok) throw new Error("Item not found")

    item.value = await res.json() // Status 200
  } catch (err) {
    console.log(err)
    isNotFound.value = true
  }
  isLoading.value = false
}


const goBack = () => {
  router.push('/sale-items')
}

const handleDeleteSaleItem = () => {
  console.log('Remove')
}


onMounted(() => {
  fetchItem()
})
</script>


<template>
  <main class="px-16 py-8">
    <BreadCrumb v-if="item" :links="[
      { to: '/sale-items', label: 'Home' },
      { to: '#', label: `${item.model}` },
    ]" />

    <div class="max-w-6xl mx-auto font-sans text-gray-800">
      <!-- Main Content -->
      <div class="">
        <!-- Error Message Box -->
        <ItemNotFound v-if="isNotFound" @goBack="goBack" />

        <section v-else-if="isLoading" class="text-center text-blue-500 animate-pulse text-3xl font-bold">Loading...</section>

        <section v-else class="itbms-row flex flex-wrap gap-12 bg-white rounded-lg shadow-lg p-6">
          <!-- Product Images -->
          <div class="flex-1 min-w-[300px]">
            <div class="mb-6 text-center overflow-hidden rounded-lg shadow-md">
              <img :src="item.image || mockPhone" alt="product"
                class="w-full h-auto hover:scale-105 transition-transform duration-300" />
            </div>
          </div>

          <!-- Product Info -->
          <div class="flex-1 min-w-[300px]">
            <div class="mb-6 pb-4 border-b border-gray-200">
              <h2 class="itbms-model text-2xl font-bold text-gray-900 mb-2">{{ item.model }}</h2>
              <div class="flex items-center gap-2">
                <span class="itbms-brand bg-blue-100 text-blue-800 font-semibold px-2 py-1 rounded-md text-sm">
                  {{ item.brandName || '-' }}
                </span>
              </div>
            </div>

            <div class="mb-8">
              <div class="flex items-center gap-2 mb-4">
                <span class="itbms-price text-3xl font-bold text-gray-900">{{ formatNumber(item.price || 0) }}</span>
                <span class="itbms-price-unit text-gray-600">Baht</span>
              </div>

              <div class="flex items-center gap-3 mb-6">
                <span class="inline-flex items-center gap-1">
                  <span class="w-3 h-3 rounded-full bg-green-500"></span>
                  <span class="text-green-600 font-medium">In Stock</span>
                </span>
                <span class="text-gray-500">|</span>
                <div class="text-gray-600">
                  <span class="itbms-quantity	">{{ item.quantity || 0 }} units available</span>
                  <span class="itbms-quantity-unit"> units available</span>
                  <span> available</span>
                </div>
              </div>
            </div>

            <div class="bg-gray-50 rounded-lg p-4 mb-6">
              <h3 class="font-medium text-gray-800 mb-2">Description</h3>
              <p class="itbms-description text-gray-600">{{ item.description }}</p>
            </div>

            <div class="space-y-4">
              <div class="flex flex-wrap gap-x-8 gap-y-2">
                <div class="flex items-center gap-2 min-w-[140px]">
                  <span class="w-3 h-3 rounded-full bg-blue-100 border border-blue-200"></span>
                  <span class="text-gray-600">Color:</span>
                  <span class="itbms-color font-medium">{{ item.color || '-' }}</span>
                </div>

                <div class="flex items-center gap-2 min-w-[140px]">
                  <span class="w-3 h-3 rounded-full bg-blue-100 border border-blue-200"></span>
                  <span class="text-gray-600">RAM:</span>
                  <span class="itbms-ramGb font-medium">{{ item.ramGb || '-' }} </span>
                  <span class="itbms-ramGb-unit font-medium">GB</span>
                </div>
              </div>

              <div class="flex flex-wrap gap-x-8 gap-y-2">
                <div class="flex items-center gap-2 min-w-[140px]">
                  <span class="w-3 h-3 rounded-full bg-blue-100 border border-blue-200"></span>
                  <span class="text-gray-600">Storage:</span>
                  <span class="itbms-storageGb font-medium">{{ item.storageGb || '-' }} </span>
                  <span class="itbms-storageGb-unit font-medium">GB</span>
                </div>

                <div class="flex items-center gap-2 min-w-[140px]">
                  <span class="w-3 h-3 rounded-full bg-blue-100 border border-blue-200"></span>
                  <span class="text-gray-600">Screen:</span>
                  <span class="itbms-screenSizeInch	font-medium">{{ item.screenSizeInch || '-' }} </span>
                  <span class="itbms-screenSizeInch-unit font-medium">Inches</span>
                </div>
              </div>
            </div>

            <div class="flex gap-3 items-center mt-10">
              <RouterLink :to="`/sale-items/${item.id}/edit`">
                <Button variant="secondary">
                  Edit
                </Button>
              </RouterLink>

              <Button variant="destructive" :onclick="handleDeleteSaleItem" :disabled="isDeleting">
                {{ isDeleting ? 'Loading...' : 'Delete' }}
              </Button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </main>
</template>