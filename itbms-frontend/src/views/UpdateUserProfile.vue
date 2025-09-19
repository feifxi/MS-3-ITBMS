<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { updateUserProfile } from '@/api'
import BreadCrumb from '@/components/BreadCrumb.vue'
import Button from '@/components/Button.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import { useStatusMessageStore } from '@/stores/statusMessage'
import { useAuthStore } from '@/stores/auth'

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
    <main class="px-4 sm:px-16 py-8">
        <BreadCrumb :links="[
            { to: '/profile', label: 'Profile' },
            { to: '#', label: 'Edit' },
        ]" />

        <div class="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-6">
            <h1 class="text-3xl font-bold mb-6">Edit Profile</h1>
            
            <form @submit="submitForm" class="flex flex-col gap-6">
                <!-- Editable Fields -->
                <div class="flex flex-col gap-1">
                    <label class="font-medium">
                        <span class="text-red-500 text-xl">*</span>
                        Nickname
                    </label>
                    <input 
                        name="nickname" 
                        type="text" 
                        :class="[
                            'input border rounded-lg p-3 transition-all duration-200',
                            fieldValidation.nickname.isDirty && !fieldValidation.nickname.isValid 
                                ? 'border-red-500 focus:ring-red-200' 
                                : 'border-gray-300 focus:ring-blue-200',
                            'focus:ring-2 focus:border-transparent focus:outline-none'
                        ]"
                        placeholder="Enter nickname..."
                        v-model="userProfile.nickname" 
                        @focusin="handleFocusIn"
                        @focusout="handleFocusOut"
                        :maxlength="fieldRules.nickname.maxLength"
                    >
                    <p v-if="fieldErrors.nickname" class="text-red-500 pl-2 text-sm">
                        {{ fieldErrors.nickname }}
                    </p>
                </div>

                <div class="flex flex-col gap-1">
                    <label class="font-medium">
                        <span class="text-red-500 text-xl">*</span>
                        Full Name
                    </label>
                    <input 
                        name="fullName" 
                        type="text" 
                        :class="[
                            'input border rounded-lg p-3 transition-all duration-200',
                            fieldValidation.fullName.isDirty && !fieldValidation.fullName.isValid 
                                ? 'border-red-500 focus:ring-red-200' 
                                : 'border-gray-300 focus:ring-blue-200',
                            'focus:ring-2 focus:border-transparent focus:outline-none'
                        ]"
                        placeholder="Enter full name..."
                        v-model="userProfile.fullName" 
                        @focusin="handleFocusIn"
                        @focusout="handleFocusOut"
                        :maxlength="fieldRules.fullName.maxLength"
                    >
                    <p v-if="fieldErrors.fullName" class="text-red-500 pl-2 text-sm">
                        {{ fieldErrors.fullName }}
                    </p>
                </div>

                <!-- Read-only Fields -->
                <div class="flex flex-col gap-1">
                    <label class="font-medium">Email</label>
                    <input 
                        type="text" 
                        class="input border border-gray-200 rounded-lg p-3 bg-gray-50 text-gray-600 cursor-not-allowed" 
                        :value="userProfile.email" 
                        readonly
                    >
                </div>
<!-- 
                <div class="flex flex-col gap-1">
                    <label class="font-medium">Password</label>
                    <input 
                        type="password" 
                        class="input border border-gray-200 rounded-lg p-3 bg-gray-50 text-gray-600 cursor-not-allowed" 
                        value="••••••••" 
                        readonly
                    >
                </div> -->

                <!-- Seller-specific fields -->
                <template v-if="userProfile.userType === 'SELLER'">
                    <div v-if="userProfile.mobileNumber" class="flex flex-col gap-1">
                        <label class="font-medium">Mobile Number</label>
                        <input 
                            type="text" 
                            class="input border border-gray-200 rounded-lg p-3 bg-gray-50 text-gray-600 cursor-not-allowed" 
                            :value="maskNumber(userProfile.mobileNumber)" 
                            readonly
                        >
                    </div>

                    <div v-if="userProfile.bankAccountNumber" class="flex flex-col gap-1">
                        <label class="font-medium">Bank Account Number</label>
                        <input 
                            type="text" 
                            class="input border border-gray-200 rounded-lg p-3 bg-gray-50 text-gray-600 cursor-not-allowed" 
                            :value="maskNumber(userProfile.bankAccountNumber)" 
                            readonly
                        >
                    </div>

                    <div v-if="userProfile.bankName" class="flex flex-col gap-1">
                        <label class="font-medium">Bank Name</label>
                        <input 
                            type="text" 
                            class="input border border-gray-200 rounded-lg p-3 bg-gray-50 text-gray-600 cursor-not-allowed" 
                            :value="userProfile.bankName" 
                            readonly
                        >
                    </div>

                    <div v-if="userProfile.nationalIdNumber" class="flex flex-col gap-1">
                        <label class="font-medium">National ID Number</label>
                        <input 
                            type="text" 
                            class="input border border-gray-200 rounded-lg p-3 bg-gray-50 text-gray-600 cursor-not-allowed" 
                            :value="maskNumber(userProfile.nationalIdNumber)" 
                            readonly
                        >
                    </div>

                    <div class="flex flex-col gap-1">
                        <label class="font-medium">National ID Photo</label>
                        <div class="border border-gray-200 rounded-lg p-3 bg-gray-50 text-gray-600 text-center">
                            Provided
                        </div>
                    </div>
                </template>

                <!-- <div class="flex flex-col gap-1">
                    <label class="font-medium">User Type</label>
                    <input 
                        type="text" 
                        class="input border border-gray-200 rounded-lg p-3 bg-gray-50 text-gray-600 cursor-not-allowed" 
                        :value="userProfile.userType" 
                        readonly
                    >
                </div> -->

                <div class="flex gap-3 items-center mt-8">
                    <Button 
                        :variant="saveButtonState.variant"
                        :onclick="submitForm" 
                        :disabled="saveButtonState.disabled"
                        type="submit"
                        class-name="px-6 py-3 min-w-[140px] transition-all duration-200"
                    >
                        {{ saveButtonState.text }}
                    </Button>

                    <Button 
                        variant="ghost" 
                        :onclick="goBack" 
                        type="button"
                        class-name="px-6 py-3"
                    >
                        Cancel
                        
                    </Button>
                </div>
            </form>
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
    </main>
</template>

<style scoped>
.input[readonly] {
  background-color: #f9fafb;
  cursor: not-allowed;
}
</style>