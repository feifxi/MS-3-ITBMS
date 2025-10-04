<script setup>
import Button from './Button.vue';

defineProps({
  title: {
    type: String,
  },
  message: {
    type: String,
  },
  buttonLabel: {
    type: String,
    default: 'confirm'
  },
  isDisabled: {
    type: Boolean
  }
})
const emit = defineEmits(['confirm', 'cancel'])
</script>

<template>
  <div class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
    <div class="bg-white/90 p-6 rounded-2xl shadow-2xl max-w-md w-full text-center animate-fade-in border border-gray-100">
      <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-br from-rose-100 to-purple-100 flex items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-alert-triangle text-rose-600"><path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"/><path d="M12 9v4"/><path d="M12 17h.01"/></svg>
      </div>
      
      <h2 class="text-xl font-bold text-gray-800 mb-3">{{ title }}</h2>
      <p class="itbms-message text-gray-600 mb-6" v-html="message"></p>

      <div class="flex justify-center gap-4">
        <button v-if="!isDisabled"
          class="cursor-pointer itbms-confirm-button bg-gradient-to-r from-rose-500 to-purple-600 text-white px-6 py-2 rounded-full font-medium hover:opacity-90 transition-all transform hover:scale-105"
          @click="$emit('confirm')"
        >
          {{ buttonLabel }}
        </button>

        <button 
          class="cursor-pointer itbms-cancel-button bg-gray-100 text-gray-600 px-6 py-2 rounded-full font-medium hover:bg-gray-200 transition-all"
          @click="$emit('cancel')"
        >
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes fade-in {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-fade-in {
  animation: fade-in 0.3s cubic-bezier(0.21, 1.02, 0.73, 1);
}
</style>