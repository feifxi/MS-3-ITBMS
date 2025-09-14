<script setup>
import { ref, computed, onMounted } from 'vue'
import { deleteBrand, fetchBrandById } from '@/api/index.js'
import { useStatusMessageStore } from '@/stores/statusMessage'
import Button from '@/components/Button.vue'
import Confirmodal from './ConfirmModal.vue'
import {Trash2,Pencil,MapPin,Link2 } from 'lucide-vue-next';

const props = defineProps({
  brand: Object,
  saleItems: Array
})

const statusMessageStore = useStatusMessageStore()
const showConfirmDialog = ref(false)
const isAbleToDelete = ref(false)
const isDeleting = ref(false)




const emit = defineEmits(['deleted']) // เพิ่ม

const confirmDelete = async () => {
  try {
    isDeleting.value = true
    const res = await deleteBrand(props.brand.id)
    if (res.ok) {
      statusMessageStore.setStatusMessage(`The brand has been deleted.`, true)
      emit('deleted', props.brand.id)  
    } else throw new Error("fail to delete")
  } catch (err) {
    console.error(err)
    statusMessageStore.setStatusMessage(`Failed to delete "${props.brand.name}".`, false)
  } finally {
    showConfirmDialog.value = false
    isDeleting.value = false
  }
}

const handleDeleteBrand = async () => {
  try {
    const res = await fetchBrandById(props.brand.id)
    if (!res.ok) throw new Error('Unexpected response')

    const brand = await res.json()
    if (brand.noOfSaleItems == 0) {
      isAbleToDelete.value = true
    } else {
      isAbleToDelete.value = false
    }
    showConfirmDialog.value = true
  } catch (err) {
    console.error(err)
    statusMessageStore.setStatusMessage(`Failed to delete "${props.brand.name}".`, false)
  }
}


</script>

<template>
  <div class="border-b bg-white last:border-none hover:bg-gray-50 transition">
    <!-- Confirm Modal -->
    <Confirmodal
      v-if="showConfirmDialog"
      :title="isAbleToDelete ? 'Delete Confirmation' : 'Cannot Delete'"
      :message="isAbleToDelete
        ? `Do you want to delete ${props.brand.name} brand?`
        : `Delete ${props.brand.name} is not allowed. There are sale items with ${props.brand.name} brand.`"
      :button-label="'Delete'"
      @confirm="confirmDelete"
      @cancel="showConfirmDialog = false"
      :is-disabled="!isAbleToDelete"
    />

    <!-- Desktop View -->
    <div class="itbms-row hidden md:grid grid-cols-8 gap-3 items-center p-4 text-center text-sm">
      <div class="itbms-id break-words whitespace-normal overflow-hidden px-1 font-bold">{{ props.brand.id }}</div>
      <div class="itbms-name font-medium text-gray-900 break-words whitespace-normal overflow-hidden px-1">
        {{ props.brand.name }}
      </div>
      <div class="col-span-2 break-words whitespace-normal overflow-hidden px-1 text-blue-600 underline">
        <a :href="props.brand.websiteUrl" target="_blank">{{ props.brand.websiteUrl || '-' }}</a>
      </div>
      <div class="break-words whitespace-normal overflow-hidden px-1">
        <span :class="props.brand.isActive ? 'text-green-600' : 'text-red-600'">
          {{ props.brand.isActive ? 'Active' : 'Inactive' }}
        </span>
      </div>
      <div class="break-words whitespace-normal overflow-hidden px-1">{{ props.brand.countryOfOrigin || '-' }}</div>
      <div class="col-span-2 flex justify-center gap-2 flex-wrap">
        <RouterLink :to="`/brands/${props.brand.id}/edit`">
          <Button variant="secondary"  class=" itbms-edit-button" ><Pencil /></Button>
        </RouterLink>
        <Button class=" itbms-delete-button" variant="destructive" :onclick="handleDeleteBrand" :disabled="isDeleting">
          <Trash2 />  
        </Button>
      </div>
    </div>



    <!-- Mobile View -->
    <div class="md:hidden bg-gradient-to-br from-white to-gray-50 border border-gray-200 rounded-2xl p-5 shadow-md hover:shadow-lg transition-all duration-300 hover:scale-[1.02]">
      <!-- Header with ID and Status -->
      <div class="flex justify-between items-start mb-4">
        <div class="flex items-center gap-2">
          <div class="w-2 h-2 rounded-full bg-indigo-500"></div>
          <span class="text-xs font-medium text-gray-500 uppercase tracking-wide">ID</span>
          <span class="itbms-id text-sm font-bold text-gray-800 bg-gray-100 px-2 py-1 rounded-lg">{{ props.brand.id }}</span>
        </div>
        <div class="flex items-center gap-1">
          <div :class="props.brand.isActive ? 'w-2 h-2 rounded-full bg-green-500' : 'w-2 h-2 rounded-full bg-red-500'"></div>
          <span :class="props.brand.isActive ? 'text-green-700 bg-green-50 border border-green-200' : 'text-red-700 bg-red-50 border border-red-200'" class="text-xs font-semibold px-2 py-1 rounded-full">
            {{ props.brand.isActive ? 'Active' : 'Inactive' }}
          </span>
        </div>
      </div>

      <!-- Brand Name -->
      <div class="mb-4">
        <h3 class="text-lg font-bold text-gray-900 break-words leading-tight">
          {{ props.brand.name }}
        </h3>
      </div>

      <!-- Details Grid -->
      <div class="space-y-3 mb-5">
        <!-- Country -->
        <div class="flex items-center gap-3 p-3 bg-white rounded-xl border border-gray-100">
          <div class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
            <svg class="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <MapPin />
            </svg>
          </div>
          <div>
            <p class="text-xs text-gray-500 font-medium">Country of Origin</p>
            <p class="text-sm font-semibold text-gray-800">{{ props.brand.countryOfOrigin || 'Not specified' }}</p>
          </div>
        </div>

        <!-- Website -->
        <div class="flex items-center gap-3 p-3 bg-white rounded-xl border border-gray-100">
          <div class="w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center">
            <svg class="w-4 h-4 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <Link2 />
            </svg>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-xs text-gray-500 font-medium">Website</p>
            <a v-if="props.brand.websiteUrl" 
              :href="props.brand.websiteUrl" 
              target="_blank" 
              class="text-sm font-semibold text-blue-600 hover:text-blue-800 truncate block transition-colors duration-200 hover:underline">
              {{ props.brand.websiteUrl }}
            </a>
            <p v-else class="text-sm font-semibold text-gray-400">Not available</p>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="flex gap-3 pt-2 border-t border-gray-100">
        <RouterLink :to="`/brands/${props.brand.id}/edit`" class="flex-1">
          <Button variant="secondary" class="w-full flex items-center justify-center gap-2 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border-indigo-200 transition-all duration-200">
            <Pencil class="w-4 h-4"/>
            <span class="font-medium">Edit</span>
          </Button>
        </RouterLink>
        <Button 
          class="itbms-delete-button flex items-center justify-center gap-2 px-4 hover:scale-105 transition-all duration-200" 
          variant="destructive" 
          :onclick="handleDeleteBrand" 
          :disabled="isDeleting">
          <Trash2 class="w-4 h-4"/>
          <span class="font-medium">Delete</span>
        </Button>
      </div>
    </div>
<!--      
    <div class=" grid md:hidden grid-cols-1 gap-2 text-sm border rounded-xl p-4 shadow-sm">
      <div><span class="font-semibold">ID:</span> <span class="itbms-id">{{ props.brand.id }}</span></div>
      <div class="break-words whitespace-normal overflow-hidden">
        <span class="font-semibold">Name:</span> <span class=""></span> {{ props.brand.name }}
      </div>
      <div class="break-words whitespace-normal overflow-hidden">
        <span class="font-semibold">Country:</span> {{ props.brand.countryOfOrigin || '-' }}
      </div>
      <div class="break-words whitespace-normal overflow-hidden">
        <span class="font-semibold">Status:</span>
        <span :class="props.brand.isActive ? 'text-green-600' : 'text-red-600'">
          {{ props.brand.isActive ? 'Active' : 'Inactive' }}
        </span>
      </div>
      <div class="whitespace-nowrap overflow-hidden text-ellipsis">
        <span class="font-semibold">Website:</span>
        <a :href="props.brand.websiteUrl" target="_blank" class="text-blue-600 underline block">
          {{ props.brand.websiteUrl || '-' }}
        </a>
      </div>
      <div class="flex justify-start gap-2 flex-wrap pt-2">
        <RouterLink :to="`/brands/${props.brand.id}/edit`">
          <Button variant="secondary"><Pencil/></Button>
        </RouterLink>
        <Button class="itbms-delete-button" variant="destructive" size="sm" :onclick="handleDeleteBrand" :disabled="isDeleting">
          <Trash2 />  
        </Button>
      </div>
    </div> -->

  </div>
</template>
