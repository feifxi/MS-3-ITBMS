<script setup>
import { useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";
import { createOrderItems } from "@/api";
import { useCartStore } from "@/stores/cart";
import placeHolder from '@/assets/placeholder.svg' 

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore()
const cartStore = useCartStore()

const BASE_API = import.meta.env.VITE_BASE_API
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/images/"

const isLoading = ref(false)

const increaseQuantity = (item) => {
  cartStore.addToCart(item)
}

const decreaseQuantity = (item) => {
  cartStore.decreaseQuantity(item.id)
}

const handlePlaceOrder = async () => { 
  try {   
    if (cartStore.items.length === 0) {
      statusMessageStore.setStatusMessage('No items in cart', false)
      return
    } 
    // the order will be grouped by sellerId
    const groupedOrderItems = cartStore.items.reduce((acc, item) => {
      if (!acc[item.sellerId]) {
        acc[item.sellerId] = [];
      }
      acc[item.sellerId].push({...item});
      return acc;
    }, {});
    
    const orders = []
    for (const sellerId in groupedOrderItems) {
      orders.push({
        sellerId: parseInt(sellerId),
        buyerId: auth.user.id,
        orderItems: groupedOrderItems[sellerId].map(item => ({
          saleItemId: item.id,
          quantity: item.quantity
        })),
        orderDate: new Date().toISOString(),
        orderNote: "hello",
        shippingAddress: "Askaban",
        orderStatus: "PENDING"
      })
    }
    // console.log(orders)

    const res = await createOrderItems(orders, auth)
    const result = await res.json()
    console.log(result)
    if (!res.ok) throw new Error("Something went wrong")

    cartStore.clearCart()
    statusMessageStore.setStatusMessage("Order success.")
    // router.push({ name: "order" })
  } catch (err) {
    console.log(err)
    statusMessageStore.setStatusMessage("Something went wrong.", false)
  }
}

onMounted(async () => {

})

</script>

<template>
  <!-- Update my cart UI  -->
  <div class="px-16 py-8">
    <div class="bg-white px-16 py-8 rounded-xl shadow">
         <div class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-6 sm:p-8 mb-6 border-4 border-pink-100 backdrop-blur-md">
          <h1 class="text-3xl sm:text-4xl font-extrabold text-rose-500 text-center tracking-widest drop-shadow-sm">
            🛍️ Shopping Cart
          </h1>
        </div>
        <div v-if="isLoading" class="text-purple-400">
          Loading...
        </div>
        <div v-else-if="!cartStore.items || cartStore.items.length === 0" class="text-center text-purple-400 font-bold text-3xl">
          No items in cart
        </div>
        <div v-else>
          <div class="max-w-4xl mx-auto">
          <!-- Cart Items -->
          <div class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-6 sm:p-8 border-4 border-pink-100 backdrop-blur-md">
            <ul class="space-y-4">
              <li 
                v-for="item in cartStore.items" 
                :key="item.id" 
                class="bg-gradient-to-r from-pink-50 to-purple-50 border-2 border-pink-200 rounded-2xl p-4 sm:p-6 flex flex-col sm:flex-row items-center gap-4 shadow-lg hover:shadow-xl transition-all duration-300"
              >
          <!-- Product Image -->
            <div class="flex-shrink-0">
              <img 
                :src="item.saleItemImages[0] ? `${IMAGE_ENDPOINT}${item.saleItemImages[0].fileName}` : placeHolder" 
                alt="product"
                class="w-32 h-32 sm:w-40 sm:h-40 object-cover rounded-xl border-4 border-white shadow-md" 
                @error="event => event.target.src = placeHolder"
              />
            </div>

            <!-- Product Info -->
            <div class="flex-1 text-center sm:text-left">
              <p class="text-lg sm:text-xl">
                <span class="font-extrabold text-purple-700">{{ item.brandName }}</span>
              </p>
              <p class="text-base sm:text-lg text-gray-700 mt-1">{{ item.model }}</p>
              <p class="text-xl sm:text-2xl font-bold text-rose-500 mt-2">
                ฿{{ (item.price * item.quantity).toFixed(2) }}
              </p>
            </div>

            <!-- Quantity Controls -->
            <div class="flex items-center gap-3">
              <button
                @click="decreaseQuantity(item)"
                class="cursor-pointer w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-r from-rose-400 to-pink-400 text-white rounded-full flex items-center justify-center text-2xl font-bold shadow-lg hover:from-rose-500 hover:to-pink-500 transition-all duration-300 hover:scale-110"
              >
                -
              </button>
              <div class="w-12 sm:w-16 h-10 sm:h-12 bg-white border-2 border-purple-300 rounded-full flex items-center justify-center text-lg sm:text-xl font-bold text-purple-700 shadow-inner">
                {{ item.quantity }}
              </div>
              <button
                @click="increaseQuantity({...item})"
                class="cursor-pointer w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-r from-purple-400 to-rose-400 text-white rounded-full flex items-center justify-center text-2xl font-bold shadow-lg hover:from-purple-500 hover:to-rose-500 transition-all duration-300 hover:scale-110"
              >
                +
              </button>
            </div>
          </li>
        </ul>

        <!-- Summary Section -->
        <div class="mt-8 bg-gradient-to-r from-purple-50 to-pink-50 border-4 border-pink-200 rounded-2xl p-6 sm:p-8 shadow-xl">
          <div class="space-y-4">
            <div class="flex justify-between items-center pb-4 border-b-2 border-pink-200">
              <span class="text-lg sm:text-xl font-semibold text-purple-700">Total Items:</span>
              <span class="text-2xl sm:text-3xl font-extrabold text-rose-500">{{ cartStore.totalItems }}</span>
            </div>
            
            <div class="flex justify-between items-center pb-6 border-b-2 border-pink-200">
              <span class="text-xl sm:text-2xl font-bold text-purple-700">Total Price:</span>
              <span class="text-3xl sm:text-4xl font-extrabold text-rose-500">฿{{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>

            <!-- Place Order Button -->
                  <button 
                  @click="handlePlaceOrder" 
                  class="cursor-pointer w-full bg-gradient-to-r from-pink-400 to-rose-400 text-white px-8 py-4 rounded-full text-xl sm:text-2xl font-bold shadow-2xl hover:from-purple-400 hover:to-purple-500 transition-all duration-300 hover:scale-105 drop-shadow-[0_2px_2px_rgba(0,0,0,0.8)] mt-4"
                  >
                  🎉 Place Order Now
                </button>
              </div>
            </div>
          </div>
        </div>
        </div>
    </div>
  </div>
</template>
