import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useStatusMessageStore = defineStore('statusMessage', () => {
  const isShowing = ref(false)
  const isSuccess = ref(false)
  const statusMessage = ref('')

  function setStatusMessage(message='', success=true) {    
    isShowing.value = true
    statusMessage.value = message
    isSuccess.value = success

    setTimeout(() => {
      isShowing.value = false
    }, 5000)
  }

  return { isShowing, isSuccess, statusMessage, setStatusMessage }
})