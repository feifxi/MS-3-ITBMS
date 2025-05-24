<script setup>
import { ChevronRight } from 'lucide-vue-next';
import { RouterLink } from 'vue-router'

defineProps({
    links: Object
})


</script>

<template>
    <div class="flex items-center font-bold pb-4 text-xl">
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
</template>