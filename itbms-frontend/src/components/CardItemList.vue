<script setup>
import { ref , computed, onMounted} from 'vue'
import { useRouter } from 'vue-router'
import { deleteSaleItem } from '@/api/index.js'
import { useStatusMessageStore } from '@/stores/statusMessage'
import Button from '@/components/Button.vue'
import DeleteConfirmModal from './ConfirmModal.vue'
import { formatNumber } from "../libs/utils.js";

const props = defineProps({ item: Object })
const router = useRouter()
const statusMessageStore = useStatusMessageStore()
const showConfirmDialog = ref(false)
const isDeleting = ref(false)
const emit = defineEmits(['deleted'])

const confirmDelete = async () => {
  try {
    isDeleting.value = true
    const res = await deleteSaleItem(props.item.id)
    if (!res.ok) throw new Error('Failed')
    statusMessageStore.setStatusMessage('The sale item has been deleted.', true)
    emit('deleted', props.item.id)  // แจ้งไปยัง parent
  } catch (err) {
    console.error(err)
    statusMessageStore.setStatusMessage(`Failed to delete "${props.item.model}".`, false)
  } finally {
    showConfirmDialog.value = false
    isDeleting.value = false
  }
}
</script>

<template>
  <DeleteConfirmModal
    v-if="showConfirmDialog"
    :title="'Delete Confirmation'"
    :message="`Do you want to delete this sale item?`"
    :button-label="'Delete'"
    @confirm="confirmDelete"
    @cancel="showConfirmDialog = false"
  />
  
  <!-- Desktop View -->
  <div class="hidden md:grid grid-cols-10 gap-3 items-center border p-4 rounded-xl shadow-sm text-sm">
    <div class="truncate">{{ props.item.id }}</div>
    <div class="truncate">{{ props.item.brandName }}</div>
    <div class="truncate col-span-2">{{ props.item.model }}</div>
    <div class="truncate">{{ props.item.ramGb || '-' }}</div>
    <div class="truncate">{{ props.item.storageGb || '-' }}</div>
    <div class="truncate">{{ props.item.color || '-' }}</div>
    <div class="truncate">{{ formatNumber(props.item.price) }}</div>
    <div class="col-span-1 flex gap-2">
      <RouterLink :to="`/sale-items/${props.item.id}/edit`">
        <Button variant="secondary" size="sm">Edit</Button>
      </RouterLink>
      <Button
        variant="destructive"
        size="sm"
        :onclick="() => (showConfirmDialog = true)"
        :disabled="isDeleting"
      >
        Delete
      </Button>
    </div>
  </div>

  <!-- Mobile View -->
  <div class="md:hidden grid grid-cols-1 gap-2 text-sm border rounded-xl p-4 shadow-sm">
    <div><span class="font-semibold">ID:</span> {{ props.item.id }}</div>
    <div><span class="font-semibold">Brand:</span> {{ props.item.brandName }}</div>
    <div><span class="font-semibold">Model:</span> {{ props.item.model }}</div>
    <div><span class="font-semibold">RAM:</span> {{ props.item.ramGb || '-' }}</div>
    <div><span class="font-semibold">Storage:</span> {{ props.item.storageGb || '-' }}</div>
    <div><span class="font-semibold">Color:</span> {{ props.item.color || '-' }}</div>
    <div><span class="font-semibold">Price:</span> {{ formatNumber(props.item.price) }}</div>
    <div class="flex gap-2 pt-2 flex-wrap">
      <RouterLink :to="`/sale-items/${props.item.id}/edit`">
        <Button variant="secondary" size="sm">Edit</Button>
      </RouterLink>
      <Button
        variant="destructive"
        size="sm"
        :onclick="() => (showConfirmDialog = true)"
        :disabled="isDeleting"
      >
        Delete
      </Button>
    </div>
  </div>
</template>