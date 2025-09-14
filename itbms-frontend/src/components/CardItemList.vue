<script setup>
import { ref , computed, onMounted} from 'vue'
import { useRouter } from 'vue-router'
import { deleteSaleItem } from '@/api/index.js'
import { useStatusMessageStore } from '@/stores/statusMessage'
import Button from '@/components/Button.vue'
import DeleteConfirmModal from './ConfirmModal.vue'
import { formatNumber } from "../libs/utils.js";
import {Trash2,Pencil,MemoryStick ,Archive,PaintBucket} from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const props = defineProps({ item: Object })
const router = useRouter()
const statusMessageStore = useStatusMessageStore()
const showConfirmDialog = ref(false)
const isDeleting = ref(false)
const emit = defineEmits(['deleted'])

const confirmDelete = async () => {
  try {
    isDeleting.value = true
    const res = await deleteSaleItem(props.item.id, auth)
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
    <div class="itbms-row border-b bg-white hidden md:grid grid-cols-10 gap-3 items-center p-4 text-center text-sm">
      <div class="itbms-id break-words whitespace-normal overflow-hidden px-1 font-bold">{{ props.item.id }}</div>
      <div class="itbms-brand break-words whitespace-normal overflow-hidden px-1">{{ props.item.brandName }}</div>
      <div class="itbms-model col-span-2 break-words whitespace-normal overflow-hidden px-1">{{ props.item.model }}</div>
      <div class="itbms-ramGb break-words whitespace-normal overflow-hidden px-1">{{ props.item.ramGb || '-' }}</div>
      <div class="itbms-storageGb break-words whitespace-normal overflow-hidden px-1">{{ props.item.storageGb || '-' }}</div>
      <div class="itbms-color break-words whitespace-normal overflow-hidden px-1">{{ props.item.color || '-' }}</div>
      <div class="itbms-price break-words whitespace-normal overflow-hidden px-1">{{ formatNumber(props.item.price) }}</div>
      <div class="col-span-2 flex justify-center gap-2 flex-wrap">
        <RouterLink :to="`/sale-items/${props.item.id}/edit`">
          <Button variant="secondary" class="itbms-edit-button"><Pencil /></Button>
        </RouterLink>
        <Button variant="destructive" class="itbms-delete-button" :onclick="() => (showConfirmDialog = true)" :disabled="isDeleting">
          <Trash2 />
        </Button>
      </div>
    </div>

    <!-- Mobile View -->

    
    <div class="md:hidden bg-gradient-to-br from-white to-gray-50 border border-gray-200 rounded-2xl p-5 shadow-md hover:shadow-lg transition-all duration-300 hover:scale-[1.02]">
      <!-- Header with ID -->
      <div class="flex justify-between items-start mb-4">
        <div class="flex items-center gap-2">
          <div class="w-2 h-2 rounded-full bg-emerald-500"></div>
          <span class="text-xs font-medium text-gray-500 uppercase tracking-wide">ID</span>
          <span class="text-sm font-bold text-gray-800 bg-gray-100 px-2 py-1 rounded-lg">{{ props.item.id }}</span>
        </div>
        <div class="bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold px-2 py-1 rounded-full">
          {{ formatNumber(props.item.price) }}
        </div>
      </div>

      <!-- Brand and Model -->
      <div class="mb-4">
        <h3 class="text-lg font-bold text-gray-900 break-words leading-tight">
          {{ props.item.brandName }}
        </h3>
        <p class="text-gray-600 font-medium">{{ props.item.model }}</p>
      </div>

      <!-- Specifications Grid -->
      <div class="grid grid-cols-2 gap-3 mb-5">
        <!-- RAM -->
        <div class="p-3 bg-white rounded-xl border border-gray-100">
          <div class="flex items-center gap-2 mb-1">
            <div class="w-6 h-6 bg-blue-100 rounded-lg flex items-center justify-center">
              <svg class="w-3 h-3 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <MemoryStick />
              </svg>
            </div>
            <p class="text-xs text-gray-500 font-medium">RAM</p>
          </div>
          <p class="text-sm font-bold text-gray-800">{{ props.item.ramGb ? props.item.ramGb + ' GB' : 'N/A' }}</p>
        </div>

        <!-- Storage -->
        <div class="p-3 bg-white rounded-xl border border-gray-100">
          <div class="flex items-center gap-2 mb-1">
            <div class="w-6 h-6 bg-purple-100 rounded-lg flex items-center justify-center">
              <svg class="w-3 h-3 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <Archive />
              </svg>
            </div>
            <p class="text-xs text-gray-500 font-medium">Storage</p>
          </div>
          <p class="text-sm font-bold text-gray-800">{{ props.item.storageGb ? props.item.storageGb + ' GB' : 'N/A' }}</p>
        </div>
      </div>

      <!-- Color -->
      <div class="mb-5">
        <div class="flex items-center gap-3 p-3 bg-white rounded-xl border border-gray-100">
          <div class="w-8 h-8 bg-gradient-to-br from-pink-100 to-orange-100 rounded-lg flex items-center justify-center">
            <svg class="w-4 h-4 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <PaintBucket />
            </svg>
          </div>
          <div>
            <p class="text-xs text-gray-500 font-medium">Color</p>
            <p class="text-sm font-semibold text-gray-800">{{ props.item.color || 'Not specified' }}</p>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="flex gap-3 pt-2 border-t border-gray-100">
        <RouterLink :to="`/sale-items/${props.item.id}/edit`" class="flex-1">
          <Button variant="secondary" class="w-full flex items-center justify-center gap-2 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border-indigo-200 transition-all duration-200">
            <Pencil class="w-4 h-4"/>
            <span class="font-medium">Edit</span>
          </Button>
        </RouterLink>
        <Button 
          class="flex items-center justify-center gap-2 px-4 hover:scale-105 transition-all duration-200" 
          variant="destructive" 
          :onclick="() => (showConfirmDialog = true)" 
          :disabled="isDeleting">
          <Trash2 class="w-4 h-4"/>
          <span class="font-medium">Delete</span>
        </Button>
      </div>
    </div>


    <!-- <div class="md:hidden bg-white grid  grid-cols-1 gap-2 text-sm border rounded-xl p-4 shadow-sm">
      <div><span class="font-semibold">ID:</span> {{ props.item.id }}</div>
      <div><span class="font-semibold">Brand:</span> {{ props.item.brandName }}</div>
      <div><span class="font-semibold">Model:</span> {{ props.item.model }}</div>
      <div><span class="font-semibold">RAM:</span> {{ props.item.ramGb || '-' }}</div>
      <div><span class="font-semibold">Storage:</span> {{ props.item.storageGb || '-' }}</div>
      <div><span class="font-semibold">Color:</span> {{ props.item.color || '-' }}</div>
      <div><span class="font-semibold">Price:</span> {{ formatNumber(props.item.price) }}</div>
      <div class="flex justify-start gap-2 flex-wrap pt-2">
        <RouterLink :to="`/sale-items/${props.item.id}/edit`">
          <Button variant="secondary" size="sm"><Pencil /></Button>
        </RouterLink>
        <Button variant="destructive" size="sm" :onclick="() => (showConfirmDialog = true)" :disabled="isDeleting">
          <Trash2 />
        </Button>
      </div>
    </div> -->
  
</template>