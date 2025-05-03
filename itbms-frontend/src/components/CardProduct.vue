<script setup>
import { RouterLink } from 'vue-router'
defineProps({
    item:{
    type:Object,
    required:true
    }



})
const formatNumber = (num)=>{
    return new Intl.NumberFormat().format(num)
}
</script>

<template>
    <RouterLink
    :to="`/sale-items/${item.id}`"
    class="itbms-row block bg-white rouded-lg shadow-md hover:shadow-2xl transition-shadow duration-200 overflow-hidden"
    >
    <!-- <img src="item.image || 'htttps://via.placeholder.com/300x300' " 
    alt="Product Image "
    class="w-full h-64 object-cover"/> -->

    <div class="p-4 space-y-2">
        <p class="text-sm text-gray-600 items-brand ">{{ item.brandName || 'No Brand' }}</p>
        <h3 class="text-lg font-bold text-gray-900 itbms-model">{{ item.model }}</h3>
        <p class="text-sm text-gray-700 ">
            <span class="itmbs-ramGb">{{ item.ramGb || '-' }}</span> /
            <span class="itmbs-storageGb">{{ item.storageGb }}</span>
            <span class="itmbs-storageGb-unit">GB</span>
        </p>
        <p class="text-xl font-semibold text-blue-600 itbms-price" >
            {{ formatNumber(item.price) }}
            <span class="text-sm text-gray-600 itbms-price-unit">Baht</span>
        </p>
    </div>
</RouterLink>
</template>