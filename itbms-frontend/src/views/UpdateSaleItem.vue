<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { fetchAllBrands, fetchSaleItemById, updateSaleItem } from '../api'
import mockPhone from '@/assets/image/mockPhone.webp'
import BreadCrumb from '@/components/BreadCrumb.vue';
import Button from '@/components/Button.vue';
import { useStatusMessageStore } from '@/stores/statusMessage';

const router = useRouter()
const route =  useRoute()
const { id } = route.params
const saleItem = ref({
    model: '',
    price: '',
    description: '',
    ramGb: '',
    storageGb: '',
    screenSizeInch: '',
    color: '',
    quantity: '',
    brand: {
        id: 0,
        name: ''
    }
})
const brands = ref([])
const isLoading = ref(false)
const isSubmitting = ref(false)
const statusMessageStore = useStatusMessageStore()
let modelName 
let originalSaleItem

const currentFocusField = ref(null)
const isFormValid = ref(false)
const errorFormMessage = reactive({
    'model': '',
    'price': '',
    'description': '',
    'ramGb': '',
    'storageGb': '',
    'screenSizeInch': '',
    'color': '',
    'quantity': '',
})

const fieldIntegrity = {
    'model': {
        checkConstraint: (data) => {
            return 0 < data.length && data.length <= 60
        },
        errorMessage: 'Model must be 1-60 characters long.', 
    },
    'price': {
        checkConstraint: (data) => {
            return 0 <= data && data.length !== 0
        },
        errorMessage: 'Price must be non-negative integer.', 
    },
    'description': {
        checkConstraint: (data) => {
            return 0 < data.length && data.length < 16385
        },
        errorMessage: 'Description must be 1-16,384 characters long.', 
    },
    'ramGb': {
        checkConstraint: (data) => {
            return (data === null || data === '') || 0 < data
        },
        errorMessage: 'RAM size must be positive integer or not specified.', 
    },
    'storageGb': {
        checkConstraint: (data) => {
            return (data === null || data === '') || 0 < data
        },
        errorMessage: 'Storage size must be positive integer or not specified.', 
    },
    'screenSizeInch': {
        checkConstraint: (data) => {
            return (data === null || data === '') || 0 < data && (Math.floor(data * 100) === data * 100)
        },
        errorMessage: 'Screen size must be positive number with at most 2 decimal points or not specified.', 
    },
    'color': {
        checkConstraint: (data) => {
            return data.length <= 40
        },
        errorMessage: 'Color must be 1-40 characters long or not specified.', 
    },
    'quantity': {
        checkConstraint: (data) => {
            return (data === null || data === '') || 0 <= data 
        },
        errorMessage: 'Quantity must be non-negative integer.', 
    },
    'brand': {
        checkConstraint: (data) => {
            return  data.id > 0
        },
        errorMessage: 'Brand must be selected.', 
    },
}


const fetchSaleItem = async () => {
    isLoading.value = true
    const res = await fetchSaleItemById(id)
    if (!res.ok) {
        return router.push('/sale-items/' + id)
    }
    saleItem.value = await res.json()
    saleItem.value.brand = { ...brands.value.find((brand)=> brand.name === saleItem.value.brandName) }
    delete saleItem.value.brandName
    modelName = saleItem.value.model
    originalSaleItem = JSON.stringify(saleItem.value)
    isLoading.value = false
}

const fetchBrands = async () => {
    isLoading.value = true
    const res = await fetchAllBrands()
    if (!res.ok) {
        statusMessageStore.setStatusMessage("Something went wrong")
        return router.push('/sale-items/' + id)
    }
    const brandData = await res.json()
    const sortedBrand = brandData.sort((a, b) => a.name.localeCompare(b.name, undefined, { sensitivity: 'base' }))
    brands.value = sortedBrand
    isLoading.value = false
}

const handleChangeBrand = (e) => {
    currentFocusField.value = e.target.name
}

const handleFocusIn = (e) => {
    currentFocusField.value = e.target.name    
}

const handleFocusOut = (e) => {
    const currentField = e.target.name
    if (typeof saleItem.value[currentField] === 'string') {
        saleItem.value[currentField] = saleItem.value[currentField].trim()
    }
    showErrorToForm()
    currentFocusField.value = null
}

const validateAllField = () => {
    let isValid = true
    for (const field in saleItem.value) {
        if (!checkFieldIntegrity(field) || (JSON.stringify(saleItem.value) === originalSaleItem)){
            isValid = false
            break;
        }
    }
    isFormValid.value = isValid
}

const checkFieldIntegrity = (field) => {
    if (fieldIntegrity[field]) {
        return fieldIntegrity[field]?.checkConstraint(saleItem.value[field])
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

const submitForm = async (e) => {
    e.preventDefault()
    isSubmitting.value = true
    try {
        saleItem.value.brand.name = brands.value.find((brand)=> brand.id === saleItem.value.brand.id)?.name
        const res = await updateSaleItem(id , saleItem.value)
        if (!res.ok) throw new Error("Something went wrong")
        statusMessageStore.setStatusMessage("The sale item has been updated.")
    } catch (err) {
        console.log(err)
        alert("Something went wrong")
    }
    router.push('/sale-items/' + id)
    isSubmitting.value = false
}


const goBackHome = () => {
    router.push('/sale-items/' + id)
}

onMounted(async () => {
    await fetchBrands()
    await fetchSaleItem()
})



watch(saleItem, () => {
    showErrorToForm()
    // Disabled save button
    validateAllField()
}, { deep: true })

</script>


<template>
    <main class="px-16 py-8">
        <BreadCrumb v-if="saleItem" :links="[
            { to: {name: 'SaleItemGallery'}, label: 'Home' },
            { to: `/sale-items/${saleItem.id}`, label: modelName },
            { to: '#', label: 'Edit' },
        ]" />

        <div class="">
            <div v-if="isLoading" class="text-center text-blue-500 animate-pulse text-3xl font-bold">
                Loading...
            </div>

            <div v-else class="itbms-row max-w-6xl mx-auto flex max-lg:flex-col flex-wrap gap-12 bg-white rounded-lg shadow-lg p-6">
                <div class="flex-1">
                    <div class="mb-6 text-center overflow-hidden rounded-lg shadow-md">
                        <img :src="saleItem?.image || mockPhone" alt="sale item"
                            class="w-full h-auto hover:scale-105 transition-transform duration-300" />
                    </div>
                    <div class="grid grid-cols-4 gap-1">
                        <img v-for="image of [1, 2, 3, 4]" :src="saleItem?.image || mockPhone" alt="sale item"
                            class="shadow-md" />
                    </div>
                </div>

                <div class="flex-1 p-3">
                    <form @submit="submitForm" class="flex flex-col gap-3">
                        <div class="flex flex-col gap-1">
                            <label>
                                <span class="text-red-500 text-xl">*</span>
                                Brand
                            </label>
                            <select name="brand" class="itbms-brand input" v-model="saleItem.brand.id" @change="handleChangeBrand">
                                <option :value="''">
                                    {{ 'Select brands' }}
                                </option>
                                <option v-for="brand of brands" :value="brand.id">
                                    {{ brand.name }}
                                </option>
                            </select>
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.brand }}
                            </p>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>
                                <span class="text-red-500 text-xl">*</span>
                                Model
                            </label>
                            <input name="model" type="text" 
                                class="itbms-model input" 
                                placeholder="Model..."
                                v-model="saleItem.model" 
                                @focusin="handleFocusIn"
                                @focusout="handleFocusOut"
                            >
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.model }}
                            </p>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>
                                <span class="text-red-500 text-xl">*</span>
                                Price
                            </label>
                            <input name="price" type="number" 
                                class="itbms-price input" 
                                placeholder="Price..."
                                v-model="saleItem.price" 
                                @focusin="handleFocusIn"
                                @focusout="handleFocusOut"
                            >
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.price }}
                            </p>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>
                                <span class="text-red-500 text-xl">*</span>
                                Description
                            </label>
                            <textarea name="description" class="itbms-description input" 
                                placeholder="Description..."
                                v-model="saleItem.description" 
                                @focusin="handleFocusIn"
                                @focusout="handleFocusOut"
                            >
                            </textarea>
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.description }}
                            </p>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Ram (GM)</label>
                            <input name="ramGb" type="number" 
                                class="itbms-ramGb input" 
                                placeholder="Ram..."
                                v-model="saleItem.ramGb" 
                                @focusin="handleFocusIn"
                                @focusout="handleFocusOut"
                            >
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.ramGb }}
                            </p>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Screen Size (Inches)</label>
                            <input name="screenSizeInch" type="number" 
                                class="itbms-screenSizeInch input" 
                                placeholder="Screen Size..."
                                v-model="saleItem.screenSizeInch" 
                                @focusin="handleFocusIn"
                                @focusout="handleFocusOut"
                            >
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.screenSizeInch }}
                            </p>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Storage (GB)</label>
                            <input name="storageGb" type="number" 
                                class="itbms-storageGb input" 
                                placeholder="Storage..."
                                v-model="saleItem.storageGb" 
                                @focusin="handleFocusIn"
                                @focusout="handleFocusOut"
                            >
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.storageGb }}
                            </p>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Color</label>
                            <input name="color" type="text" 
                                class="itbms-color input" 
                                placeholder="Color..."
                                v-model="saleItem.color" 
                                @focusin="handleFocusIn"
                                @focusout="handleFocusOut"
                            >
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.color }}
                            </p>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Quantity</label>
                            <input name="quantity" type="number" 
                                class="itbms-quantity input" 
                                placeholder="Quantity..."
                                v-model="saleItem.quantity" 
                                @focusin="handleFocusIn"
                                @focusout="handleFocusOut"
                            >
                            <p class="itbms-message text-red-500 pl-2">
                                {{ errorFormMessage.quantity }}
                            </p>
                        </div>

                        <div class="flex gap-3 items-center mt-5">
                            <Button variant="primary" :onclick="submitForm" :disabled="isSubmitting || !isFormValid"
                                class-name="itbms-save-button" type="submit">
                                {{ isSubmitting ? 'Loading...' : 'Save' }}
                            </Button>

                            <Button variant="ghost" :onclick="goBackHome" type="button" class-name="itbms-cancel-button">
                                Cancel
                            </Button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </main>
</template>