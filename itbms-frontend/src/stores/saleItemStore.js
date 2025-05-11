import { defineStore } from 'pinia'
import { ref } from 'vue'
import { deleteSaleItem } from '@/api/index.js'
import { useStatusMessageStore } from './statusMessage'
import { useRouter } from 'vue-router'

export const useSaleItemStore = defineStore('saleItem', () => {
  const saleItem = ref(null)
  const statusMessageStore = useStatusMessageStore()
  const router = useRouter()

  const removeSaleItem = async (id) => {
    try {
      const res = await deleteSaleItem(id)
      if (!res.ok) throw new Error('Delete failed')
      statusMessageStore.setStatusMessage('Sale item deleted successfully.', true)
      router.push('/sale-items')
    } catch (err) {
      console.error(err)
      statusMessageStore.setStatusMessage('Failed to delete sale item.', false)
    }
  }

  return {
    saleItem,
    removeSaleItem,
  }
})
