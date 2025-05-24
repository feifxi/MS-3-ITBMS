<script setup>
import { fetchAllBrands, fetchAllSaleItems, fetchAllSaleItemsV2 } from '@/api/index.js'
import { ref, computed, onMounted, reactive, watch } from 'vue'
import CardSaleItem from '../components/CardSaleItem.vue'
import Button from '@/components/Button.vue'
import { ArrowDownWideNarrow, ArrowUpWideNarrow, List, X } from 'lucide-vue-next'

const saleitems = ref([])
const brands = ref([])
const loading = ref(false)

const pagination = reactive({
  last: false,
  first: false,
  totalPages: 0,
  totalElements: 0,
  page: 0,
  size: 10
})
const filterBrands = reactive([])
const sortOptions = reactive({
  sortField: 'createdOn',
  sortDirection: null,
})


const fetchSaleItems = async () => {
  loading.value = true
  try {
      const res = await fetchAllSaleItemsV2(pagination.page, pagination.size, filterBrands, sortOptions.sortField, sortOptions.sortDirection)
      if (!res.ok) throw new Error("Something went wrong")
      const data = await res.json()
      const { content, first, last, size, totalPages } = data
      saleitems.value = content
      pagination.first = first
      pagination.last = last
      pagination.totalPages = totalPages
    } catch (err) {
      console.error('Failed to fetch item: ', err)
    } 
    loading.value = false
}

const fetchBrands = async () => {
  loading.value = true
  try {
      const res = await fetchAllBrands()
      if (!res.ok) throw new Error("Something went wrong")
      const brandData = await res.json()
      const sortedBrand = brandData.sort((a, b) => a.name.localeCompare(b.name, undefined, { sensitivity: 'base' }))
      brands.value = sortedBrand
    } catch (err) {
      console.error('Failed to fetch brand: ', err)
    } 
    loading.value = false
}

// Filtering
const handleFilterByBrands = (e) => {
  pagination.page = 0
  const brandName = e.target.value
  const existing = filterBrands.find((brand) => brand === brandName)
  if (existing) return
  filterBrands.push(brandName)
}

const handleRemoveBrandFilter = (brandName) => {
  pagination.page = 0
  const removeIndex = filterBrands.findIndex((brand) => brand === brandName)
  filterBrands.splice(removeIndex, 1)
}

const handleClearBrandFilter = () => {
  pagination.page = 0
  filterBrands.splice(0, filterBrands.length)
}

// Sorting 
const handleSortDefault = () => {
  pagination.page = 0
  sortOptions.sortField = 'createdOn'
  sortOptions.sortDirection = null
}

const handleSortAsc = () => {
  pagination.page = 0
  sortOptions.sortField = 'brand.name'
  sortOptions.sortDirection = 'asc'
}

const handleSortDesc = () => {
  pagination.page = 0
  sortOptions.sortField = 'brand.name'
  sortOptions.sortDirection = 'desc'
}


// Pagination
const handleChangePage = (pageNumber) => {
  pagination.page = pageNumber
}

const handleGoFirst = () => {
  pagination.page = 0
}

const handleGoLast = () => {
  pagination.page = pagination.totalPages - 1
}

const handleClickNext = () => {
  if (pagination.page + 1 >= pagination.totalPages) return
  pagination.page += 1
}

const handleClickPrev = () => {
  if (pagination.page <= 0) return
  pagination.page -= 1
}


watch(()=> pagination.page, () => {
  fetchSaleItems()
})

watch([()=> pagination.size, filterBrands, sortOptions], () => {
  pagination.page = 0
  fetchSaleItems()
})

onMounted(async () => {
  await fetchSaleItems()
  await fetchBrands()
})


// const filterItems = computed(() => {
//   return items.value.filter(item => {
//     const matchesSearch = item.model.toLowerCase().includes(searchQuery.value.toLowerCase())
//     const matchesBrand = selectBrand.value === '' || item.brand === selectBrand.value
//     return matchesSearch && matchesBrand
//   })
// })


</script>


<template>
  <main class="mx-auto px-8 py-16 max-w-screen-xl">
    <h1 class="text-3xl font-extrabold mb-8 text-gray-800 text-center">
      🛍️ All Sale Items
    </h1>

    <div class="flex flex-col md:flex-row md:items-center gap-4 mb-10 justify-center">
      <input type="text" placeholder="🔍 Search by model..."
        class="w-full md:w-1/2 px-5 py-3 border border-gray-300 rounded-xl shadow-md focus:ring-2 focus:ring-blue-300 focus:outline-none transition duration-200" />
    </div>

    <div class="p-3 rounded mb-4 flex shadow-xl border-neutral-300">
      <!-- Filtering -->
      <div class="flex-2">
        <div class="flex">
          <select @change="handleFilterByBrands" class="input !w-auto min-w-[100px]" >
            <option>{{ 'All brands' }}</option>
            <option v-for="brand in brands" :value="brand.name">{{ brand.name }}</option>
          </select>
          <button @click="handleClearBrandFilter" class="border py-3 px-4 bg-neutral-300 cursor-pointer" >clear</button>
        </div>

        <div class="flex flex-wrap gap-3 p-3" v-if="filterBrands.length > 0">
          <div v-for="brand in filterBrands" class="bg-neutral-300 rounded-xl px-3 py-1 max-w-xl flex gap-1 items-center">
            <p>{{ brand }}</p>
            <button @click="() => handleRemoveBrandFilter(brand)" class="cursor-pointer">
              <X class="size-4" />
            </button>
          </div>
        </div>
      </div>

      <!-- Size & Sorting -->
      <div class="flex-1 flex justify-end items-start gap-3">
        <select v-model="pagination.size" class="input !w-auto min-w-[100px]">
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="20">20</option>
        </select>

        <div class="flex" >
          <button @click="handleSortDefault" :class="['paginationBtn', {
            'bg-neutral-500': sortOptions.sortField === 'createdOn' 
            }]">
            <List />
          </button>

          <button @click="handleSortAsc" :class="['paginationBtn', {
            'bg-neutral-500': sortOptions.sortDirection === 'asc' && sortOptions.sortField === 'brand.name' 
            }]">
            <ArrowUpWideNarrow />
          </button>

          <button @click="handleSortDesc" :class="['paginationBtn', { 
            'bg-neutral-500': sortOptions.sortDirection === 'desc' && sortOptions.sortField === 'brand.name' 
            }]">
            <ArrowDownWideNarrow />
          </button>
        </div>
      </div>  
    </div>

    <!-- Loading -->
    <section v-if="loading" class="text-center text-blue-500 animate-pulse text-3xl font-bold">
      Loading...
    </section>

    <!-- No SaleItems in database -->
    <section v-else-if="saleitems.length === 0" class="text-center text-3xl text-gray-500 mt-10 font-bold">
      No sale item.
    </section>

    <!-- Have an Item -->
    <section v-else>
      <div  class="mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
        <CardSaleItem v-for="saleItem in saleitems" :key="saleItem.id" :item="saleItem" />
      </div>

      current page {{ pagination.page }}
      <div class="border p-3 mt-5 flex gap-3">
        <button @click="handleGoFirst" class='paginationBtn' 
          :disabled="pagination.first">
          First
        </button>
        <button @click="handleClickPrev" class='paginationBtn' 
          :disabled="pagination.first">
          Prev
        </button>

        <button v-for="i in pagination.totalPages" 
          v-if="true"
          :class="[`item-page-${i} paginationBtn`, {
            'bg-neutral-400': pagination.page + 1 == i
          }]"
          @click="() => handleChangePage(i-1)"
        >
          {{ i }}
        </button>

        <button @click="handleClickNext" class="paginationBtn" 
          :disabled="pagination.last"> 
          Next
        </button>
        <button @click="handleGoLast" class='paginationBtn' 
          :disabled="pagination.last">
          Last
        </button>
      </div>

      
    </section>
  </main>
</template>
