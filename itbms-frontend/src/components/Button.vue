<script setup>
defineProps({
    onclick: Function,
    variant: String,
    type: String,
    disabled: Boolean,
})
</script>

<template>
    <button :class="['cursor-pointer border-2 px-8 py-3 rounded font-bold transition-all duration-300', {
        'border-blue-600 bg-blue-600 text-white hover:bg-blue-700 active:bg-blue-900 disabled:bg-blue-900 disabled:border-blue-900 disabled:cursor-not-allowed': variant === 'primary',
        'border-blue-600 text-blue-600 hover:bg-blue-600 hover:text-white': variant === 'secondary',
        'border-red-600 text-red-600 hover:bg-red-600 hover:text-white': variant === 'destructive',
    }]" :type="type" @click="onclick" :disabled="disabled">
        <slot></slot>
    </button>
</template>