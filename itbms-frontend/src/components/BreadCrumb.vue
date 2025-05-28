<script setup>
import { ChevronRight } from 'lucide-vue-next';
import { RouterLink } from 'vue-router'

defineProps({
    links: Object
})


</script>

<template>
    <div class="flex items-center font-bold pb-4 text-sm sm:text-xl overflow-x-auto">
        <div v-for="(link, index) of links" class="flex items-center whitespace-nowrap" :key="index">
            <div v-if="index === links.length-1" class="text-gray-800">
                {{ link.label }}
            </div>
            <RouterLink v-else :to="link.to" class="text-rose-500 hover:text-rose-600 transition-colors" :class="{
                'itbms-home-button': index === 0,
                'itbms-back-button': index === 1,
            }">
                {{ link.label }}
            </RouterLink>
            <ChevronRight v-if="index !== links.length-1" class="mx-1 sm:mx-2 flex-shrink-0 w-4 h-4 sm:w-5 sm:h-5" />
        </div>
    </div>
</template>

<!-- <template>
    <div class="flex items-center font-bold pb-4 sm:text-xl">
        <div v-for="(link, index) of links" class="flex">
            <div v-if="index === links.length-1" class="">
                {{ link.label }}
            </div>
            <RouterLink v-else :to="link.to" class="text-rose-500" :class="{
                'itbms-home-button': index === 0,
                'itbms-back-button': index === 1,
            }">
                {{ link.label }}
            </RouterLink>
            <ChevronRight v-if="index !== links.length-1" />
        </div>
    </div>
</template> -->