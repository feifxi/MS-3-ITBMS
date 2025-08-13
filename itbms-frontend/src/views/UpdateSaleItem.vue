<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { fetchAllBrands, fetchSaleItemById, updateSaleItem } from '../api'
import mockPhone from '@/assets/image/mockPhone.webp'
import BreadCrumb from '@/components/BreadCrumb.vue';
import Button from '@/components/Button.vue';
import { useStatusMessageStore } from '@/stores/statusMessage';
import { ChevronDown, ChevronUp, X } from 'lucide-vue-next';

const BASE_API = import.meta.env.VITE_BASE_API
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/pictures/"

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
let originalSaleItemImages

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
    const saleItemData = await res.json()
    saleItem.value = saleItemData
    saleItem.value.brand = { ...brands.value.find((brand)=> brand.name === saleItem.value.brandName) }
    delete saleItem.value.brandName
    modelName = saleItem.value.model
    originalSaleItem = JSON.stringify(saleItem.value)
    // fetch image
    await fetchImageFile(saleItemData.saleItemImages)
    saleItemImageNames.value = saleItemData.saleItemImages
    // console.log(saleItemImageNames.value)
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
        if (
            !checkFieldIntegrity(field) || 
            (JSON.stringify(saleItem.value) === originalSaleItem) && (JSON.stringify(saleItemImageFiles.value) === originalSaleItemImages)
        ){
            isValid = false
            break;
        }
    }
    if (saleItemImageFiles.value.length == 0) {
        isValid = false;
    }
    isFormValid.value = isValid
}

const checkFieldIntegrity = (field) => {
    if (fieldIntegrity[field] && saleItem.value[field]) {
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
        // parse sale item filed to formdata.
        const formData = new FormData();
        for (const field in saleItem.value) {
            if (field != "brand" && saleItem.value[field] !== "" && saleItem.value[field] != null) {
                formData.append(field, saleItem.value[field]);
            }
        }
        formData.append("brandId", saleItem.value.brand.id)
        // parse image of saleitem to formdata
        for (const image of saleItemImageFiles.value) {
            formData.append("images", image.file);
            formData.append("isNewImageList", image.newImage);
            if (!image.newImage) {
                formData.append("keptImageNames", image.file.name);  // keep the old image
            }
        }
        // formData.forEach((value, key) => {
        //   console.log(key, " : ", value);
        // });

        const res = await updateSaleItem(id, formData)
        const result = await res.json()
        // console.log(result)
        if (!res.ok) throw new Error("Something went wrong")
        statusMessageStore.setStatusMessage("The sale item has been updated.")
    } catch (err) {
        console.log(err)
        statusMessageStore.setStatusMessage("Something went wrong.", false)
    }
    router.push('/sale-items/' + id) 
    isSubmitting.value = false
}


const goBackHome = () => {
    router.push('/sale-items/' + id)
}


// Image Upload
const selectedImageIndex = ref(0)   // current selected image 
const saleItemImageFiles = ref([])  // store list of fetched file and upload file object
const saleItemImageNames = ref([])  // store list of filename and original filename

const fetchImageFile = async (images) => {
    for (let image of images) {
        const filename = image.fileName
        const res = await fetch(IMAGE_ENDPOINT + filename);
        if (!res.ok) {
        throw new Error(`Failed to fetch image: ${response.statusText}`);
        }
        const blob = await res.blob();
        // Create File object from Blob
        const file = new File([blob], filename, { type: blob.type });
        saleItemImageFiles.value = [
            ...saleItemImageFiles.value, 
            {
                file: file,
                preview: URL.createObjectURL(file),
                newImage: false,
            }
        ]
    }
    originalSaleItemImages = JSON.stringify(saleItemImageFiles.value)
}

const handleFileSelect = (e) => {
    const files = e.target.files ? Array.from(e.target.files) : [];
    const MAX_MB_UNIT = 2 * 1024 * 1024
    const validFileSize = files.every((f) => f.size <= MAX_MB_UNIT);
    if (!validFileSize) {
      statusMessageStore.setStatusMessage("Some files exceed 2MB.", false)
      return
    }
    let updatedImages = [
      ...saleItemImageFiles.value,
      ...files.map((file) => ({
        file: file,
        preview: URL.createObjectURL(file),
        newImage: true,
      })),
    ];

    if (updatedImages.length > 4) {
        statusMessageStore.setStatusMessage("Maximum 4 pictures are allowed.", false)
        updatedImages = updatedImages.slice(0, 4)
    }

    saleItemImageFiles.value = updatedImages
    e.target.value = "";
    // console.log(saleItemImageFiles.value)
};

const handleRemoveImage = (filename) => {
    saleItemImageFiles.value = saleItemImageFiles.value.filter((image) => image.file.name != filename)
}

const handleChangeOrderUp = (index) => {
    if (index == 0) return
    const prevFile = saleItemImageFiles.value[index - 1]
    saleItemImageFiles.value[index - 1] = saleItemImageFiles.value[index]
    saleItemImageFiles.value[index] = prevFile
}

const handleChangeOrderDown = (index) => {
    if (index == saleItemImageFiles.length - 1) return
    const nextFile = saleItemImageFiles.value[index + 1]
    saleItemImageFiles.value[index + 1] = saleItemImageFiles.value[index]
    saleItemImageFiles.value[index] = nextFile
}

const handleChangeSelectedImage = (index) => {
    selectedImageIndex.value = index
}

const mappedToOriginalFileName = (fileName) => {
    const image = saleItemImageNames.value.find((image) => image.fileName === fileName)
    return image?.originalFileName || fileName
}

onMounted(async () => {
    await fetchBrands()
    await fetchSaleItem()
})

watch([saleItem, saleItemImageFiles], () => {
    showErrorToForm()
    // Disabled save button
    validateAllField()
}, { deep: true })

</script>


<template>
    <main class="px-4 sm:px-16 py-8">
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
                <div class="flex-1 flex flex-col gap-4">
                    <div class="text-center overflow-hidden rounded-lg shadow-md">
                        <img 
                            :src="saleItemImageFiles[selectedImageIndex]?.preview || '/placeholder.svg'" 
                            alt="sale item"
                            class="w-full h-auto hover:scale-105 transition-transform duration-300" />
                    </div>
                    <div class="grid grid-cols-4 gap-1">
                        <img 
                            v-for="i of [0,1,2,3]" 
                            @click="() => handleChangeSelectedImage(i)"
                            :src="saleItemImageFiles[i]?.preview || '/placeholder.svg'" 
                            alt="sale item" 
                            :class="'shadow-md ' + (saleItemImageFiles.length > 0 && i == selectedImageIndex ? 'border-5 cursor-pointer border-purple-600' : '') " 
                        />
                    </div>
                    <div>
                        <label htmlFor="image-upload" className="itbms-upload-button primary-btn max-w-[150px]">
                            upload image
                        </label>
                        <input
                            id="image-upload"
                            type="file"
                            multiple
                            accept="image/*"
                            @change="handleFileSelect"
                            className="hidden"
                        />
                    </div>
                    <div class="flex flex-col gap-2">
                        <div 
                            v-for="(image, index) in saleItemImageFiles" 
                            class="py-2 rounded-full font-medium transition-all duration-300 flex items-center justify-center gap-2 bg-purple-100 text-purple-600 px-6"
                        >
                            <p :class="`itbms-picture-file${index+1}`">{{ image.newImage ? image.file.name : mappedToOriginalFileName(image.file.name) }}</p>
                            <X  @click="() => handleRemoveImage(image.file.name)"
                                :class="`itbms-picture-file${index+1}-clear hover:text-white ml-auto size-6 cursor-pointer hover:bg-purple-600 transition-all rounded-full`" 
                            />
                            <div class="flex flex-col">
                                <ChevronUp 
                                    v-if="index != 0" 
                                    :class="`itbms-picture-file${index+1}-up cursor-pointer`" 
                                    @click="() => handleChangeOrderUp(index)"/>
                                <ChevronDown 
                                    v-if="index != saleItemImageFiles.length - 1"  
                                    :class="`itbms-picture-file${index+1}-down cursor-pointer`"
                                    @click="() => handleChangeOrderDown(index)"/>
                            </div>
                        </div>
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