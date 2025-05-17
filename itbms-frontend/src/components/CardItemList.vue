<script setup>
import { ref , computed, onMounted} from 'vue'
import { useRouter } from 'vue-router'
import { deleteSaleItem } from '@/api/index.js'
import { useStatusMessageStore } from '@/stores/statusMessage'
import Button from '@/components/Button.vue'
import DeleteConfirmModal from './ConfirmModal.vue'

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
    statusMessageStore.setStatusMessage(`"${props.item.model}" has been deleted.`, true)
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
      :message="`Do you want to ${props.item.model} brand?`"
      :button-label="'Delete'"
      @confirm="confirmDelete"
      @cancel="showConfirmDialog = false"
    />

  <div class="itbms-row grid grid-cols-10 gap-3 items-center border p-4 rounded shadow-sm">
    <div class="itbms-id col-span-1">{{ props.item.id }}</div>
    <div class="itbms-brand col-span-1 ">{{ props.item.brandName }}</div>
    <div class="itbms-model col-span-2 ">{{ props.item.model }}</div>
    <div class="itbms-ramGb col-span-1 ">{{ props.item.ramGb }}</div>
    <div class="itbms-storageGb col-span-1 ">{{ props.item.storageGb }}</div>
    <div class="itbms-color col-span-1 ">{{ props.item.color }}</div>
    <!-- <div class="itbms-screenSizeInch col-span-1 ">{{ props.item.screenSizeInch }}</div> -->
    <div class="itbms-price col-span-1 ">{{ props.item.price }}</div>
    <!-- <div class="itbms-quantity col-span-1 ">{{ props.item.quantity }}</div> -->
    <div class="col-span-1 flex gap-1">
      <RouterLink :to="`/sale-items/${props.item.id}/edit`" class="itbms-edit-button">
        <Button variant="secondary">E</Button>
      </RouterLink>
      <Button
        variant="destructive"
        class="itbms-delete-button"
        :onclick="() => (showConfirmDialog = true)"
        :disabled="isDeleting">
        D
      </Button>
    </div>
  </div>
</template>
