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
const isDataValid = ref(false)
const isLoading = ref(false)
const isSubmitting = ref(false)
const statusMessageStore = useStatusMessageStore()
let modelName 
let saleItemResponseModel

const requiredField = [
    "model",
    "price",
    "description",
    "brand",
]

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
    saleItemResponseModel = JSON.stringify(saleItem.value)
    isLoading.value = false
}

const fetchBrands = async () => {
    isLoading.value = true
    const res = await fetchAllBrands()
    if (!res.ok) {
        statusMessageStore.setStatusMessage("Something went wrong")
        return router.push('/sale-items/' + id)
    }
    brands.value = await res.json()
    isLoading.value = false
}


// Validate Require field
const validateData = () => {
    isDataValid.value = true
    for (const field in saleItem.value) {
        if (
            (requiredField.includes(field) && !saleItem.value[field]) || 
            (typeof saleItem.value[field] === 'number' && saleItem.value[field] <= 0) ||
            (field === 'model' && saleItem.value[field].length > 60) || 
            (JSON.stringify(saleItem.value) === saleItemResponseModel)
        ) {
            isDataValid.value = false
        }
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

const trimmedData = (e) => {
    saleItem.value[e.target.name] = saleItem.value[e.target.name].trim()
}

const goBackHome = () => {
    router.push('/sale-items/' + id)
}

onMounted(async () => {
    await fetchBrands()
    await fetchSaleItem()
})

watch(saleItem, () => {
    validateData()
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

            <div v-else class="itbms-row flex max-lg:flex-col flex-wrap gap-12 bg-white rounded-lg shadow-lg p-6">
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
                            <select class="itbms-brand input" v-model="saleItem.brand.id">
                                <option v-for="brand of brands" :value="brand.id">
                                    {{ brand.name }}
                                </option>
                            </select>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>
                                <span class="text-red-500 text-xl">*</span>
                                Model
                            </label>
                            <input name="model" type="text" class="itbms-model input" placeholder="Model..."
                                v-model="saleItem.model" @focusout="trimmedData">
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>
                                <span class="text-red-500 text-xl">*</span>
                                Price
                            </label>
                            <input name="price" type="number" class="itbms-price input" placeholder="Price..."
                                v-model="saleItem.price">
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>
                                <span class="text-red-500 text-xl">*</span>
                                Description
                            </label>
                            <textarea name="description" class="itbms-description input" placeholder="Description..."
                                v-model="saleItem.description" @focusout="trimmedData"></textarea>
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Ram (GM)</label>
                            <input name="ramGb" type="number" class="itbms-ramGb input" placeholder="Ram..."
                                v-model="saleItem.ramGb">
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Screen Size (Inches)</label>
                            <input name="screenSizeInch" type="number" class="itbms-screenSizeInch input" placeholder="Screen Size..."
                                v-model="saleItem.screenSizeInch">
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Storage (GB)</label>
                            <input name="storageGb" type="number" class="itbms-storageGb input" placeholder="Storage..."
                                v-model="saleItem.storageGb">
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Color</label>
                            <input name="color" type="text" class="itbms-color input" placeholder="Color..."
                                v-model="saleItem.color" @focusout="trimmedData">
                        </div>

                        <div class="flex flex-col gap-1">
                            <label>Quantity</label>
                            <input name="quantity" type="number" class="itbms-quantity input" placeholder="Quantity..."
                                v-model="saleItem.quantity">
                        </div>

                        <div class="flex gap-3 items-center mt-5">
                            <Button variant="primary" :onclick="submitForm" :disabled="isSubmitting || !isDataValid"
                                class-name="itbms-save-button" type="submit">
                                {{ isSubmitting ? 'Loading...' : 'Save' }}
                            </Button>

                            <Button variant="secondary" :onclick="goBackHome" type="button" class-name="itbms-cancel-button">
                                Cancel
                            </Button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </main>
</template>