// stores/cart.js
import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { useStatusMessageStore } from './statusMessage'

export const useCartStore = defineStore('cart', () => {
    // Initialize from localStorage or empty array
    const items = ref(JSON.parse(localStorage.getItem('cart')) || [])

    // Watch for changes and save to localStorage
    watch(items, (newItems) => {
        localStorage.setItem('cart', JSON.stringify(newItems))
    }, { deep: true })

    function addToCart(saleItem, quantity = 1) {
        const statusMessageStore = useStatusMessageStore()

        if (saleItem.quantity <= 0) {
            const statusMessageStore = useStatusMessageStore()
            statusMessageStore.setStatusMessage('Item is out of stock', false)
            return
        }
        
        const existing = items.value.find(i => i.id === saleItem.id)
        if (existing) {
            if (existing.quantity + quantity > existing.availableQuantity) {
                statusMessageStore.setStatusMessage(`Cannot add more than available quantity (${existing.availableQuantity})`, false)
                return
            }

            existing.quantity += quantity
        } else {
            items.value.push({ ...saleItem, availableQuantity: saleItem.quantity, quantity: quantity })
        }
        statusMessageStore.setStatusMessage('Item added to cart')
        // console.log('Adding to cart:', saleItem, 'Quantity:', quantity)
        // console.log(items.value)
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

    function updateItem(saleItemId, newPrice, newAvailableQuantity) {
        // Remove item if no longer available
        if (newAvailableQuantity <= 0) {
            removeFromCart(saleItemId)
            return
        }
        // Update item details
        items.value = items.value.map(item => {
            if (item.id === saleItemId) {
                const newData = {
                    ...item,
                    price: newPrice, 
                    availableQuantity: newAvailableQuantity,
                    quantity: item.quantity > newAvailableQuantity ? newAvailableQuantity : item.quantity,
                }
                // console.log('Updating item:', {...item}, 'to', newData)
                return newData
            } else {
                return item
            }
        })
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
        updateItem,
        totalItems,
        totalPrice,
    }
})
