<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchAllBrands, fetchBrandById, updateBrand } from '@/api/index.js'
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
  try {
    const res = await fetchBrandById(brandId)
    if (!res.ok) throw new Error('Failed to create brand')
    const data = await res.json()
    brand.value = data
    originalBrand.value = JSON.stringify(data)
  } catch (err) {
    console.error(err)
    statusMessageStore.setStatusMessage("The brand does not exist.", false)
  } finally {
    isLoading.value = false
  }
}

const validateForm = () => {
  const hasChanged = JSON.stringify(brand.value).trim() !== originalBrand.value
    if (!isValidUrl(brand.value.websiteUrl?.trim())) {
    websiteError.value = 'Please enter a valid URL.'
  } else {
    websiteError.value = ''
  }
  isModified.value =
    brand.value.name.trim() !== '' &&
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
        <div>
          <label class="block text-purple-700 font-semibold mb-1">
            🏷️ Brand Name *
          </label>
          <input
            v-model="brand.name"
            type="text"
            class="itbms-name w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 focus:ring-rose-400 
            transition shadow-inner"
          />
        </div>

     <!-- Website URL -->
        <div class="">
        <label class="block text-purple-700 font-semibold mb-1">🌐 Website</label>
        <input
          v-model="brand.websiteUrl"
          type="url"
          placeholder="https://example.com"
          class="itbms-websiteUrl w-full p-3 border border-purple-200 rounded-full bg-purple-50 focus:outline-none focus:ring-2 focus:ring-purple-400
           transition shadow-inner"
          />
        </div>


        <!-- Active Toggle -->
        <!-- <div class="itbms-isActive flex items-center justify-between">
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
        </div> -->
        <div class="flex items-center justify-between">
          <label class="text-purple-800 font-semibold">✨ Active</label>
          <input type="checkbox" class="itbms-isActive size-5" v-model="brand.isActive">
        </div>

        <!-- Country of Origin -->
        <div class="">
          <label class="block text-purple-700 font-semibold mb-1">
            🏳️ Country of Origin
          </label>
          <input
            v-model="brand.countryOfOrigin"
            type="text"
            class="itbms-countryOfOrigin w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 
            focus:ring-rose-300 transition shadow-inner"
          />
        </div>

        <!-- Buttons -->
        <div class="flex justify-between pt-6">
          <Button
            type="button"
            class="itbms-cancel-button bg-white text-gray-700 px-6 py-2.5 rounded-full border border-gray-300 hover:bg-gray-100 transition font-medium shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,0.5)] "
            @click="cancelEdit"
          >
            ❌ Cancel
          </Button>
          <Button
            type="submit"
            :disabled="!isModified || isSubmitting"
            :class="['itbms-save-button bg-gradient-to-r from-pink-400 to-rose-400 text-white px-6 py-2.5 rounded-full  hover:from-purple-400 hover:to-purple-400 shadow-lg transition font-bold shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,1)]',
            {
              'cursor-not-allowed' : !isModified,
            }]"
          >
            {{ isSubmitting ? "Saving..." : "💾 Save" }}
          </Button>
        </div>
      </form>
    </div>
  </div>
  </div>
</template>
