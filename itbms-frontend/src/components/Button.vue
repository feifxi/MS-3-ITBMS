<script setup>
defineProps({
    onclick: Function,
    variant: String,
    type: String,
    disabled: Boolean,
    className: String,
})
</script>

<template>
    <button 
        :class="[
            className, 
            'cursor-pointer px-4 py-2 rounded-full font-medium transition-all duration-300 flex items-center justify-center gap-2',
            {
                'bg-gradient-to-r from-rose-500 px-6 to-purple-600 text-white hover:opacity-90 transform hover:scale-105 disabled:opacity-70 disabled:cursor-not-allowed disabled:hover:scale-100': variant === 'primary',
                'bg-purple-100 text-purple-600 px-6 hover:bg-purple-600 hover:text-white': variant === 'secondary',
                'bg-rose-100 text-rose-600 px-6 hover:bg-rose-600 hover:text-white': variant === 'destructive',
                'bg-gray-100 text-gray-600 px-6 py-2 rounded-full hover:bg-gray-200 transition-all': variant === 'ghost',
            }
        ]" 
        :type="type" 
        @click="onclick" 
        :disabled="disabled"
    >
        <slot></slot>
    </button>
</template>