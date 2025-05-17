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
    if (res.status === 200) {
      statusMessageStore.setStatusMessage(`"${props.brand.name}" brand has been deleted.`, true)
      emit('deleted', props.brand.id)  // แจ้งไปยัง parent ว่าลบสำเร็จ
    } else if (res.status === 404) {
      statusMessageStore.setStatusMessage('An error has occurred, the status does not exist.', false)
    } else {
      throw new Error('Unexpected response')
    }
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
  <div class="itbms-row">
    <!-- Confirm Modal -->
    <Confirmodal v-if="showConfirmDialog" 
      :title="isAbleToDelete ? 'Delete Confirmation' : 'Cannot Delete'" 
      :message="
        isAbleToDelete
        ? `Do you want to delete ${props.brand.name}`
        : `Delete ${props.brand.name} is not allowed. There are sale items associative with this brand.`" :button-label="'Delete'"
      @confirm="confirmDelete" 
      @cancel="showConfirmDialog = false" 
      :is-disabled="!isAbleToDelete" />

    <div class="grid grid-cols-8 items-center gap-3 border p-4 rounded shadow-sm">
      <div class="itbms-id col-span-1">{{ props.brand.id }}</div>
      <div class="itbms-name col-span-2 font-semibold">{{ props.brand.name }}</div>
      <div class="itbms-websiteUrl col-span-3 text-blue-600 underline">
        <a :href="props.brand.websiteUrl" target="_blank">{{ props.brand.websiteUrl }}</a>
      </div>
      <div class="itbms-isActive col-span-1">{{ props.brand.isActive }}</div>

      <div class="itbms-countryOfOrigin col-span-1">{{ props.brand.countryOfOrigin }}</div>
      <div class="col-span-1 flex gap-1">
        <RouterLink :to="`/brands/${props.brand.id}/edit`">
          <Button class="itbms-edit-button" variant="secondary">E</Button>
        </RouterLink>
        <Button class="itbms-delete-button" variant="destructive" :onclick="handleDeleteBrand" :disabled="isDeleting">
          D
        </Button>
      </div>
    </div>
  </div>
</template>
