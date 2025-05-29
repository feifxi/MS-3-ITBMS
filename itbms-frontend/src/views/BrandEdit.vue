<script setup>
import { ref, onMounted, watch , reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchAllBrands, fetchBrandById, updateBrand } from '@/api/index.js'
import Button from '@/components/Button.vue'
import { useStatusMessageStore } from '@/stores/statusMessage'
import { PencilRuler } from 'lucide-vue-next'
import { X } from 'lucide-vue-next'
import { PrinterCheck } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const statusMessageStore = useStatusMessageStore()
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


const currentFocusField = ref(null)
const isFormValid = ref(false)
const errorFormMessage = reactive({
  name: '',
  websiteUrl: '',
  countryOfOrigin: ''
})


const fieldIntegrity = {
    'name': {
        checkConstraint: (data) => {
            return 0 < data.length && data.length <= 30
        },
        errorMessage: 'Brand name must be 1-30 characters long.', 
    },
    'websiteUrl': {
        checkConstraint: (data) => {
            return (data === null || data === '') || (0 < data.length && isValidUrl(data))
        },
        errorMessage: 'Brand URL must be a valid URL or not specified.', 
    },
    'countryOfOrigin': {
        checkConstraint: (data) => {
            return (data === null || data === '') || (0 < data.length && data.length <= 80)
        },
        errorMessage: 'Brand country of origin must be 1-80 characters long or not specified.', 
    }
}

const handleFocusIn = (e) => {
    currentFocusField.value = e.target.name    
}

const handleFocusOut = (e) => {
    const currentField = e.target.name
    if (typeof brand.value[currentField] === 'string') {
        brand.value[currentField] = brand.value[currentField].trim()
    }   
    showErrorToForm()
    currentFocusField.value = null
}

const validateAllField = () => {
    let isValid = true
    for (const field in brand.value) {
        if (!checkFieldIntegrity(field) || (JSON.stringify(brand.value) === originalBrand.value)){
            isValid = false
            break;
        }
    }
    isFormValid.value = isValid
}

const checkFieldIntegrity = (field) => {
    if (fieldIntegrity[field]) {
        return fieldIntegrity[field]?.checkConstraint(brand.value[field])
    } else {
        return true 
    }
} 

const showErrorToForm = () => {
    const field = currentFocusField.value
    if (field) {
        // console.log(field)
        errorFormMessage[field] = checkFieldIntegrity(field) ? '' : fieldIntegrity[field]?.errorMessage
    }
}

watch(brand, () => {
  // Show error message
  showErrorToForm()
  // Disabled save button
  validateAllField()
}, { deep: true })

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
        class="text-2xl sm:text-4xl font-extrabold text-rose-500 mb-8 sm:mb-10 text-center tracking-widest drop-shadow-sm"
      > 
        <span class="inline-flex items-center gap-2"><PencilRuler class="w-6 h-6" />Edit Brand ID: {{ brandId }}<PencilRuler  class="w-6 h-6" />
          </span>
      </h1>

      <div v-if="isLoading" class="text-center text-blue-500 text-xl animate-pulse">
        Loading...
      </div>

      <form v-else @submit.prevent="saveChanges" class="itbms-managed-brand space-y-6">
        <!-- Brand Name -->
        <div>
          <label class="block text-purple-700 font-semibold mb-1">
             Brand Name *
          </label>
          <input
            name="name"
            v-model="brand.name"
            @focusin="handleFocusIn"
            @focusout="handleFocusOut"
            type="text"
            class="itbms-name w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 focus:ring-rose-400 
            transition shadow-inner"
          />
          <p class="itbms-message text-red-500 pl-2">{{ errorFormMessage.name }}</p>
        </div>

     <!-- Website URL -->
        <div class="">
        <label class="block text-purple-700 font-semibold mb-1"> Website</label>
        <input
          name="websiteUrl"
          v-model="brand.websiteUrl"
          @focusin="handleFocusIn"
          @focusout="handleFocusOut"
          type="url"
          placeholder="https://example.com"
          class="itbms-websiteUrl w-full p-3 border border-purple-200 rounded-full bg-purple-50 focus:outline-none focus:ring-2 focus:ring-purple-400
           transition shadow-inner"
          />
          <p class="itbms-message text-red-500 pl-2">{{ errorFormMessage.websiteUrl}}</p>
        </div>


        <!-- Active Toggle -->
        <div class="flex items-center justify-between">
          <label class="text-purple-800 font-semibold"> Active</label>
          <input type="checkbox" class="itbms-isActive size-5" v-model="brand.isActive">
        </div>

        <!-- Country of Origin -->
        <div class="">
          <label class="block text-purple-700 font-semibold mb-1">
            Country of Origin
          </label>
          <input
            name="countryOfOrigin"
            v-model="brand.countryOfOrigin"
            @focusin="handleFocusIn"
            @focusout="handleFocusOut"
            type="text"
            class="itbms-countryOfOrigin w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 
            focus:ring-rose-300 transition shadow-inner"
          />
          <p class="itbms-message text-red-500 pl-2">{{ errorFormMessage.countryOfOrigin }}</p>
        </div>

        <!-- Buttons -->
        <div class="flex jflex-col sm:flex-row justify-between gap-4 pt-6">
          <Button
            type="button"
            class="itbms-cancel-button sm:w-auto text-sm sm:text-base bg-white text-gray-700 px-4 sm:px-6 py-2 sm:py-2.5 rounded-full border border-gray-300 hover:bg-gray-100 transition font-medium shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,0.5)] "
            @click="cancelEdit"
          >
             <X class="w-5 h-5 text-red-500 mr-1" /> Cancel
          </Button>
          <Button
            type="submit"
            :disabled="isSubmitting || !isFormValid"
            :class="['itbms-save-button sm:w-auto order-2 sm:order-none text-sm sm:text-base px-4 sm:px-6 py-2 sm:py-2.5 disabled:from-pink-100 disabled:to-rose-200 disabled:cursor-not-allowed  bg-gradient-to-r from-pink-400 to-rose-400 text-white px-6 rounded-full hover:from-purple-400 hover:to-purple-400 shadow-lg transition font-bold drop-shadow-[0_1px_1px_rgba(0,0,0,1)]']"
          >
            {{ isSubmitting ? "Saving..." : " Save" }}<PrinterCheck />
          </Button>
        </div>
      </form>
    </div>
  </div>
  </div>
</template>
