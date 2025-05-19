<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchAllBrands, updateBrand } from '@/api/index.js'
import Button from '@/components/Button.vue'
import { useStatusMessageStore } from '@/stores/statusMessage'

const route = useRoute()
const router = useRouter()
const statusMessageStore = useStatusMessageStore()
const websiteError = ref('') 
const isValidUrl = (url) => {
  try {
    new URL(url)
    return true
  } catch (_) {
    return false
  }
}


const brandId = route.params.id
const originalBrand = ref(null)
const brand = ref({
  name: '',
  websiteUrl: '',
  isActive: true,
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
    if (!isValidUrl(brand.value.websiteUrl.trim())) {
    websiteError.value = 'Please enter a valid URL.'
  } else {
    websiteError.value = ''
  }
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
  <div
    class="min-h-screen bg-gradient-to-br from-rose-100 via-pink-100 to-purple-100 flex justify-center items-center p-6"
  >
    <div
      class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-10 w-full max-w-2xl border-4 border-pink-100 backdrop-blur-md"
    >
      <h1
        class="text-4xl font-extrabold text-rose-500 mb-10 text-center tracking-widest drop-shadow-sm"
      >
        🔧 Edit Brand ID: {{ brandId }}
      </h1>

      <div v-if="isLoading" class="text-center text-blue-500 text-xl animate-pulse">
        Loading...
      </div>

      <form v-else @submit.prevent="saveChanges" class="itbms-managed-brand space-y-6">
        <!-- Brand Name -->
        <div class="itbms-name">
          <label class="block text-purple-700 font-semibold mb-1">
            🏷️ Brand Name *
          </label>
          <input
            v-model="brand.name"
            type="text"
            class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 focus:ring-rose-400 
            transition shadow-inner"
          />
        </div>

     <!-- Website URL -->
        <div class="itbms-websiteUrl">
        <label class="block text-purple-700 font-semibold mb-1">🌐 Website</label>
        <input
           v-model="brand.websiteUrl"
          type="url"
          placeholder="https://example.com"
          class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 focus:outline-none focus:ring-2 focus:ring-purple-400
           transition shadow-inner"
        />
        </div>


        <!-- Active Toggle -->
        <div class="itbms-isActive flex items-center justify-between">
          <label class="text-purple-800 font-semibold">✨ Active</label>
          <button
            type="button"
            @click="brand.isActive = !brand.isActive"
            class="relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-300"
            :class="brand.isActive ? 'bg-pink-400' : 'bg-gray-300'"
          >
            <span
              class="inline-block h-4 w-4 transform rounded-full bg-white transition-transform"
              :class="brand.isActive ? 'translate-x-6' : 'translate-x-1'"
            />
          </button>
        </div>

        <!-- Country of Origin -->
        <div class="itbms-countryOfOrigin">
          <label class="block text-purple-700 font-semibold mb-1">
            🏳️ Country of Origin
          </label>
          <input
            v-model="brand.countryOfOrigin"
            type="text"
            class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 
            focus:ring-rose-300 transition shadow-inner"
          />
        </div>

        <!-- Buttons -->
        <div class="flex justify-between pt-6">
          <button
            type="button"
            class="itbms-cancle-button bg-white text-gray-700 px-6 py-2.5 rounded-full border border-gray-300 hover:bg-gray-100 transition font-medium shadow"
            @click="cancelEdit"
          >
            ❌ Cancel
          </button>
          <button
            type="submit"
            :disabled="!isModified || isSubmitting"
            class="itbms-save-button bg-gradient-to-r from-pink-400 to-rose-400 text-white px-6 py-2.5 rounded-full hover:from-rose-400 
            hover:to-pink-400 shadow-lg transition font-bold disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ isSubmitting ? "Saving..." : "💾 Save" }}
          </button>
        </div>
      </form>
    </div>
  </div>
  </div>
</template>
