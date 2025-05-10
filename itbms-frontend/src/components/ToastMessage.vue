<script setup>
import { CircleCheckBig, CircleX } from 'lucide-vue-next';
import { useStatusMessageStore } from '@/stores/statusMessage';
const statusMessageStore = useStatusMessageStore()
</script>

<template>
    <div :class="['fixed right-10 -bottom-100 bg-white p-4 min-w-[300px] border border-neutral-300 shadow-xl font-bold rounded-lg flex gap-3 items-center text-xl transition-all duration-700', {
        'bottom-10': statusMessageStore.isShowing 
    }]">
      <CircleCheckBig  v-if="statusMessageStore.isSuccess" class="size-8 text-green-500" />
      <CircleX v-else class="size-8 text-red-500" />
      <p>{{ statusMessageStore.statusMessage }}</p>
    </div>
</template>

<style scoped>
</style>
