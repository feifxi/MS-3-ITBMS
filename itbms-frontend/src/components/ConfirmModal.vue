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
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div class="bg-white p-6 rounded-xl shadow-2xl max-w-md w-full text-center animate-fade-in">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">{{ title }}</h2>
      <p class="itbms-message text-gray-600 mb-6">
        {{ message }}
      </p>

      <div class="flex justify-center gap-4">
        <button v-if="!isDisabled"
          class="itbms-confirm-button bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition"
          @click="$emit('confirm')"
        >
          {{ buttonLabel }}
        </button>

        <button 
          class="itbms-cancel-button bg-gray-300 text-gray-800 px-4 py-2 rounded-lg hover:bg-gray-400 transition"
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
  animation: fade-in 0.2s ease-out;
}
</style>
