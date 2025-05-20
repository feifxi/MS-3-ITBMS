<script setup>
import { ref, computed, onMounted } from 'vue'
import { deleteBrand, fetchBrandById } from '@/api/index.js'
import { useStatusMessageStore } from '@/stores/statusMessage'
import Button from '@/components/Button.vue'
import Confirmodal from './ConfirmModal.vue'

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
  <div class="border-b last:border-none hover:bg-gray-50 transition">
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
    <div class="hidden md:grid grid-cols-8 gap-3 items-center p-4 text-center text-sm">
      <div class="break-words whitespace-normal overflow-hidden px-1">{{ props.brand.id }}</div>
      <div class="font-medium text-gray-900 break-words whitespace-normal overflow-hidden px-1">
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
          <Button variant="secondary">Edit</Button>
        </RouterLink>
        <Button variant="destructive" :onclick="handleDeleteBrand" :disabled="isDeleting">
          Delete
        </Button>
      </div>
    </div>



    <!-- Mobile View -->
    <div class="grid md:hidden grid-cols-1 gap-2 text-sm border rounded-xl p-4 shadow-sm">
      <div><span class="font-semibold">ID:</span> {{ props.brand.id }}</div>
      <div class="break-words whitespace-normal overflow-hidden">
        <span class="font-semibold">Name:</span> {{ props.brand.name }}
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
          <Button variant="secondary" size="sm">Edit</Button>
        </RouterLink>
        <Button variant="destructive" size="sm" :onclick="handleDeleteBrand" :disabled="isDeleting">
          Delete
        </Button>
      </div>
    </div>

  </div>
</template>
