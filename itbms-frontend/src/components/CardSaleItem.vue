<script setup>
// import { formatNumber } from '@/libs/utils'
import { formatNumber } from "../libs/utils.js";
import { RouterLink } from 'vue-router'
import mockPhone from '@/assets/image/mockPhone.webp'

defineProps({
    item: {
        type: Object,
        required: true
    }
})
</script>

<template>
    <RouterLink :to="`/sale-items/${item.id}`"
        class="itbms-row block bg-white rounded shadow-xl hover:shadow-2xl transition-shadow duration-200 overflow-hidden">
        <img :src="item.image || mockPhone" alt="Product Image " class="w-full h-64 object-cover" />

        <div class="p-4 space-y-2">
            <p class="itbms-brand text-sm text-gray-600">{{ item.brandName || 'No Brand' }}</p>
            <h3 class="text-lg font-bold text-gray-900 itbms-model">{{ item.model }}</h3>
            <p class="text-sm text-gray-700 ">
                <span class="itbms-ramGb">{{ item.ramGb || '-' }}</span> 
                <span>/</span>
                <span class="itbms-storageGb">{{ item.storageGb || '-' }}</span>
                <span class="itbms-storageGb-unit">GB</span>
            </p>
            <p class="text-xl font-semibold text-blue-600 itbms-price">
                {{ formatNumber(item.price) }}
                <span class="text-sm text-gray-600 itbms-price-unit">Baht</span>
            </p>
        </div>
    </RouterLink>
</template>