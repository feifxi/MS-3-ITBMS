<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { fetchUserProfile, updateUserProfile } from '@/api'
import BreadCrumb from '@/components/BreadCrumb.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import { useStatusMessageStore } from '@/stores/statusMessage'
import { useAuthStore } from '@/stores/auth'
import Button from "@/components/Button.vue";
import { Save, X } from 'lucide-vue-next'
import placeHolder from '@/assets/placeholder.svg'

const router = useRouter()
const auth = useAuthStore()
const statusMessageStore = useStatusMessageStore()

const userProfile = ref({
    nickname: '',
    fullName: '',
    email: '',
    userType: '',
    phone: '',
    bankAccountNumber: '',
    bankName: '',
    idCardNumber: ''
})

const isSubmitting = ref(false)
const isLoading = ref(false)
const showConfirmModal = ref(false)
let originalData = ''

// === IMAGE CONFIGURATION ===
const BASE_API = import.meta.env.VITE_BASE_API

const errors = ref({ nickname: '', fullName: '' })

// Validation
const isValid = computed(() => {
    const nick = userProfile.value.nickname?.trim()
    const full = userProfile.value.fullName?.trim()
    return nick && nick.length <= 40 && full && full.length <= 40
})

const isChanged = computed(() => {
    if (!originalData) return false
    
    try {
        const current = JSON.stringify({
            nickname: userProfile.value.nickname,
            fullName: userProfile.value.fullName
        })
        const original = JSON.parse(originalData)
        const originalEditable = JSON.stringify({
            nickname: original.nickname,
            fullName: original.fullName
        })
        return current !== originalEditable
    } catch (error) {
        return false
    }
})

const canSave = computed(() => isValid.value && isChanged.value && !isSubmitting.value)

const saveText = computed(() => {
    if (isSubmitting.value) return 'Saving...'
    if (!isChanged.value) return 'No Changes'
    if (!isValid.value) return 'Fix Errors'
    return 'Save'
})

// Mask numbers: show 2nd, 3rd, 4th digits from end
const maskNumber = (num) => {
    if (!num || num.length < 4) return num
    const str = num.toString()
    const len = str.length
    const visible = str.slice(-4, -1)
    return 'x'.repeat(len - 4) + visible + 'x'
}

// Load data
const loadData = async () => {
    try {
        isLoading.value = true
        const res = await fetchUserProfile(auth)
        if (!res.ok) throw new Error('Failed to load')
        
        const data = await res.json()
        userProfile.value = data
        originalData = JSON.stringify(data)
    } catch (error) {
        statusMessageStore.setStatusMessage("Failed to load profile", false)
        router.push('/profile')
    } finally {
        isLoading.value = false
    }
}

// Validate on blur
const validateField = (field) => {
    const value = userProfile.value[field]?.trim()
    if (!value || value.length > 40) {
        errors.value[field] = `${field} must be 1-40 characters`
    } else {
        errors.value[field] = ''
    }
}

// Submit
const submit = () => {
    if (!canSave.value) return
    showConfirmModal.value = true
}

const confirmSave = async () => {
    showConfirmModal.value = false
    isSubmitting.value = true
    
    try {
        const formData = new FormData()
        
        // Editable fields
        formData.append("nickname", userProfile.value.nickname.trim())
        formData.append("fullName", userProfile.value.fullName.trim())
        
        // Preserve existing data
        if (userProfile.value.email) {
            formData.append("email", userProfile.value.email)
        }
        if (userProfile.value.userType) {
            formData.append("userType", userProfile.value.userType)
        }
        
        // SELLER data (preserve without editing)
        if (userProfile.value.userType === 'SELLER') {
            const sellerFields = ['phone', 'bankAccountNumber', 'bankName', 'idCardNumber', 'shopName']
            sellerFields.forEach(field => {
                if (userProfile.value[field]) {
                    formData.append(field, userProfile.value[field])
                }
            })
            
            // === IMAGE PRESERVATION FLAGS ===
            formData.append("keptIdCardImageFront", "true")
            formData.append("keptIdCardImageBack", "true")
        }

        const res = await updateUserProfile(formData, auth)
        
        if (!res.ok) {
            const errorData = await res.json()
            throw new Error(errorData.message || "Update failed")
        }
        
        statusMessageStore.setStatusMessage("Profile data is updated successfully.")
        router.push('/profile')
        
    } catch (error) {
        statusMessageStore.setStatusMessage("Update failed", false)
    } finally {
        isSubmitting.value = false
    }
}

onMounted(async () => {
    if (!auth.user) return router.push('/')
    await loadData()
})
</script>

<template>
    <div>
        <!-- Loading State -->
        <div v-if="isLoading" class="min-h-screen bg-gradient-to-br from-rose-100 via-pink-100 to-purple-100 flex justify-center items-center">
            <div class="text-center">
                <div class="animate-spin rounded-full h-32 w-32 border-b-2 border-rose-500"></div>
                <p class="text-rose-500 text-2xl font-bold mt-4">Loading...</p>
            </div>
        </div>

        <!-- Main Form -->
        <div v-else class="min-h-screen bg-gradient-to-br from-rose-100 via-pink-100 to-purple-100 flex justify-center items-center p-4 md:p-6">
            <div class="bg-white bg-opacity-80 shadow-2xl rounded-3xl p-6 md:p-10 w-full max-w-3xl border-4 border-pink-100">
                
                <!-- Header -->
                <div class="text-center mb-6 md:mb-8">
                    <div class="relative inline-flex justify-center items-center w-32 h-32 md:w-48 md:h-48 bg-gradient-to-br from-pink-400 to-rose-500 rounded-full mb-4 shadow-2xl">
                        <div class="absolute top-3 md:top-5 w-10 md:w-15 h-10 md:h-15 bg-white rounded-full"></div>
                        <div class="absolute bottom-4 md:bottom-6 w-16 md:w-23 h-12 md:h-20 bg-white rounded-t-full"></div>
                    </div>
                    <h1 class="text-2xl md:text-4xl font-extrabold text-rose-500">Edit Profile</h1>
                </div>

                <form @submit.prevent="submit" class="space-y-4 md:space-y-6">
                    <BreadCrumb :links="[
                        { to: '/profile', label: 'Profile' },
                        { to: '#', label: 'Edit' }
                    ]" />

                    <!-- Nickname -->
                    <div>
                        <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">
                            <span class="text-red-500">*</span> Nickname
                        </label>
                        <input 
                            v-model="userProfile.nickname"
                            @blur="validateField('nickname')"
                            maxlength="40"
                            class="w-full p-3 border rounded-full shadow-inner focus:ring-2 focus:ring-rose-400 focus:outline-none text-sm md:text-base"
                            :class="errors.nickname ? 'border-red-400 bg-red-50' : 'border-pink-200 bg-pink-50'"
                        >
                        <p v-if="errors.nickname" class="text-red-500 text-xs md:text-sm mt-1">{{ errors.nickname }}</p>
                    </div>

                    <!-- Full Name -->
                    <div>
                        <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">
                            <span class="text-red-500">*</span> Full Name
                        </label>
                        <input 
                            v-model="userProfile.fullName"
                            @blur="validateField('fullName')"
                            maxlength="40"
                            class="w-full p-3 border rounded-full shadow-inner focus:ring-2 focus:ring-blue-400 focus:outline-none text-sm md:text-base"
                            :class="errors.fullName ? 'border-red-400 bg-red-50' : 'border-purple-200 bg-purple-50'"
                        >
                        <p v-if="errors.fullName" class="text-red-500 text-xs md:text-sm mt-1">{{ errors.fullName }}</p>
                    </div>

                    <!-- Read-only Fields -->
                    <div>
                        <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">Email</label>
                        <input :value="userProfile.email" readonly
                            class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 text-gray-600 cursor-not-allowed opacity-70 text-sm md:text-base">
                    </div>

                    <div>
                        <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">Password</label>
                        <input type="password" value="••••••••" readonly
                            class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 text-gray-600 cursor-not-allowed opacity-70 text-sm md:text-base">
                    </div>

                    <!-- Seller Fields -->
                    <template v-if="userProfile.userType === 'SELLER'">
                        <div v-if="userProfile.phone">
                            <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">Mobile</label>
                            <input :value="maskNumber(userProfile.phone)" readonly
                                class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 text-gray-600 cursor-not-allowed opacity-70 text-sm md:text-base">
                        </div>
                        
                        <div v-if="userProfile.bankAccountNumber">
                            <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">Bank Account</label>
                            <input :value="maskNumber(userProfile.bankAccountNumber)" readonly
                                class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 text-gray-600 cursor-not-allowed opacity-70 text-sm md:text-base">
                        </div>
                        
                        <div v-if="userProfile.bankName">
                            <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">Bank Name</label>
                            <input :value="userProfile.bankName" readonly
                                class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 text-gray-600 cursor-not-allowed opacity-70 text-sm md:text-base">
                        </div>
                        
                        <div v-if="userProfile.idCardNumber">
                            <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">National ID</label>
                            <input :value="maskNumber(userProfile.idCardNumber)" readonly
                                class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 text-gray-600 cursor-not-allowed opacity-70 text-sm md:text-base">
                        </div>
                        
                        <!-- === ID PHOTO STATIC DISPLAY === -->
                        <!-- <div>
                            <label class="block text-purple-700 font-semibold mb-2 text-sm md:text-base">National ID Photo</label>
                            <div class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 shadow-inner text-gray-600 text-center opacity-70 text-sm md:text-base">
                                Provided
                            </div>
                        </div> -->
                    </template>

                    <!-- Action Buttons -->
                    <div class="flex flex-col sm:flex-row gap-3 md:gap-4 pt-4 md:pt-6 justify-center">
                        <Button 
                            type="submit" 
                            :disabled="!canSave"
                            variant="primary"
                            class-name="min-w-[140px] order-1 sm:order-1"
                        >
                            <Save class="w-4 h-4 md:w-5 md:h-5" />
                            {{ saveText }}
                        </Button>

                        <Button 
                            type="button" 
                            @click="router.push('/profile')"
                            variant="ghost"
                            class-name="order-2 sm:order-2"
                        >
                            <X class="w-4 h-4 md:w-5 md:h-5 text-red-500" />
                            Cancel
                        </Button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Confirm Save Modal -->
        <ConfirmModal v-if="showConfirmModal"
            title="Confirm Save"
            message="Save profile changes?"
            button-label="Save"
            :is-disabled="isSubmitting"
            @confirm="confirmSave"
            @cancel="showConfirmModal = false" />
    </div>
    
</template>