// stores/cart.js
import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'

export const useCartStore = defineStore('cart', () => {
    // Initialize from localStorage or empty array
    const items = ref(JSON.parse(localStorage.getItem('cart')) || [])

    // Watch for changes and save to localStorage
    watch(items, (newItems) => {
        localStorage.setItem('cart', JSON.stringify(newItems))
    }, { deep: true })

    function addToCart(saleItem, quantity = 1) {
        const existing = items.value.find(i => i.id === saleItem.id)
        if (existing) {
            existing.quantity += quantity
        } else {
            items.value.push({ ...saleItem, quantity })
        }
    }

    function removeFromCart(saleItemId) {
        items.value = items.value.filter(i => i.id !== saleItemId)
    }

    function decreaseQuantity(saleItemId) {
        const item = items.value.find(i => i.id === saleItemId)
        if (item) {
            item.quantity -= 1
            if (item.quantity <= 0) {
                removeFromCart(saleItemId)
            }
        }
    }

    function clearCart() {
        items.value = []
    }

    const totalItems = computed(() =>
        items.value.reduce((sum, i) => sum + i.quantity, 0)
    )

    const totalPrice = computed(() =>
        items.value.reduce((sum, i) => sum + i.price * i.quantity, 0)
    )

    return {
        items,
        addToCart,
        removeFromCart,
        decreaseQuantity,
        clearCart,
        totalItems,
        totalPrice,
    }
})
