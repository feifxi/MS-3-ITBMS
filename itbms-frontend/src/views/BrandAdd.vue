<script setup>
import { ref , computed, watch, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { createBrand } from '@/api/index.js'
import { useStatusMessageStore } from '@/stores/statusMessage';
import Button from '@/components/Button.vue'
import { Flower } from 'lucide-vue-next';
import { X } from 'lucide-vue-next';
import { PrinterCheck } from 'lucide-vue-next';
import { reactive } from 'vue';


const statusMessageStore = useStatusMessageStore()

const newBrand = ref({
  name: '',
  websiteUrl: '',
  isActive: true,
  countryOfOrigin: ''
})

const isSubmitting = ref(false)
const router = useRouter()

const submitBrand = async () => {
  isSubmitting.value = true
  try {
    const res = await createBrand(newBrand.value)
    if (!res.ok) throw new Error('Failed to create brand')
    statusMessageStore.setStatusMessage("The brand has been added.")
    router.push('/brands')
  } catch (err) {
    console.error(err)
    statusMessageStore.setStatusMessage("Failed to add brand")
  } finally {
    isSubmitting.value = false
  }
}

const cancelEdit = () => {
  router.push('/brands')
}
// const isFormValid = computed(() => {
//   return newBrand.value.name.trim() !== ''
// })


// watch(newBrand.value, () => {
//   console.log(newBrand.value)
// })




///////////////////////////////////////////////////////////////
const currentFocusField = ref(null)
const isFormValid = ref(false)
const errorFormMessage = reactive({
  name: '',
  websiteUrl: '',
  countryOfOrigin: ''
})
let originalBrand

const isValidUrl = (url) => {
  try {
    new URL(url)
    return true
  } catch (_) {
    return false
  }
}


const fieldIntegrity = {
    'name': {
        checkConstraint: (data) => {
            return 0 < data.length && data.length < 30
        },
        errorMessage: 'Name must be 1-30 characters long.', 
    },
    'websiteUrl': {
        checkConstraint: (data) => {
            return 0 < data.length && isValidUrl(data)
        },
        errorMessage: 'Website must be only URL type.', 
    },
    'countryOfOrigin': {
        checkConstraint: (data) => {
            return 0 < data.length && data.length < 80
        },
        errorMessage: 'countryOfOrigin must be 1-80 characters long.', 
    }
}

const handleFocusIn = (e) => {
    currentFocusField.value = e.target.name    
}

const handleFocusOut = (e) => {
    const currentField = e.target.name
    if (typeof newBrand.value[currentField] === 'string') {
        newBrand.value[currentField] = newBrand.value[currentField].trim()
    }   
    currentFocusField.value = null
}

const validateAllField = () => {
    let isValid = true
    for (const field in newBrand.value) {
        if (!checkFieldIntegrity(field) || (JSON.stringify(newBrand.value) === originalBrand)){
            isValid = false
            break;
        }
    }
    isFormValid.value = isValid
}

const checkFieldIntegrity = (field) => {
    if (fieldIntegrity[field]) {
        return fieldIntegrity[field]?.checkConstraint(newBrand.value[field])
    } else {
        return true 
    }
} 


watch(newBrand, () => {
    // Show error message
    const field = currentFocusField.value
    console.log(field)
    if (field) {
        // console.log(field)
        errorFormMessage[field] = checkFieldIntegrity(field) ? '' : fieldIntegrity[field]?.errorMessage
    }
    // Disabled save button
    validateAllField()
}, { deep: true })

</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-rose-100 via-pink-100 to-purple-100 flex justify-center items-center p-6">
    <div class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-10 w-full max-w-2xl border-4 border-pink-100 backdrop-blur-md">
      <h2 class="text-4xl font-extrabold text-rose-500 mb-10 text-center tracking-widest drop-shadow-sm">
          <span class="inline-flex items-center gap-2"><Flower class="w-6 h-6" />Add New Brand<Flower class="w-6 h-6" />
          </span>
      </h2>

      <form @submit.prevent="submitBrand" class="itbms-manage-brand space-y-6">

        <!-- Brand Name -->
        <div>
          <label class="block text-purple-700 font-semibold mb-1"> Brand Name *</label>
          <input
            name="name"
            v-model="newBrand.name"
            @focusin="handleFocusIn"
            @focusout="handleFocusOut"
            type="text"
            required
            placeholder="Enter brand name"
            class="itbms-name w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 focus:ring-rose-400 
            transition shadow-inner"
          />
            <p class="text-red-500 pl-2">{{ errorFormMessage.name }}</p>
        </div>

        <!-- Website -->
        <div class="">
          <label class="block text-purple-700 font-semibold mb-1"> Website</label>
          <input
            name="websiteUrl"
            v-model="newBrand.websiteUrl"
            @focusin="handleFocusIn"
            @focusout="handleFocusOut"
            type="url"
            placeholder="https://example.com"
            class="itbms-websiteUrl w-full p-3 border border-purple-200 rounded-full bg-purple-50 focus:outline-none focus:ring-2 
            focus:ring-purple-400 transition shadow-inner"
          />
            <p class="text-red-500 pl-2">{{ errorFormMessage.websiteUrl }}</p>
        </div>

        <!-- Is Active -->
        <div class="flex items-center justify-between">
          <label class="text-purple-800 font-semibold"> Active</label>
          <input type="checkbox" class="itbms-isActive size-5" v-model="newBrand.isActive">
        </div>

        <!-- Country of Origin -->
        <div class="">
          <label class="block text-purple-700 font-semibold mb-1">Country of Origin</label>
          <input
            name="countryOfOrigin"
            v-model="newBrand.countryOfOrigin"
            @focusin="handleFocusIn"
            @focusout="handleFocusOut"
            type="text"
            placeholder="Country"
            class="itbms-countryOfOrigin w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 
            focus:ring-rose-300 transition shadow-inner"
          />
          <p class="text-red-500 pl-2">{{ errorFormMessage.countryOfOrigin }}</p>
        </div>

        <!-- Buttons -->
        <div class="flex justify-between pt-6">
          <Button
            type="button"
            class="itbms-cancel-button bg-white text-gray-700 px-6 py-2.5 rounded-full border border-gray-300 hover:bg-gray-100
             transition font-medium shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,0.5)]"
            @click="cancelEdit"
          >
            <X class="w-5 h-5 text-red-500 mr-1" /> Cancel
          </Button>
          <Button
            type="submit"
            :disabled="isSubmitting || !isFormValid"
            :class="['itbms-save-button disabled:hover:purple-400 bg-gradient-to-r from-pink-400 to-rose-400 text-white px-6 py-2.5 rounded-full hover:from-purple-400 hover:to-purple-400 shadow-lg transition font-bold shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,0.5)]',
            {
              'cursor-not-allowed' : !isFormValid,
            }]"
          >
            {{ isSubmitting ? "Saving..." : "Save" }}<PrinterCheck />
          </Button>

        </div>
      </form>
    </div>
  </div>
</template>

