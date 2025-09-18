<script setup>
import { CircleCheckBig, CircleX } from 'lucide-vue-next';
import { useStatusMessageStore } from '@/stores/statusMessage';
const statusMessageStore = useStatusMessageStore()
</script>

<template>
    <div 
        :class="[
            'z-50 fixed right-10 -bottom-100 backdrop-blur-sm rounded-xl overflow-hidden border border-gray-100 shadow-lg font-medium text-base flex gap-3 items-center transition-all duration-700',
            statusMessageStore.isSuccess ? 'bg-white/90' : 'bg-white/90',
            {
                'bottom-10 transform hover:scale-105 transition-all': statusMessageStore.isShowing 
            }
        ]">
        <div :class="[
            'h-full w-2',
            statusMessageStore.isSuccess ? 'bg-gradient-to-b from-rose-500 to-purple-600' : 'bg-red-500'
        ]"></div>
        <div class="flex items-center p-4">
            <div :class="['flex items-center justify-center w-10 h-10 rounded-full mr-3']">
                <CircleCheckBig v-if="statusMessageStore.isSuccess" class="size-6 text-green-500" />
                <CircleX v-else class="size-6 text-red-500" />
            </div>
            <p class="itbms-message">{{ statusMessageStore.statusMessage }}</p>
        </div>
    </div>
</template>

<style scoped>
</style>