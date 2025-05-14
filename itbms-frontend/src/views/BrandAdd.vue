<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createBrand } from '@/api/index.js'

const newBrand = ref({
  name: '',
  websiteUrl: '',
  countryOfOrigin: ''
})

const router = useRouter()
const isSubmitting = ref(false)

const submitBrand = async () => {
  isSubmitting.value = true
  try {
    const res = await createBrand(newBrand.value)
    if (!res.ok) throw new Error('Failed to create brand')

    statusMessageStore.setStatusMessage("Brand added successfully.", true)
    router.push('/brands') 
  } catch (err) {
    console.error(err)
    alert("Failed to add brand.")
  } finally {
    isSubmitting.value = false
  }
}

// const formData = ref({
//   name: '',
//   websiteUrl: '',
//   isActive: 'true',
//   countryOfOrigin: ''
// })

const cancelEdit = () => {
  router.push('/brands')
}
</script>

<template>

  <div class="min-h-screen bg-gradient-to-br from-blue-100 via-blue-50 to-white flex justify-center items-center p-6">
  <div class="bg-white shadow-2xl rounded-3xl p-10 w-full max-w-2xl border border-blue-100">
    <h2 class="text-3xl font-extrabold text-blue-800 mb-8 text-center tracking-wide">Add New Brand</h2>

    <form @submit.prevent="submitBrand" class="space-y-6">
     <!-- ใส่รูป here -->
      <div class="w-full flex justify-center">
        <div class="relative w-40 h-40 rounded-full overflow-hidden border-4 border-blue-200 shadow-md 
        bg-gradient-to-br from-blue-50 to-white flex items-center justify-center">
          <span class="text-blue-300 text-6xl">📷</span>
          <button
            type="button"
            class="absolute bottom-1 right-1 bg-white text-blue-600 border border-blue-300 px-2 py-0.5 
            text-xs rounded-full shadow-sm hover:bg-blue-50 transition"
          >
            Change Image
          </button>
        </div>
      </div>

      <div>
        <label class="block text-blue-700 font-semibold mb-1">Brand Name *</label>
        <input
          v-model.trim="newBrand.name"
          type="text"
          required
          placeholder="Enter brand name"
          class="w-full p-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-400 transition shadow-sm"
        />
      </div>

      <div>
        <label class="block text-blue-700 font-semibold mb-1">Website</label>
        <input
          v-model.trim="newBrand.websiteUrl"
          type="url"
          placeholder="https://example.com"
          class="w-full p-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-400 transition shadow-sm"
        />
      </div>

      <div>
        <label class="block text-blue-700 font-semibold mb-1">Country of Origin</label>
        <input
          v-model.trim="newBrand.countryOfOrigin"
          type="text"
          placeholder="Country"
          class="w-full p-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-400 transition shadow-sm"
        />
      </div>

      <div class="flex justify-between pt-6">
        <button
          type="button"
          class="bg-gray-100 text-gray-700 px-5 py-2.5 rounded-lg hover:bg-gray-200 transition font-medium"
          :onclick="cancelEdit"
        >
          Cancel
        </button>
        <button
          type="submit"
          :disabled="isSubmitting"
          class="bg-blue-600 text-white px-6 py-2.5 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition font-semibold"
        >
          {{ isSubmitting ? "Saving..." : "Save" }}
        </button>
      </div>
    </form>
  </div>
</div>

</template>