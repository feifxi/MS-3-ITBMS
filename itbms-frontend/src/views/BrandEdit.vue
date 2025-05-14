<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchAllBrands, updateBrand } from '@/api/index.js'
import Button from '@/components/Button.vue'
import { useStatusMessageStore } from '@/stores/statusMessage'

const route = useRoute()
const router = useRouter()
const statusMessageStore = useStatusMessageStore()

const brandId = route.params.id
const originalBrand = ref(null)
const brand = ref({
  name: '',
  websiteUrl: '',
  countryOfOrigin: ''
})
const isLoading = ref(true)
const isSubmitting = ref(false)
const isModified = ref(false)

const fetchBrand = async () => {
  isLoading.value = true
  const res = await fetchAllBrands()
  const allBrands = await res.json()
  const foundBrand = allBrands.find(b => b.id === parseInt(brandId))
  if (!foundBrand) {
    statusMessageStore.setStatusMessage("The brand does not exist.", false)
    return router.push('/brands')
  }
  brand.value = { ...foundBrand }
  originalBrand.value = JSON.stringify(foundBrand)
  isLoading.value = false
}

const validateForm = () => {
  const hasChanged = JSON.stringify(brand.value).trim() !== originalBrand.value
  isModified.value =
    brand.value.name.trim() !== '' &&
    brand.value.websiteUrl.trim() !== '' &&
    brand.value.countryOfOrigin.trim() !== '' &&
    hasChanged
}

const saveChanges = async () => {
  isSubmitting.value = true
  const trimmedBrand = {
    ...brand.value,
    name: brand.value.name.trim(),
    websiteUrl: brand.value.websiteUrl.trim(),
    countryOfOrigin: brand.value.countryOfOrigin.trim()
  }

  try {
    const res = await updateBrand(brandId, trimmedBrand)
    if (res.status === 404) {
      statusMessageStore.setStatusMessage("The brand does not exist.", false)
      return router.push('/brands')
    }
    statusMessageStore.setStatusMessage("The brand has been updated.", true)
    router.push('/brands')
  } catch (err) {
    console.error(err)
    alert("Something went wrong")
  } finally {
    isSubmitting.value = false
  }
}

const cancelEdit = () => {
  router.push('/brands')
}

watch(brand, validateForm, { deep: true })
onMounted(fetchBrand)


</script>

<template>
    <div>
      <h1>แก้ไขแบรนด์ ID: {{ $route.params.id }}</h1>
       <main class="px-10 py-8 max-w-3xl mx-auto text-blue-900">
    <h1 class="text-2xl font-bold mb-6 text-center">🔧 Edit Brand ID: {{ brandId }}</h1>

    <div v-if="isLoading" class="text-center text-blue-500 text-xl animate-pulse">Loading...</div>

    <form v-else class="bg-white shadow-lg p-6 rounded-lg space-y-4">
      <div class="flex flex-col">
        <label class="font-semibold mb-1">Brand Name</label>
        <input type="text" v-model="brand.name" class="input border border-blue-300 rounded px-3 py-2" />
      </div>

      <div class="flex flex-col">
        <label class="font-semibold mb-1">Website URL</label>
        <input type="text" v-model="brand.websiteUrl" class="input border border-blue-300 rounded px-3 py-2" />
      </div>

      <div class="flex flex-col">
        <label class="font-semibold mb-1">Country of Origin</label>
        <input type="text" v-model="brand.countryOfOrigin" class="input border border-blue-300 rounded px-3 py-2" />
      </div>

      <div class="flex justify-between mt-6">
        <Button
          type="button"
          variant="secondary"
          :onclick="cancelEdit"
          class-name="bg-gray-200 text-gray-700 hover:bg-gray-300"
        >
          Cancel
        </Button>

        <Button
          type="button"
          variant="primary"
          :onclick="saveChanges"
          :disabled="!isModified || isSubmitting"
          class-name="bg-blue-600 hover:bg-blue-700 text-white"
        >
          {{ isSubmitting ? 'Saving...' : 'Save' }}
        </Button>
      </div>
    </form>
  </main>
      
    </div>

</template>
  <style scoped>
  .input:focus {
  outline: none;
  border-color: #3b82f6; 
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
}
</style>
