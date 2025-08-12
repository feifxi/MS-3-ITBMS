<script setup>
import { fetchAllBrands, fetchAllSaleItemsV2 } from '@/api/index.js'
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
const isShowPriceFilters = ref(false)

// เพิ่ม price options
const priceOptions = ref([
  { label: '0 - 5,000', value: { lower: 0, upper: 5000 } },
  { label: '5,001-10,000', value: { lower: 5001, upper: 10000 } },
  { label: '10,001-20,000', value: { lower: 10001, upper: 20000 } },
  { label: '20,001-30,000', value: { lower: 20001, upper: 30000 } },
  { label: '30,001-40,000', value: { lower: 30001, upper: 40000 } },
  { label: '40,001-50,000', value: { lower: 40001, upper: 50000 } },
  { label: '50,001', value: { lower: 50001, upper: 999999 } }
])
const filterPriceOptions = reactive([])
const handleAddFilterByPrice = (priceOption) => {
  const existingIndex = filterPriceOptions.findIndex((option) => 
    option.lower === priceOption.lower && option.upper === priceOption.upper)
  if (existingIndex >= 0) {
    return filterPriceOptions.splice(existingIndex, 1)
  }
  filterPriceOptions.push(priceOption)
}

const handleRemovePriceFilter = (priceOption) => {
  const removeIndex = filterPriceOptions.findIndex((option) => 
    option.lower === priceOption.lower && option.upper === priceOption.upper)
  if (removeIndex >= 0) {
    filterPriceOptions.splice(removeIndex, 1)
  }
}

// ฟังก์ชันสำหรับหา label จาก value
const getPriceLabelFromValue = (priceValue) => {
  const option = priceOptions.value.find(option => 
    option.value.lower === priceValue.lower && option.value.upper === priceValue.upper)
  return option ? option.label : `${priceValue.lower} - ${priceValue.upper}`
}








// Retrive saved data from session storage
const persistFilterPriceOptionJSON = getSessionStorageItem("filterPriceOption")
const persistFilterPriceRangeOption = persistFilterPriceOptionJSON
  ? JSON.parse(persistFilterPriceOptionJSON) 
  : {
      lower: 0,
      upper: 5000
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
const isShowStorageFilters = ref(false)

const saleitems = ref([])
const brands = ref([])
const storageSizes = ref([])
const loading = ref(false)

// Set the saved data to the current state
const filterBrands = reactive([...persistFilterBrandsOption])

const filterPriceRange = reactive({
  lower: persistFilterPriceRangeOption.lower,
  upper: persistFilterPriceRangeOption.upper,
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
      const res = await fetchAllSaleItemsV2(pagination.page, pagination.size, filterBrands, filterPriceRange, 
      filterStorageSizes, sortOption.sortField, sortOption.sortDirection)
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

// const fetchStorageSize = async () => {
//   loading.value = true
//   try {
//     const MOCK_STORAGE = [8, 32 ,124, 256]
//       const res = await fetchAllBrands()
//       if (!res.ok) throw new Error("Something went wrong")
//       const brandData = await res.json()
//       storageSizes.value = MOCK_STORAGE
//   } catch (err) {
//       console.error('Failed to fetch brand: ', err)
//   } finally {
//     loading.value = false
//   }
// }

// แก้ไข fetchStorageSize function
const fetchStorageSize = async () => {
  try {
    const STORAGE_OPTIONS = ['32Gb', '64Gb', '128Gb', '256Gb', '512Gb', '1Tb +']
    storageSizes.value = STORAGE_OPTIONS
  } catch (err) {
    console.error('Failed to fetch storage sizes: ', err)
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

const handlePriceRangeChange = (lower, upper) => {
  filterPriceRange.lower = lower
  filterPriceRange.upper = upper
}

const handleAddFilterByStorageSize = (size) => {
  const existingIndex = filterStorageSizes.findIndex((s) => s === size) 
  if (existingIndex >= 0){
    return filterStorageSizes.splice(existingIndex,1)
  }
  filterStorageSizes.push(size)
}
const handleRemoveStorangeFilter = (size) => {
  const removeIndex = filterStorageSizes.findIndex((s) => s === size)
  if (removeIndex >= 0){
    filterStorageSizes.splice(removeIndex,1)
  }
}

const handleClearFilter = () => {
  filterBrands.splice(0, filterBrands.length)
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

  sessionStorage.setItem('paginationOption', JSON.stringify({
    pageNumber: pagination.page,
    size: pagination.size,
  }))
  sessionStorage.setItem('filterPriceOption', JSON.stringify({
  lower: filterPriceRange.lower,
  upper: filterPriceRange.upper
  }))
  sessionStorage.setItem('filterStorageSizeOption', JSON.stringify([...filterStorageSizes]))

}

onMounted(async () => {
  await fetchBrands()
  await fetchSaleItems()
  await fetchStorageSize()
})

watch(()=> pagination.page, () => {
  saveSessionData()
  fetchSaleItems()
})

watch([()=> pagination.size, filterBrands, sortOption], () => {
  pagination.page = 0
  saveSessionData()
  fetchSaleItems()
})
watch(
  [() => pagination.size, filterBrands, filterPriceRange, filterStorageSizes, sortOption], 
  () => {
    pagination.page = 0
    saveSessionData()
    fetchSaleItems()
  }
)

</script>


<template>
  <main class="py-8 px-4 sm:px-16 min-h-screen bg-gradient-to-br from-rose-50 to-purple-50">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-2xl font-bold">Sale Items</h2>
      <RouterLink to="/sale-items/add" >
        <Button variant="primary" class="itbms-sale-item-add ">➕ Add Sale Item</Button>
      </RouterLink>
    </div>

    <!-- Filter Brands & Price & Storage Size-->
    <div class="p-3 rounded-xl mb-4 bg-white">
      <div class="flex max-md:flex-col gap-5">
        <!-- Filter Brands -->
        <div class="relative">
          <div class="flex items-start gap-1 relative">
            <div @click="isShowBrandFilters = !isShowBrandFilters" class="itbms-brand-filter itbms-brand-filter-button flex items-center border-2 border-rose-300 bg-rose-50 cursor-pointer rounded px-3 py-2 gap-2">
              <ListFilterPlus />
              <p v-if="filterBrands.length === 0" class="text-rose-300">Filter by brand(s)</p>
              <!-- Loop selected Brand -->
              <div class="flex flex-wrap gap-3" v-if="filterBrands.length > 0">
                <Button v-for="brand in filterBrands" :class-name="'itbms-filter-item-clear'" variant="secondary" @click="() => handleRemoveBrandFilter(brand)">
                  <p class="">{{ brand }}</p>
                  <X class="size-4" />
                </Button>
              </div>
            </div>
          </div>







         
<!-- Filter Price -->
<div class="relative">
  <div class="flex items-start gap-1 relative">
    <div @click="isShowPriceFilters = !isShowPriceFilters" class="itbms-price-filter flex items-center border-2 border-rose-300 bg-rose-50 cursor-pointer rounded px-3 py-2 gap-2">
      <ListFilterPlus />
      <p v-if="filterPriceOptions.length === 0" class="text-rose-300">Filter by price</p>
      <!-- Loop selected Price Options -->
      <div class="flex flex-wrap gap-3" v-if="filterPriceOptions.length > 0">
        <Button v-for="priceOption in filterPriceOptions" 
          :key="`${priceOption.lower}-${priceOption.upper}`" 
          :class-name="'itbms-price-item-clear'" 
          variant="secondary" 
          @click="() => handleRemovePriceFilter(priceOption)">
          <p class="">{{ getPriceLabelFromValue(priceOption) }}</p>
          <X class="size-4" />
        </Button>
      </div>
    </div>
  </div>

  <!-- Dropdown Price Filter -->
  <div v-if="isShowPriceFilters" class="z-10 absolute flex flex-col border border-neutral-100 bg-white shadow-xl rounded-xl p-5 gap-5 max-h-100 overflow-y-scroll">
    <div v-for="priceOption in priceOptions" :key="`${priceOption.value.lower}-${priceOption.value.upper}`" class="flex items-center gap-2">
      <input type="checkbox" class="size-5" 
        @click="() => handleAddFilterByPrice(priceOption.value)" 
        :checked="filterPriceOptions.some(option => option.lower === priceOption.value.lower && option.upper === priceOption.value.upper)" >
      <label class="itbms-price-item" @click="() => handleAddFilterByPrice(priceOption.value)">{{ priceOption.label }}</label>
    </div>
  </div>
</div>


<!-- ในส่วน Filter Storage Size แก้ไข button -->
<Button v-for="storageSize in filterStorageSizes" 
  :key="storageSize" 
  :class-name="'itbms-storage-size-item-clear'" 
  variant="secondary" 
  @click="() => handleRemoveStorangeFilter(storageSize)">
  <p class="">{{ storageSize }}</p>
  <X class="size-4" />
</Button>













          <!-- Dropdown Brands Filter -->
          <div v-if="isShowBrandFilters" class="z-10 absolute flex flex-col border border-neutral-100 bg-white shadow-xl  rounded-xl p-5 gap-5 max-h-100 overflow-y-scroll">
            <div v-for="brand in brands" class="flex items-center gap-2">
              <input type="checkbox" class=" size-5" @click="() => handleAddFilterByBrands(brand.name)" :checked="filterBrands.includes(brand.name)" >
              <label class="itbms-filter-item" @click="() => handleAddFilterByBrands(brand.name)">{{ brand.name }}</label>
            </div>
          </div>
        </div>

        <!-- Filter Storage Size -->
        <div class="relative">
          <div class="flex items-start gap-1 relative">
            <div @click="isShowStorageFilters = !isShowStorageFilters" class="itbms-brand-filter itbms-brand-filter-button flex items-center border-2 border-rose-300 bg-rose-50 cursor-pointer rounded px-3 py-2 gap-2">
              <ListFilterPlus />
              <p v-if="filterStorageSizes.length === 0" class="text-rose-300">Filter Storage Size</p>
              <!-- Loop selected storage size -->
              <div class="flex flex-wrap gap-3" v-if="filterStorageSizes.length > 0">
                <Button v-for="storageSize in filterStorageSizes" :key="storageSize" :class-name="'itbms-filter-item-clear'" variant="secondary" @click="() => handleRemoveBrandFilter(storageSize)">
                  <p class="">{{ storageSize }}</p>
                  <X class="size-4" />
                </Button>
              </div>
            </div>
          </div>

          <!-- Dropdown storage size Filter -->
          <div v-if="isShowStorageFilters" class="z-10 absolute flex flex-col border border-neutral-100 bg-white shadow-xl  rounded-xl p-5 gap-5 max-h-100 overflow-y-scroll">
            <div v-for="storageSize in storageSizes" class="flex items-center gap-2">
              <input type="checkbox" class=" size-5" @click="() => handleAddFilterByStorageSize(storageSize)" :checked="filterStorageSizes.includes(storageSize)" >
              <label class="itbms-filter-item" @click="() => handleAddFilterByStorageSize(storageSize)">{{ storageSize }}</label>
            </div>
          </div>
        </div>

        <div>
          <Button :class-name="'itbms-brand-filter-clear'" :variant="filterBrands.length > 0 ? 'primary' :'ghost'" @click="handleClearFilter" >clear</Button>
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
