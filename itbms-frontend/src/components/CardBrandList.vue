<script setup>
import { ref, computed, onMounted } from 'vue'
import { deleteBrand } from '@/api/index.js'
import { useStatusMessageStore } from '@/stores/statusMessage'
import Button from '@/components/Button.vue'
import DeleteConfirmModal from './DeleteConfirmModal.vue'

const props = defineProps({
  brand: Object,
  saleItems: Array
})

const statusMessageStore = useStatusMessageStore()
const showConfirmDialog = ref(false)
const showBlockedDialog = ref(false)
const isDeleting = ref(false)

const hasSaleItems = computed(() =>
  props.saleItems.some(item => item.brandId === props.brand.id)
)

const onDeleteClick = () => {
  if (hasSaleItems.value) {
    showBlockedDialog.value = true
  } else {
    showConfirmDialog.value = true
  }
}

const confirmDelete = async () => {
  try {
    isDeleting.value = true
    const res = await deleteBrand(props.brand.id)
    if (res.status === 200) {
      statusMessageStore.setStatusMessage(`"${props.brand.name}" brand has been deleted.`, true)
      window.location.reload()
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
</script>

<template>
  <!-- Confirm Modal -->
  <DeleteConfirmModal
    v-if="showConfirmDialog"
    :item-name="props.brand.name"
    @confirm="confirmDelete"
    @cancel="showConfirmDialog = false"
  />

  <!-- Blocked Modal -->
  <DeleteConfirmModal
    v-if="showBlockedDialog"
    :item-name="props.brand.name"
    cancel-only
    message="Delete {{ props.brand.name }} is not allowed. There are sale items with this brand."
    @cancel="showBlockedDialog = false"
  />

  <div class="grid grid-cols-8 items-center gap-3 border p-4 rounded shadow-sm">
    <div class="col-span-1">{{ props.brand.id }}</div>
    <div class="col-span-2 font-semibold">{{ props.brand.name }}</div>
    <div class="col-span-3 text-blue-600 underline">
      <a :href="props.brand.websiteUrl" target="_blank">{{ props.brand.websiteUrl }}</a>
    </div>
    <div class="col-span-1">{{ props.brand.countryOfOrigin }}</div>
    <div class="col-span-1 flex gap-1">
      <RouterLink :to="`/brands/${props.brand.id}/edit`">
        <Button variant="secondary">E</Button>
      </RouterLink>
      <Button
        variant="destructive"
        :onclick="onDeleteClick"
        :disabled="isDeleting"
      >
        D
      </Button>
    </div>
  </div>
</template>
