<script setup>
import { useRouter } from 'vue-router'
import { useStatusMessageStore } from '@/stores/statusMessage'
import { useAuthStore } from '@/stores/auth'
import { onMounted, ref } from 'vue'
import { fetchUserProfile, updateUserProfile } from '@/api'
import placeHolder from '@/assets/placeholder.svg' 

const BASE_API = import.meta.env.VITE_BASE_API
const IMAGE_ENDPOINT = BASE_API + "/v2/users/images/"

const router = useRouter()
const statusMessageStore = useStatusMessageStore()
const auth = useAuthStore()

const userProfile = ref(null)
const isLoading = ref(false)

const loadUserProfile = async () => {
  try {
    isLoading.value = true
    const res = await fetchUserProfile(auth)
    const profile = await res.json()
    userProfile.value = profile
    
    console.log(profile) // print user profile
  } catch (error) {
    console.log(error)
  }
}

const updateProfile = async () => {
  try {
    isLoading.value = true
    const res = await updateUserProfile(userProfile, auth)
    const result = await res.json()
    console.log(result)
  } catch (error) {
    console.log(error)
  }
}

onMounted(async () => {
  loadUserProfile()
})
</script>

<template>
  <div>
    <div class="min-h-screen bg-gradient-to-br from-rose-100 via-pink-100 to-purple-100 flex justify-center items-center p-6">
      <div class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-10 w-full max-w-2xl border-4 border-pink-100 backdrop-blur-md">
        <h1 class="text-2xl sm:text-4xl font-extrabold text-rose-500 mb-8 sm:mb-10 text-center tracking-widest drop-shadow-sm">
            <div class="text-center mb-8">
              <div class="relative inline-flex justify-center items-center w-48 h-48 bg-gradient-to-br from-pink-400 to-rose-500 rounded-full mb-4 shadow-2xl drop-shadow-2xl">
                <!-- User head -->
                <div class="absolute top-5 w-15 h-15 bg-white rounded-full drop-shadow-lg"></div>
                <!-- User body -->
                <div class="absolute bottom-6 w-23 h-20 bg-white rounded-t-full drop-shadow-lg"></div>
              </div>
            </div>
          <span class="inline-flex items-center gap-2">
            User Profile
          </span>
        </h1>
        
        <div class="space-y-6">
          <!-- Nickname -->
          <div>
            <label class="block text-purple-700 font-semibold mb-2">
              Nickname
            </label>
            <div
              class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-700">
              {{ userProfile?.nickname || '-' }}
            </div>
          </div>

          <!-- Full Name -->
          <div>
            <label class="block text-purple-700 font-semibold mb-2">
              Fullname
            </label>
            <div
              class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 shadow-inner text-gray-700">
              {{ userProfile?.fullName || '-' }}
            </div>
          </div>

          <!-- Email -->
          <div>
            <label class="block text-purple-700 font-semibold mb-2">
              Email
            </label>
            <div
              class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-700">
              {{ userProfile?.email || '-' }}
            </div>
          </div>

          <div v-if="userProfile?.userType === 'SELLER'" class="space-y-6">
            <!-- Shop Name -->
            <div>
              <label class="block text-purple-700 font-semibold mb-2">
                Shop Name
              </label>
              <div
                class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-700">
                {{ userProfile?.shopName || '-' }}
              </div>
            </div>

            <div>
              <label class="block text-purple-700 font-semibold mb-2">
                Phone
              </label>
              <div
                class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-700">
                {{ userProfile?.phone || '-' }}
              </div>
            </div>

            <div>
              <label class="block text-purple-700 font-semibold mb-2">
                Bank
              </label>
              <div
                class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-700">
                {{ userProfile?.bankName || '-' }}
              </div>
            </div>

            <div>
              <label class="block text-purple-700 font-semibold mb-2">
                Bank Account Number
              </label>
              <div
                class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-700">
                {{ userProfile?.bankAccountNumber || '-' }}
              </div>
            </div>

            <div>
              <label class="block text-purple-700 font-semibold mb-2">
                Nation Id
              </label>
              <div
                class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-700">
                {{ userProfile?.idCardNumber || '-' }}
              </div>
            </div>
            
            <!-- National id card image -->
            <label class="block text-purple-700 font-semibold mb-2">
              National ID Card
            </label>
            <div class="grid grid-cols-2 gap-3">
              <img 
                :src="IMAGE_ENDPOINT + userProfile?.idCardImageFront || placeHolder" 
                alt="national id card" 
                :class="'shadow-md'" 
              />
              <img 
                :src="IMAGE_ENDPOINT + userProfile?.idCardImageBack || placeHolder" 
                alt="national id card" 
                :class="'shadow-md'" 
              />
            </div>
          </div>

          

          <!-- Edit Button -->
        <div class="mt-8 flex justify-end">
          <router-link to="/profile/edit">
            <button 
              class="bg-red-400 text-white font-bold py-3 px-8 rounded-full shadow-lg hover:bg-red-600 transition">
              Edit
            </button>
          </router-link>
        </div>
       </div>
      </div>
    </div>
  </div>
</template>
