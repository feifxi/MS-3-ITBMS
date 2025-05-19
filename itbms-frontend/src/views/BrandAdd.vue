<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createBrand } from '@/api/index.js'

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
    router.push('/brands')
  } catch (err) {
    console.error(err)
    alert("Failed to add brand.")
  } finally {
    isSubmitting.value = false
  }
}

const cancelEdit = () => {
  router.push('/brands')
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-rose-100 via-pink-100 to-purple-100 flex justify-center items-center p-6">
    <div class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-10 w-full max-w-2xl border-4 border-pink-100 backdrop-blur-md">
      <h2 class="text-4xl font-extrabold text-rose-500 mb-10 text-center tracking-widest drop-shadow-sm">🌸 Add New Brand 🌸</h2>

      <form @submit.prevent="submitBrand" class="itbms-manage-brand space-y-6">

        <!-- Brand Name -->
        <div class="itbms-name">
          <label class="block text-purple-700 font-semibold mb-1">🏷️ Brand Name *</label>
          <input
            v-model="newBrand.name"
            type="text"
            required
            placeholder="Enter brand name"
            class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 focus:ring-rose-400 
            transition shadow-inner"
          />
        </div>

        <!-- Website -->
        <div class="itbms-websiteUrl">
          <label class="block text-purple-700 font-semibold mb-1">🌐 Website</label>
          <input
            v-model="newBrand.websiteUrl"
            type="url"
            placeholder="https://example.com"
            class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 focus:outline-none focus:ring-2 
            focus:ring-purple-400 transition shadow-inner"
          />
        </div>

        <!-- Active Toggle -->
        <div class="itbms-isActive flex items-center justify-between">
          <label class="text-purple-800 font-semibold">✨ Active</label>
          <button
            type="button"
            @click="newBrand.isActive = !newBrand.isActive"
            class="relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-300"
            :class="newBrand.isActive ? 'bg-pink-400' : 'bg-gray-300'"
          >
            <span
              class="inline-block h-4 w-4 transform rounded-full bg-white transition-transform"
              :class="newBrand.isActive ? 'translate-x-6' : 'translate-x-1'"
            />
          </button>
        </div>

        <!-- Country of Origin -->
        <div class="itbms-countryOfOrigin">
          <label class="block text-purple-700 font-semibold mb-1">🏳️ Country of Origin</label>
          <input
            v-model="newBrand.countryOfOrigin"
            type="text"
            placeholder="Country"
            class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 focus:outline-none focus:ring-2 focus:ring-rose-300 transition shadow-inner"
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
            :disabled="isSubmitting"
            class="itbms-save-button bg-gradient-to-r from-pink-400 to-rose-400 text-white px-6 py-2.5 rounded-full hover:from-rose-400 hover:to-pink-400 shadow-lg transition font-bold"
          >
            {{ isSubmitting ? "Saving..." : "💾 Save" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

