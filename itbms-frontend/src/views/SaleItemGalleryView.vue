<script setup>
import { fetchAllBrands, fetchAllSaleItemsV2 } from '@/api/index.js'
import { ref, computed, onMounted, reactive, watch } from 'vue'
import CardSaleItem from '../components/CardSaleItem.vue'
import Button from '@/components/Button.vue'
import { ArrowDownWideNarrow, ArrowUpWideNarrow, List, X } from 'lucide-vue-next'
import { useRoute, useRouter } from 'vue-router'

const persistSortOptions = sessionStorage.getItem('sortOptions')
const persistFilterOptions = sessionStorage.getItem('filterOptions')
const persistPaginationOptions = sessionStorage.getItem('paginationOptions')

const sort = persistSortOptions 
  ? JSON.parse(persistSortOptions) 
  : {
      sortField: 'createdOn',
      sortDirection: null
    }
const filter = persistFilterOptions
  ? JSON.parse(persistFilterOptions) 
  : []

const page = persistPaginationOptions
  ? JSON.parse(persistPaginationOptions) 
  : {
      size: 10
    } 

const route = useRoute()
const router = useRouter()

const saleitems = ref([])
const brands = ref([])
const loading = ref(false)

const pagination = reactive({
  last: false,
  first: false,
  totalPages: 0,
  totalElements: 0,
  page: 0,
  size: page.size
})
const filterBrands = reactive([...filter])
const sortOptions = reactive({
  sortField: sort.sortField,
  sortDirection: sort.sortDirection,
})

const paginatedPages = computed(() => {
  const current = pagination.page
  const total = pagination.totalPages
  const maxVisible = 10

  let start = Math.max(current - Math.floor(maxVisible / 2), 1)
  let end = start + maxVisible - 1

  if (end > total) {
    end = total
    start = Math.max(end - maxVisible + 1, 1)
  }

  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})


const fetchSaleItems = async () => {
  loading.value = true
  try {
      const res = await fetchAllSaleItemsV2(pagination.page, pagination.size, filterBrands, sortOptions.sortField, sortOptions.sortDirection)
      if (!res.ok) throw new Error("Something went wrong")
      const data = await res.json()
      const { content, first, last, totalPages } = data
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
const handleAddFilterByBrands = (e) => {
  const brandName = e.target.value
  if (brandName === 'All brands') return handleClearBrandFilter()
  pagination.page = 0
  // console.log(brandName)
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
  if (filterBrands.length === 0) return
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

const saveSessionData = () => {
  const sort = {
    sortField: sortOptions.sortField,
    sortDirection: sortOptions.sortDirection
  }
  const filter = [...filterBrands]
  const page = {
    size: pagination.size
  }

  sessionStorage.setItem('sortOptions', JSON.stringify(sort))
  sessionStorage.setItem('filterOptions', JSON.stringify(filter))
  sessionStorage.setItem('paginationOptions', JSON.stringify(page))

  // console.log('Save : ', sort, filter, page)
}

const navigateToSelectedPage = () => {
  const { page } = route.query
  if (!page) {
    pagination.page = 0
    setNaviagatedPage()
  } else {
    pagination.page = page-1 
  }
}

const setNaviagatedPage = () => {
  router.push({ query: { page: pagination.page+1  }})
}

watch(()=> pagination.page, () => {
  setNaviagatedPage()
  fetchSaleItems()
})

watch([()=> pagination.size, filterBrands, sortOptions], () => {
  saveSessionData()
  pagination.page = 0
  fetchSaleItems()
})

onMounted(async () => {
  navigateToSelectedPage()
  await fetchBrands()
  await fetchSaleItems()
})


</script>


<template>
  <main class="py-8 px-16 min-h-screen bg-gradient-to-br from-rose-50 to-purple-50">
    <h2 class="text-2xl font-bold mb-4">Sale Items</h2>
    <!-- Filtering & Sorting-->
    <div class="p-3 rounded-xl mb-4 bg-white">
      <div class="flex">
        <!-- Filter -->
        <div class="flex-2">
          <div class="flex gap-1">
            <select @change="handleAddFilterByBrands" class="input !w-auto min-w-[100px]" >
              <option :value="'All brands'" >{{ 'All brands' }}</option>
              <option v-for="brand in brands" :value="brand.name">{{ brand.name }}</option>
            </select>
            <Button :variant="filterBrands.length > 0 ? 'primary' :'ghost'" @click="handleClearBrandFilter" >clear</Button>
          </div>
        </div>
        <!-- Size & Sorting -->
        <div class="flex-1 flex justify-end gap-3">
          <select v-model="pagination.size" class="input !w-auto">
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
          </select>

          <div class="flex" >
            <button @click="handleSortDefault" :class="['paginationBtn !bg-rose-50 border border-white', {
              '!bg-rose-200': sortOptions.sortField === 'createdOn' 
              }]">
              <List />
            </button>

            <button @click="handleSortAsc" :class="['paginationBtn !bg-rose-50 border border-white', {
              '!bg-rose-200': sortOptions.sortDirection === 'asc' && sortOptions.sortField === 'brand.name' 
              }]">
              <ArrowUpWideNarrow />
            </button>

            <button @click="handleSortDesc" :class="['paginationBtn !bg-rose-50 border border-white', { 
              '!bg-rose-200': sortOptions.sortDirection === 'desc' && sortOptions.sortField === 'brand.name' 
              }]">
              <ArrowDownWideNarrow />
            </button>
          </div>
        </div> 
      </div>

      <div class="flex flex-wrap gap-3 p-3" v-if="filterBrands.length > 0">
        <Button v-for="brand in filterBrands" variant="secondary" @click="() => handleRemoveBrandFilter(brand)">
          <p>{{ brand }}</p>
          <X class="size-4" />
        </Button>
      </div>
    </div>

    <!-- Loading -->
    <section v-if="loading" class="text-center text-blue-500 animate-pulse text-3xl font-bold">
      Loading...
    </section>

    <!-- No SaleItems -->
    <section v-else-if="saleitems.length === 0" class="text-center text-3xl text-gray-500 mt-10 font-bold">
      No sale item.
    </section>

    <!-- Item -->
    <section v-else>
      <div class="mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
        <CardSaleItem v-for="saleItem in saleitems" 
          :key="saleItem.id" 
          :item="saleItem" 
          :query="`fromPage=${pagination.page + 1}`"
        />
      </div>
      
      <!-- Pagination -->
      <div class="p-4 mt-5 flex gap-3 rounded-xl bg-white justify-center text-white font-bold">
        <button @click="handleGoFirst" class='paginationBtn !px-4' :disabled="pagination.first">
          First
        </button>
        <button @click="handleClickPrev" class='paginationBtn !px-4' :disabled="pagination.first">
          Prev
        </button>
        
        <button v-for="pageIndex in paginatedPages"
          :class="[`item-page-${pageIndex} paginationBtn w-10`, {
            '!bg-rose-500': pagination.page + 1 === pageIndex
          }]"
          @click="() => handleChangePage(pageIndex-1)"
        >
          {{ pageIndex }}
        </button>

        <button @click="handleClickNext" class="paginationBtn !px-4" :disabled="pagination.last"> 
          Next
        </button>
        <button @click="handleGoLast" class='paginationBtn !px-4' :disabled="pagination.last">
          Last
        </button>
      </div>
    </section>
  </main>
</template>
