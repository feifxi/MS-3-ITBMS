<script setup>
import { fetchAllBrands, fetchAllSaleItemsV2 } from '@/api/index.js'
import { ref, computed, onMounted, reactive, watch } from 'vue'
import CardSaleItem from '../components/CardSaleItem.vue'
import Button from '@/components/Button.vue'
import { ArrowDownWideNarrow, ArrowUpWideNarrow, List, ListFilterPlus, X } from 'lucide-vue-next'
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
const handleAddFilterByBrands = (brandName) => {
  // console.log(brandName)
  const existingIndex = filterBrands.findIndex((brand) => brand === brandName)
  if (existingIndex >= 0) {
    return filterBrands.splice(existingIndex, 1)
  }
  filterBrands.push(brandName)
}

const handleRemoveBrandFilter = (brandName) => {
  const removeIndex = filterBrands.findIndex((brand) => brand === brandName)
  filterBrands.splice(removeIndex, 1)
}

const handleClearBrandFilter = () => {
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

const isShowBrandFilters = ref(false)


</script>


<template>
  <main class="py-8 px-16 min-h-screen bg-gradient-to-br from-rose-50 to-purple-50">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-2xl font-bold">Sale Items</h2>
      <RouterLink to="/sale-items/add" >
        <Button variant="primary" class="itbms-sale-item-add ">➕ Add Sale Item</Button>
      </RouterLink>
    </div>
    <!-- Filtering & Sorting-->
    <div class="p-3 rounded-xl mb-4 bg-white">
      <div class="flex">
        <!-- Filter -->
        <div class="flex-2">
          <div class="flex gap-1 relative">
            <button @click="isShowBrandFilters = !isShowBrandFilters" 
              class="itbms-brand-filter itbms-brand-filter-button cursor-pointer p-2 hover:bg-neutral-200 transition-all duration-300 rounded">
              <ListFilterPlus />
            </button>
            <Button :class-name="'itbms-brand-filter-clear'" :variant="filterBrands.length > 0 ? 'primary' :'ghost'" @click="handleClearBrandFilter" >clear</Button>
          </div>
          <div v-if="isShowBrandFilters" class="z-10 absolute border border-neutral-100 bg-white shadow-xl  rounded-xl p-5 gap-8 grid grid-cols-5">
            <div v-for="brand in brands" class="flex items-center gap-2">
              <input type="checkbox" class="size-5" :checked="filterBrands.includes(brand.name)" @click="() => handleAddFilterByBrands(brand.name)">
              <label class="itbms-filter-item">{{ brand.name }}</label>
            </div>
          </div>
        </div>
        <!-- Size & Sorting -->
        <div class="flex-1 flex justify-end gap-3">
          <div class="flex gap-2 items-center">
            <h3>show</h3>
            <select v-model="pagination.size" class="itbms-page-size input !w-auto">
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20">20</option>
            </select>
          </div>

          <div class="flex" >
            <button @click="handleSortDefault" :class="['itbms-brand-none paginationBtn !bg-rose-50 border border-white', {
              '!bg-rose-200': sortOptions.sortField === 'createdOn' 
              }]">
              <List />
            </button>

            <button @click="handleSortAsc" :class="['itbms-brand-asc paginationBtn !bg-rose-50 border border-white', {
              '!bg-rose-200': sortOptions.sortDirection === 'asc' && sortOptions.sortField === 'brand.name' 
              }]">
              <ArrowUpWideNarrow />
            </button>

            <button @click="handleSortDesc" :class="['itbms-brand-desc paginationBtn !bg-rose-50 border border-white', { 
              '!bg-rose-200': sortOptions.sortDirection === 'desc' && sortOptions.sortField === 'brand.name' 
              }]">
              <ArrowDownWideNarrow />
            </button>
          </div>
        </div> 
      </div>

      <div class="flex flex-wrap gap-3 p-3" v-if="filterBrands.length > 0">
        <Button v-for="brand in filterBrands" :class-name="'itbms-filter-item-clear'" variant="secondary" @click="() => handleRemoveBrandFilter(brand)">
          <p class="">{{ brand }}</p>
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
        <button @click="handleGoFirst" class='itbms-page-first paginationBtn !px-4' :disabled="pagination.first">
          First
        </button>
        <button @click="handleClickPrev" class='itbms-page-prev paginationBtn !px-4' :disabled="pagination.first">
          Prev
        </button>
        
        <button v-for="pageIndex in paginatedPages"
          :class="[`itbms-page-${pageIndex-1} paginationBtn w-10`, {
            '!bg-rose-500': pagination.page + 1 === pageIndex
          }]"
          @click="() => handleChangePage(pageIndex-1)"
        >
          {{ pageIndex }}
        </button>

        <button @click="handleClickNext" class="itbms-page-next paginationBtn !px-4" :disabled="pagination.last"> 
          Next
        </button>
        <button @click="handleGoLast" class='itbms-page-last paginationBtn !px-4' :disabled="pagination.last">
          Last
        </button>
      </div>
    </section>
  </main>
</template>
