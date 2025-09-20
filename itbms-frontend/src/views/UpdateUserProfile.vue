<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { updateUserProfile } from '@/api'
import BreadCrumb from '@/components/BreadCrumb.vue'
import Button from '@/components/Button.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import { useStatusMessageStore } from '@/stores/statusMessage'
import { useAuthStore } from '@/stores/auth'
import { Save, X } from 'lucide-vue-next'

const router = useRouter()
const auth = useAuthStore()
const statusMessageStore = useStatusMessageStore()

const userProfile = ref({
    id: '',
    nickname: '',
    fullName: '',
    email: '',
    userType: '',
    mobileNumber: '',
    bankAccountNumber: '',
    bankName: '',
    nationalIdNumber: ''
})

const isSubmitting = ref(false)
const showConfirmModal = ref(false)
let originalUserProfile

const currentFocusField = ref(null)

// Field validation rules
const fieldRules = {
    nickname: {
        required: true,
        maxLength: 40,
        validate: (value) => value && value.trim().length > 0 && value.trim().length <= 40,
        errorMsg: 'Nickname must be 1-40 characters long.'
    },
    fullName: {
        required: true,
        maxLength: 40,
        validate: (value) => value && value.trim().length > 0 && value.trim().length <= 40,
        errorMsg: 'Full name must be 1-40 characters long.'
    }
}

// Reactive validation state
const fieldErrors = reactive({
    nickname: '',
    fullName: ''
})

// Computed validation status for each field
const fieldValidation = computed(() => {
    const result = {}
    for (const [field, rules] of Object.entries(fieldRules)) {
        const value = userProfile.value[field]
        result[field] = {
            isValid: rules.validate(value),
            isDirty: currentFocusField.value === field || fieldErrors[field] !== '',
            errorMsg: rules.validate(value) ? '' : rules.errorMsg
        }
    }
    return result
})

// Check if form has been modified
const isFormModified = computed(() => {
    return JSON.stringify(userProfile.value) !== originalUserProfile
})

// Check if all fields are valid
const isFormValid = computed(() => {
    return Object.values(fieldValidation.value).every(field => field.isValid)
})

// Main form validation state
const canSave = computed(() => {
    return isFormValid.value && isFormModified.value && !isSubmitting.value
})

// Save button state and text
const saveButtonState = computed(() => {
    if (isSubmitting.value) {
        return { text: 'Saving...', disabled: true, variant: 'primary' }
    }
    
    if (!isFormModified.value) {
        return { text: 'No Changes', disabled: true, variant: 'ghost' }
    }
    
    if (!isFormValid.value) {
        return { text: 'Fix Errors First', disabled: true, variant: 'ghost' }
    }
    
    return { text: 'Save', disabled: false, variant: 'primary' }
})

// Mask phone/bank/national ID numbers
const maskNumber = (number) => {
    if (!number || number.length < 4) return number
    const len = number.length
    const visible = number.slice(-4, -1) // 2nd, 3rd, 4th digits from the end
    return 'x'.repeat(len - 4) + visible + 'x'
}

// Load profile data from sessionStorage or auth store
const loadProfileData = () => {
    const savedProfile = sessionStorage.getItem('userProfileData')
    if (savedProfile) {
        userProfile.value = JSON.parse(savedProfile)
        sessionStorage.removeItem('userProfileData')
    } else if (auth.user) {
        userProfile.value = {
            id: auth.user.id || '',
            nickname: auth.user.nickname || '',
            fullName: auth.user.fullName || '',
            email: auth.user.email || '',
            userType: auth.user.userType || '',
            mobileNumber: auth.user.mobileNumber || '',
            bankAccountNumber: auth.user.bankAccountNumber || '',
            bankName: auth.user.bankName || '',
            nationalIdNumber: auth.user.nationalIdNumber || ''
        }
    }
    originalUserProfile = JSON.stringify(userProfile.value)
}

// Handle field focus events
const handleFocusIn = (e) => {
    currentFocusField.value = e.target.name
}

const handleFocusOut = (e) => {
    const fieldName = e.target.name
    const value = userProfile.value[fieldName]
    
    // Auto-trim string values
    if (typeof value === 'string') {
        userProfile.value[fieldName] = value.trim()
    }
    
    // Update field error if field was focused
    if (fieldValidation.value[fieldName]) {
        fieldErrors[fieldName] = fieldValidation.value[fieldName].errorMsg
    }
    
    currentFocusField.value = null
}

// Form submission handlers
const submitForm = (e) => {
    e.preventDefault()
    if (!canSave.value) return
    showConfirmModal.value = true
}

const confirmSave = async () => {
    showConfirmModal.value = false
    isSubmitting.value = true
    
    try {
        const formData = new FormData()
        formData.append("nickname", userProfile.value.nickname)
        formData.append("fullName", userProfile.value.fullName)

        const res = await updateUserProfile(formData, auth)
        const result = await res.json()
        
        if (!res.ok) throw new Error(result.message || "Something went wrong")
        
        statusMessageStore.setStatusMessage("Profile data is updated successfully.")
        router.push('/profile')
    } catch (err) {
        console.error('Update profile error:', err)
        statusMessageStore.setStatusMessage(
            err.message || "Something went wrong.", 
            false
        )
    } finally {
        isSubmitting.value = false
    }
}

const cancelSave = () => {
    showConfirmModal.value = false
}

const goBack = () => {
    router.push('/profile')
}

const redirectIfNotAuthenticated = () => {
    if (!auth.user) {
        router.push('/')
    }
}

onMounted(() => {
    redirectIfNotAuthenticated()
    loadProfileData()
})
</script>

<template>
      <div>
        <div class="min-h-screen bg-gradient-to-br from-rose-100 via-pink-100 to-purple-100 flex justify-center items-center p-6">
            <div class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-10 w-full max-w-3xl border-4 border-pink-100 backdrop-blur-md">
                
                <!-- Header with large user icon -->
                    <div class="text-center mb-8">
                    <div class="relative inline-flex justify-center items-center w-48 h-48 bg-gradient-to-br from-pink-400 to-rose-500 rounded-full mb-4 shadow-2xl drop-shadow-2xl">
                        <!-- User head -->
                        <div class="absolute top-5 w-15 h-15 bg-white rounded-full drop-shadow-lg"></div>
                        <!-- User body -->
                        <div class="absolute bottom-6 w-23 h-20 bg-white rounded-t-full drop-shadow-lg"></div>
                    </div>
                        <h1 class="text-2xl sm:text-4xl font-extrabold text-rose-500 tracking-widest drop-shadow-sm">Edit Profile</h1>
                    </div>

            <form @submit.prevent="submitForm" class="space-y-6">
                <!-- Editable Fields -->
                <div class="mb-6">
                    <BreadCrumb :links="[
                            { to: '/profile', label: 'Profile' },
                            { to: '#', label: 'Edit' },
                        ]" />
                </div>

                <div>
                    <label class="block text-purple-700 font-semibold mb-2">
                        <span class="text-red-500 text-xl">*</span>
                        Nickname
                    </label>
                    <input 
                        name="nickname" 
                        type="text" 
                        :class="[
                            'w-full p-3 border rounded-full shadow-inner transition-all duration-200',
                            fieldValidation.nickname.isDirty && !fieldValidation.nickname.isValid 
                                ? 'border-red-400 bg-red-50 focus:ring-2 focus:ring-red-400' 
                                : 'border-pink-200 bg-pink-50 focus:ring-2 focus:ring-rose-400',
                            'focus:outline-none'
                        ]"
                        placeholder="Enter nickname..."
                        v-model="userProfile.nickname" 
                        @focusin="handleFocusIn"
                        @focusout="handleFocusOut"
                        :maxlength="fieldRules.nickname.maxLength"
                    >
                    <p v-if="fieldErrors.nickname" class="text-red-500 pl-2 text-sm mt-1">
                        {{ fieldErrors.nickname }}
                    </p>
                </div>

                <div>
                <label class="block text-purple-700 font-semibold mb-2">
         
                        <span class="text-red-500 text-xl">*</span>
                        Full Name
                    </label>
                    <input 
                        name="fullName" 
                        type="text" 
                        :class="[
                            'w-full p-3 border rounded-full shadow-inner transition-all duration-200',
                            fieldValidation.fullName.isDirty && !fieldValidation.fullName.isValid 
                                ? 'border-red-400 bg-red-50 focus:ring-2 focus:ring-red-400' 
                                : 'border-purple-200 bg-purple-50 focus:ring-2 focus:ring-blue-400',
                            'focus:outline-none'
                        ]"
                        placeholder="Enter full name..."
                        v-model="userProfile.fullName" 
                        @focusin="handleFocusIn"
                        @focusout="handleFocusOut"
                        :maxlength="fieldRules.fullName.maxLength"
                    >
                    <p v-if="fieldErrors.fullName" class="text-red-500 pl-2 text-sm mt-1">
                        {{ fieldErrors.fullName }}
                    </p>
                </div>

                <!-- Read-only Fields -->
                <div>
                    <label class="block text-purple-700 font-semibold mb-2">Email</label>
                    <input 
                        type="text" 
                        class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-600 cursor-not-allowed opacity-70" 
                        :value="userProfile.email" 
                        readonly
                    >
                </div>

                <!-- Seller-specific fields -->
                <template v-if="userProfile.userType === 'SELLER'">
                    <div v-if="userProfile.mobileNumber">
                        <label class="block text-purple-700 font-semibold mb-2">Mobile Number</label>
                        <input 
                            type="text" 
                            class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 shadow-inner text-gray-600 cursor-not-allowed opacity-70" 
                            :value="maskNumber(userProfile.mobileNumber)" 
                            readonly
                        >
                    </div>

                    <div v-if="userProfile.bankAccountNumber">
                        <label class="block text-purple-700 font-semibold mb-2">Bank Account Number</label>
                        <input 
                            type="text" 
                            class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-600 cursor-not-allowed opacity-70" 
                            :value="maskNumber(userProfile.bankAccountNumber)" 
                            readonly
                        >
                    </div>

                    <div v-if="userProfile.bankName">
                        <label class="block text-purple-700 font-semibold mb-2">Bank Name</label>
                        <input 
                            type="text" 
                            class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 shadow-inner text-gray-600 cursor-not-allowed opacity-70" 
                            :value="userProfile.bankName" 
                            readonly
                        >
                    </div>

                    <div v-if="userProfile.nationalIdNumber">
                        <label class="block text-purple-700 font-semibold mb-2">National ID Number</label>
                        <input 
                            type="text" 
                            class="w-full p-3 border border-pink-200 rounded-full bg-pink-50 shadow-inner text-gray-600 cursor-not-allowed opacity-70" 
                            :value="maskNumber(userProfile.nationalIdNumber)" 
                            readonly
                        >
                    </div>

                    <div>
                        <label class="block text-purple-700 font-semibold mb-2">National ID Photo</label>
                        <div class="w-full p-3 border border-purple-200 rounded-full bg-purple-50 shadow-inner text-gray-600 text-center opacity-70">
                            Provided
                        </div>
                    </div>
                </template>

                <div class="flex gap-4 pt-6 justify-center sm:justify-start">
                    <Button 
                        :variant="saveButtonState.variant"
                        @click="submitForm" 
                        :disabled="saveButtonState.disabled"
                        type="submit"
                        :class="[
                                'px-6 py-2.5 rounded-full transition-all duration-200 font-bold shadow-lg min-w-[140px]',
                                saveButtonState.disabled 
                                    ? 'bg-gray-300 !cursor-not-allowed' 
                                    : 'bg-gradient-to-r from-pink-400 to-rose-400 text-white hover:from-purple-400 hover:to-purple-400 drop-shadow-[0_1px_1px_rgba(0,0,0,1)]'
                            ]"
                        >
                            <Save class="w-5 h-5 mr-2 inline" />
                        {{ saveButtonState.text }}
                    </Button>

                    <Button
                        variant="ghost" 
                        @click="goBack" 
                        type="button"
                        class="px-6 py-2.5 bg-white text-gray-700 rounded-full border border-gray-300 hover:bg-gray-100 transition font-medium shadow-lg"
                        >
                            <X class="w-5 h-5 mr-2 inline text-red-500" />
                        Cancel
                    </Button>
                </div>
            </form>
        </div>
      </div>

        <!-- Confirm Modal -->
        <ConfirmModal
            v-if="showConfirmModal"
            title="Confirm Save Changes"
            message="Are you sure you want to save the changes to your profile?"
            button-label="Save Changes"
            :is-disabled="isSubmitting"
            @confirm="confirmSave"
            @cancel="cancelSave"
        />
    </div>
</template>

<style scoped>
input[readonly] {
  background-color: #fdf2f8;
  cursor: not-allowed;
}
</style>