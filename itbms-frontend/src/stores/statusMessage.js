import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useStatusMessageStore = defineStore('statusMessage', () => {
  const isShowing = ref(false)
  const isSuccess = ref(false)
  const statusMessage = ref('')
  const currentTimeoutId = ref(null)

  function setStatusMessage(message='', success = true) {    
    if (isShowing && currentTimeoutId) {
      clearTimeout(currentTimeoutId.value);
    }
    isShowing.value = true
    statusMessage.value = message
    isSuccess.value = success

    currentTimeoutId.value = setTimeout(() => {
      isShowing.value = false
      currentTimeoutId.value = null
    }, 5000)
  }

  return { isShowing, isSuccess, statusMessage, setStatusMessage }
})