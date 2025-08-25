 <script setup>
import { fetchAllBrands, fetchAllSaleItemsV2 , fetchAllSaleItemsV3, fetchAllStorageSizes, fetchPriceRanges } from '@/api/index.js'
import { ref, computed, onMounted, reactive, watch } from 'vue'
import CardSaleItem from '../components/CardSaleItem.vue'
import Button from '@/components/Button.vue'
import { ArrowDownWideNarrow, ArrowUpWideNarrow, List, ListFilterPlus, X } from 'lucide-vue-next'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const getSessionStorageItem = (key) => {
  return sessionStorage.getItem(key)
}
const searchKeyword = ref('')

// Retrive saved data from session storage
const persistFilterPriceOptionJSON = getSessionStorageItem("filterPriceOption")
const persistFilterPriceRangeOption = persistFilterPriceOptionJSON
  ? JSON.parse(persistFilterPriceOptionJSON) 
  : {
      lower: 0,
      upper: 5000,
      selectedRanges: []
    }

const persistFilterStorageSizeOptionJOSN = getSessionStorageItem("filterStorageSizeOption")
const persistFilterStorageSizeOption = persistFilterStorageSizeOptionJOSN
  ? JSON.parse(persistFilterStorageSizeOptionJOSN) 
  : []

const persistFilterBrandsOptionJSON = getSessionStorageItem("filterBrandsOption")
const persistFilterBrandsOption = persistFilterBrandsOptionJSON
  ? JSON.parse(persistFilterBrandsOptionJSON) 
  : []

const persistSortOptionJSON = getSessionStorageItem("sortOption")
const persistSortOption = persistSortOptionJSON  
  ? JSON.parse(persistSortOptionJSON) 
  : {
      sortField: 'createdOn',
      sortDirection: null
    }

const persistPaginationOptionJSON = getSessionStorageItem("paginationOption")
const persistPaginationOption = persistPaginationOptionJSON 
  ? JSON.parse(persistPaginationOptionJSON ) 
  : {
      pageNumber: 0,
      size: 10,
    } 


const isShowBrandFilters = ref(false)
const isShowPriceFilters = ref(false)
const isShowStorageFilters = ref(false)

const saleitems = ref([])
const brands = ref([])
const storageSizes = ref([])
const priceRanges = ref([]) 
const loading = ref(false)

// Set the saved data to the current state
const filterBrands = reactive([...persistFilterBrandsOption])

const filterPriceRange = reactive({
  lower: persistFilterPriceRangeOption.lower,
  upper: persistFilterPriceRangeOption.upper,
  selectedRanges: [...(persistFilterPriceRangeOption.selectedRanges || [])],
  customMin: '',
  customMax: ''
})

const filterStorageSizes = reactive([...persistFilterStorageSizeOption])

const sortOption = reactive({
  sortField: persistSortOption.sortField,
  sortDirection: persistSortOption.sortDirection,
})

const pagination = reactive({
  last: false,
  first: false,
  totalPages: 0,
  totalElements: 0,
  page: persistPaginationOption.pageNumber,
  size: persistPaginationOption.size
})

const predefinedPriceRanges = [
  { label: '0 - 5,000 Baht', min: 0, max: 5000 },
  { label: '5,001-10,000 Baht', min: 5001, max: 10000 },
  { label: '10,001-20,000 Baht', min: 10001, max: 20000 },
  { label: '20,001-30,000 Baht', min: 20001, max: 30000 },
  { label: '30,001-40,000 Baht', min: 30001, max: 40000 },
  { label: '40,001-50,000 Baht', min: 40001, max: 50000 }
]

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
      const res = await fetchAllSaleItemsV3(pagination.page, pagination.size, filterBrands, filterPriceRange, filterStorageSizes, sortOption.sortField, sortOption.sortDirection , searchKeyword.value)
      if (!res.ok) throw new Error("Something went wrong")
      const data = await res.json()
      const { content, first, last, totalPages } = data
      saleitems.value = content
      pagination.first = first
      pagination.last = last
      pagination.totalPages = totalPages
  } catch (err) {
    console.error('Failed to fetch item: ', err)
  } finally {
    loading.value = false
  }
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
  } finally {
    loading.value = false
  }
}

const fetchStorageSize = async () => {
  loading.value = true
  try {
    const res = await fetchAllStorageSizes()
      if (!res.ok) throw new Error("Something went wrong")
      const storageData = await res.json()
      storageSizes.value = storageData 
      // storageSizes.value = [...storageData,'1 TB', 'Not specified']
  } catch (err) {
      console.error('Failed to fetch storage sizes: ', err)
      // storageSizes.value = [32, 64, 128, 256, 512,'1 TB', 'Not specified']
  } finally {
    loading.value = false
  }
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

const handlePriceRangeChange = () => {

}

// const handleAddFilterByStorageSize = () => {
  
// }

// const handleClearFilter = () => {
//   filterBrands.splice(0, filterBrands.length)
// }


// Price range filtering
const handleAddPredefinedPriceRange = (range) => {
  const existingIndex = filterPriceRange.selectedRanges.findIndex(r => r.label === range.label)
  if (existingIndex >= 0) {
    filterPriceRange.selectedRanges.splice(existingIndex, 1)
    filterPriceRange.customMin = ''
    filterPriceRange.customMax = ''
  } else {
    filterPriceRange.selectedRanges.push(range)
    filterPriceRange.customMin = range.min.toString()
    filterPriceRange.customMax = range.max.toString()
  }
}

const handleRemovePriceRange = (range) => {
  const removeIndex = filterPriceRange.selectedRanges.findIndex(r => r.label === range.label)
  filterPriceRange.selectedRanges.splice(removeIndex, 1)
}

// const handleCustomPriceRange = () => {
//   const min = parseInt(filterPriceRange.customMin) || 0
//   const max = parseInt(filterPriceRange.customMax) || 999999
  
//   if (min >= max) {
//     alert('Minimum price must be less than maximum price')
//     return
//   }
  
//   const customRange = {
//     label: `${min} - ${max} Baht`,
//     min: min,
//     max: max
//   }

//    // Remove any existing custom range
//   filterPriceRange.selectedRanges = filterPriceRange.selectedRanges.filter(r => !r.label.includes('Custom'))
//   filterPriceRange.selectedRanges.push({ ...customRange, label: `Custom: ${customRange.label}` })
  
//   // Clear custom inputs
//   filterPriceRange.customMin = ''
//   filterPriceRange.customMax = ''
// }

// Storage size filtering

const handleCustomPriceRange = () => {
  const min = parseInt(filterPriceRange.customMin)
  const max = parseInt(filterPriceRange.customMax)

  if (!isNaN(min) && !isNaN(max)) {
    filterPriceRange.selectedRanges = [{
      label: `${min} - ${max} Baht`,
      min,
      max
    }]
  } else if (!isNaN(min)) {
    filterPriceRange.selectedRanges = [{
      label: `${min} Baht`,
      min,
      max: min
    }]
  } else if (!isNaN(max)) {
    filterPriceRange.selectedRanges = [{
      label: `${max} Baht`,
      min: max,
      max
    }]
  }
}

const handleAddFilterByStorageSize = (storageSize) => {
  const value = storageSize === null || storageSize === 'Not specified' ? -1 : storageSize
  if (!filterStorageSizes.includes(value)) {
    filterStorageSizes.push(value)
  } else {
    filterStorageSizes.splice(filterStorageSizes.indexOf(value), 1)
  }
}

const handleRemoveStorageSizeFilter = (storageSize) => {
  const value = storageSize === null || storageSize === 'Not specified' ? -1 : storageSize
  const index = filterStorageSizes.indexOf(value)
  if (index !== -1) {
    filterStorageSizes.splice(index, 1)
  }
}
const handleClearFilter = () => {
  filterBrands.splice(0, filterBrands.length)
  filterPriceRange.selectedRanges.splice(0, filterPriceRange.selectedRanges.length)
  filterStorageSizes.splice(0, filterStorageSizes.length)
  filterPriceRange.customMin = ''
  filterPriceRange.customMax = ''
  searchKeyword.value = ''
 if (route.query.search) {
    const newQuery = { ...route.query }
    delete newQuery.search
    router.replace({
      name: 'SaleItemGallery',
      query: newQuery
    })
    pagination.page = 0
    fetchSaleItems()
  }
}


// Sorting 
const handleSortDefault = () => {
  pagination.page = 0
  sortOption.sortField = 'createdOn'
  sortOption.sortDirection = null
}

const handleSortAsc = () => {
  pagination.page = 0
  sortOption.sortField = 'brand.name'
  sortOption.sortDirection = 'asc'
}

const handleSortDesc = () => {
  pagination.page = 0
  sortOption.sortField = 'brand.name'
  sortOption.sortDirection = 'desc'
}


// Pagination
const handleChangePage = (pageNumber) => {
  pagination.page = pageNumber
  fetchSaleItems()
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
  sessionStorage.setItem('sortOption', JSON.stringify({
    sortField: sortOption.sortField,
    sortDirection: sortOption.sortDirection
  }))
  sessionStorage.setItem('filterBrandsOption', JSON.stringify(
    [...filterBrands]
  ))

  sessionStorage.setItem('filterPriceOption', JSON.stringify({
    lower: filterPriceRange.lower,
    upper: filterPriceRange.upper,
    selectedRanges: [...filterPriceRange.selectedRanges]
  }))
  
  sessionStorage.setItem('filterStorageSizeOption', JSON.stringify([...filterStorageSizes]))

  sessionStorage.setItem('paginationOption', JSON.stringify({
    pageNumber: pagination.page,
    size: pagination.size,
  }))
}

// Close dropdowns when clicking outside
const handleClickOutside = (event) => {
  const brandFilter = event.target.closest('.itbms-brand-filter')
  const priceFilter = event.target.closest('.itbms-price-filter')
  const storageFilter = event.target.closest('.itbms-storage-size-filter')
  
  if (!brandFilter) isShowBrandFilters.value = false
  if (!priceFilter) isShowPriceFilters.value = false
  if (!storageFilter) isShowStorageFilters.value = false
}
watch(() => route.query.search, (newSearch) => {
  if (newSearch) {
    searchKeyword.value = newSearch
    pagination.page = 0
    fetchSaleItems()
  }else {
    if (searchKeyword.value) {
      searchKeyword.value = ''
      pagination.page = 0
      fetchSaleItems()
    }
  }
}, { immediate: true })

onMounted(async () => {
  await fetchBrands()
  await fetchSaleItems()
  await fetchStorageSize()
  document.addEventListener('click', handleClickOutside)
})

watch(()=> pagination.page, () => {
  saveSessionData()
  fetchSaleItems()
})

watch([()=> pagination.size, filterBrands, () => filterPriceRange.selectedRanges, filterStorageSizes, sortOption ], () => {
  pagination.page = 0
  saveSessionData()
  fetchSaleItems()
},{ deep: true })
</script>


<template>
  <main class="py-8 px-4 sm:px-16 min-h-screen bg-gradient-to-br from-rose-50 to-purple-50">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-2xl font-bold">Sale Items</h2>
      <RouterLink to="/sale-items/add" >
        <Button variant="primary" class="itbms-sale-item-add ">➕ Add Sale Item</Button>
      </RouterLink>
    </div>

    <div v-if="route.query.search" class="p-3 rounded-xl mb-4 bg-white">
      <div class="flex items-center gap-3 justify-center">
        <div class="flex items-center gap-2 bg-rose-100 px-4 py-2 rounded-full">
          <span class="text-sm font-medium">Searching for: "{{ route.query.search }}"</span>
          <button @click="handleClearFilter" class="text-rose-500 hover:text-rose-700">
            <X class="size-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- Filter Brands & Price & Storage Size-->
    <div class="p-3 rounded-xl mb-4 bg-white">
      <div class="flex max-md:flex-col gap-5">
        <!-- Filter Brands -->
        <div class="relative itbms-brand-filter">
          <div class="flex items-start gap-1 relative">
            <div @click="isShowBrandFilters = !isShowBrandFilters" class="itbms-brand-filter-button flex items-center border-2 border-rose-300 bg-rose-50 cursor-pointer rounded px-3 py-2 gap-2 ">
              <ListFilterPlus />
              <p v-if="filterBrands.length === 0" class="text-rose-300">Filter by brand(s)</p>
              <!-- Loop selected Brand -->
              <div class="flex flex-wrap gap-3" v-if="filterBrands.length > 0">
                <div v-for="brand in filterBrands" :key="brand" class="itbms-brand-item flex items-center gap-1 bg-rose-100 px-2 py-1 rounded" >
                  <span>{{ brand }}</span>
                  <X class="size-4 cursor-pointer itbms-brand-item-clear" @click.stop="() => handleRemoveBrandFilter(brand)" />
              </div>
            </div>
            </div>
          </div>

          <!-- Dropdown Brands Filter -->
          <div v-if="isShowBrandFilters" class="z-10 absolute flex flex-col border border-neutral-100 bg-white shadow-xl  rounded-xl p-5 gap-5 max-h-100 overflow-y-scroll  min-w-60">
             <h4 class="font-semibold">Brand Options</h4>
            <div v-for="brand in brands" :key="brand.id" class="flex items-center gap-2">
              <input type="checkbox" class=" size-5" @click="() => handleAddFilterByBrands(brand.name)" :checked="filterBrands.includes(brand.name)" >
              <label class="itbms-filter-item cursor-pointer" @click="() => handleAddFilterByBrands(brand.name)">{{ brand.name }}</label>
            </div>
          </div>
        </div>

         <!-- Filter Price Range -->
        <div class="relative itbms-price-filter">
          <div class="flex items-start gap-1 relative">
            <div @click="isShowPriceFilters = !isShowPriceFilters" class="itbms-price-filter-button flex items-center border-2 border-rose-300 bg-rose-50 cursor-pointer rounded px-3 py-2 gap-2">
              <ListFilterPlus />
              <p v-if="filterPriceRange.selectedRanges.length === 0" class="text-rose-300">Price Range</p>
              <!-- Loop selected Price Ranges -->
              <div class="flex flex-wrap gap-3" v-if="filterPriceRange.selectedRanges.length > 0">
                <div v-for="range in filterPriceRange.selectedRanges" :key="range.label" class="itbms-price-item flex items-center gap-1 bg-rose-100 px-2 py-1 rounded">
                  <span>{{ range.label }}</span>
                  <X class="size-4 cursor-pointer itbms-price-item-clear" @click.stop="() => handleRemovePriceRange(range)" />
                </div>
              </div>
            </div>
          </div>

          <!-- Dropdown Price Filter -->
          <div v-if="isShowPriceFilters" class="z-10 absolute flex flex-col border border-neutral-100 bg-white shadow-xl rounded-xl p-5 gap-5 max-h-100 overflow-y-scroll min-w-80">
            <!-- Predefined Price Ranges -->
            <div class="space-y-2">
              <h4 class="font-semibold">Price options</h4>
              <div v-for="range in predefinedPriceRanges" :key="range.label" class="flex items-center gap-2">
                <input type="checkbox" class="size-5" @click="() => handleAddPredefinedPriceRange(range)" :checked="filterPriceRange.selectedRanges.some(r => r.label === range.label)">
                <label class="itbms-filter-item cursor-pointer" @click="() => handleAddPredefinedPriceRange(range)">{{ range.label }}</label>
              </div>
            </div>

            <!-- Custom Price Range -->
            <div class="border-t pt-3 space-y-3">
              <h4 class="font-semibold">Custom Range</h4>
              <div class="flex items-center gap-2">
                <input 
                  type="number" 
                  placeholder="Min Price" 
                  v-model="filterPriceRange.customMin"
                  class="itbms-price-item-min border rounded px-2 py-1 w-24"
                >
                <span>-</span>
                <input 
                  type="number" 
                  placeholder="Max Price" 
                  v-model="filterPriceRange.customMax"
                  class="itbms-price-item-max border rounded px-2 py-1 w-24"
                >
                <span>Baht</span>
                <!-- <Button variant="primary" @click="handleCustomPriceRange" class="px-3 py-1 text-sm">Apply</Button> -->
                <Button variant="primary" @click="handleCustomPriceRange" class="px-3 py-1 text-sm">Apply</Button>

              </div>
            </div>
          </div>
        </div>

        <!-- Filter Storage Size -->
        <div class="relative itbms-storage-size-filter">
          <div class="flex items-start gap-1 relative">
            <div @click="isShowStorageFilters = !isShowStorageFilters" class="itbms-storage-size-filter-button flex items-center border-2 border-rose-300 bg-rose-50 cursor-pointer rounded px-3 py-2 gap-2">
              <ListFilterPlus />
              <p v-if="filterStorageSizes.length === 0" class="text-rose-300">Filter Storage Size</p>
              <!-- Loop selected storage size -->
              <div class="flex flex-wrap gap-3" v-if="filterStorageSizes.length > 0">
                <div v-for="storageSize in filterStorageSizes" :key="storageSize" class="itbms-storage-size-item flex items-center gap-1 bg-rose-100 px-2 py-1 rounded">
                  <span>
                    {{ storageSize === -1 ? 'Not specified' 
                                          : (typeof storageSize === 'number' ? storageSize + 'GB' : storageSize) }}
                  </span>
                  <X class="size-4 cursor-pointer itbms-storage-size-item-clear" @click.stop="() => handleRemoveStorageSizeFilter(storageSize)" />
                </div>
              </div>
            </div>
          </div>

          <!-- Dropdown storage size Filter -->
          <div v-if="isShowStorageFilters" class="z-10 absolute flex flex-col border border-neutral-100 bg-white shadow-xl  rounded-xl p-5 gap-5 max-h-100 overflow-y-scroll">
            <h4 class="font-semibold">Storage Size</h4>

            <div v-for="storageSize in storageSizes" :key="storageSize" class="flex items-center gap-2">
              <input
                type="checkbox"
                class="size-5"
                @click="() => handleAddFilterByStorageSize(storageSize)"
                :checked="filterStorageSizes.includes(storageSize === null ? -1 : storageSize)"
              >
              <label @click="() => handleAddFilterByStorageSize(storageSize)">
                {{ typeof storageSize === 'number' ? storageSize + 'GB' : (storageSize === null ? 'Not specified' : storageSize) }}
              </label>
            </div>
          </div>
        </div>

        <div>
          <Button :class-name="'itbms-brand-filter-clear'" :variant="(filterBrands.length > 0 || filterPriceRange.selectedRanges.length > 0 || filterStorageSizes.length > 0) ? 'primary' : 'ghost'" 
            @click="handleClearFilter"> Clear 
          </Button>
        </div>
      </div>
    </div>

    <!-- Page Size & Sorting Option -->
    <div class="p-3 rounded-xl mb-4 bg-white">
      <div class="flex max-md:flex-col gap-5 md:items-start">       
        <div class="flex-1 flex justify-start gap-3">
          <div class="flex gap-2 items-center">
            <h3>show</h3>
            <select v-model="pagination.size" class="itbms-page-size input sm:!w-30">
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20">20</option>
            </select>
          </div>

          <div class="flex" >
            <button @click="handleSortDefault" :class="['itbms-brand-none paginationBtn !bg-rose-50 border border-white', {
              '!bg-rose-200': sortOption.sortField === 'createdOn' 
              }]">
              <List />
            </button>

            <button @click="handleSortAsc" :class="['itbms-brand-asc paginationBtn !bg-rose-50 border border-white', {
              '!bg-rose-200': sortOption.sortDirection === 'asc' && sortOption.sortField === 'brand.name' 
              }]">
              <ArrowUpWideNarrow />
            </button>

            <button @click="handleSortDesc" :class="['itbms-brand-desc paginationBtn !bg-rose-50 border border-white', { 
              '!bg-rose-200': sortOption.sortDirection === 'desc' && sortOption.sortField === 'brand.name' 
              }]">
              <ArrowDownWideNarrow />
            </button>
          </div>
        </div> 
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
        />
      </div>
      
      <!-- Pagination -->
      <div v-show="paginatedPages.length > 1" class="p-4 mt-5 flex gap-3 rounded-xl bg-white justify-center text-white font-bold">
        <button @click="handleGoFirst" class='itbms-page-first paginationBtn !px-4' :disabled="pagination.first">
          First
        </button>
        <button @click="handleClickPrev" class='itbms-page-prev paginationBtn !px-4' :disabled="pagination.first">
          Prev
        </button>
        
        <button v-for="pageIndex in paginatedPages"
        :key="pageIndex"
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
