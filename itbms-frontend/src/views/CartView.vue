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
const shippingAddress = ref("")
const orderNote = ref("")

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

    if (!shippingAddress.value.trim()) {
      statusMessageStore.setStatusMessage('Please enter shipping address', false)
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
        orderNote: orderNote.value || "",
        shippingAddress: shippingAddress.value,
        orderStatus: "PENDING"
      })
    }

    const res = await createOrderItems(orders, auth)
    const result = await res.json()
    
    console.log(result)
    if (!res.ok) throw new Error("Something went wrong")

    cartStore.clearCart()
    shippingAddress.value = ""
    orderNote.value = ""
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
        <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        
        <!-- Cart Items Section -->
        <div class="bg-white rounded-2xl shadow-lg overflow-hidden border-2 border-purple-100 lg:row-span-2">
          <div class="bg-gradient-to-r from-purple-50 to-pink-50 px-6 py-4 border-b-2 border-purple-100">
            <h2 class="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent flex items-center gap-2">
              <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
              </svg>
              Items
            </h2>
          </div>
          
          <div class="p-6 max-h-[550px] overflow-y-auto">
 
            <ul class="space-y-4">
              <li 
                v-for="item in cartStore.items" 
                :key="item.id" 
                class="bg-gradient-to-br from-purple-50/50 to-pink-50/50 border-2 border-purple-100 rounded-xl p-4 hover:shadow-2xl hover:scale-105 transition-all duration-300 hover:border-purple-300"
              >
                <div class="flex items-center gap-4">
                  <!-- Product Image -->
                  <img 
                    :src="item.saleItemImages[0] ? `${IMAGE_ENDPOINT}${item.saleItemImages[0].fileName}` : placeHolder" 
                    alt="product"
                    class="w-20 h-20 sm:w-24 sm:h-24 object-cover rounded-xl border-2 border-purple-200 shadow-md flex-shrink-0" 
                    @error="event => event.target.src = placeHolder"
                  />

                  <!-- Product Info -->
                  <div class="flex-1 min-w-0">
                    <p class="font-bold text-gray-800 mb-1 line-clamp-2">
                      <span class="text-purple-700">{{ item.brandName }}</span> {{ item.model }}
                    </p>
                    <p class="text-xl font-bold text-rose-500 mb-2">
                      ฿{{ (item.price * item.quantity).toFixed(2) }}
                    </p>
                    
                    <!-- Quantity Controls -->
                    <div class="flex items-center gap-3">
                      <button
                        @click="decreaseQuantity(item)"
                        class="cursor-pointer w-8 h-8 bg-gradient-to-r from-rose-400 to-pink-400 text-white rounded-full flex items-center justify-center text-xl font-bold shadow-lg hover:from-rose-500 hover:to-pink-500 transition-all duration-300 hover:scale-110"
                      >
                        -
                      </button>
                      <div class="w-12 h-8 bg-white border-2 border-purple-300 rounded-full flex items-center justify-center text-base font-bold text-purple-700 shadow-inner">
                        {{ item.quantity }}
                      </div>
                      <button
                        @click="increaseQuantity({...item})"
                        class="cursor-pointer w-8 h-8 bg-gradient-to-r from-purple-400 to-rose-400 text-white rounded-full flex items-center justify-center text-xl font-bold shadow-lg hover:from-purple-500 hover:to-rose-500 transition-all duration-300 hover:scale-110"
                      >
                        +
                      </button>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>

        <!-- Delivery Details Card -->
        <div class="bg-white rounded-2xl shadow-lg overflow-hidden border-2 border-purple-100">
          <div class="bg-gradient-to-r from-purple-50 to-pink-50 px-6 py-4 border-b-2 border-purple-100">
            <h2 class="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent flex items-center gap-2">
              <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
              </svg>
              Delivery Details
            </h2>
          </div>
          <div class="p-6 space-y-4">

            <!-- Shipping Address -->
            <div class="bg-gradient-to-r from-purple-50/50 to-pink-50/50 p-5 rounded-xl border-2 border-purple-100 shadow-sm">
              <div class="flex items-start gap-4 mb-3">
                <div class="bg-gradient-to-r from-purple-600 to-pink-600 rounded-full p-2.5 flex-shrink-0 shadow-md">
                  <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                  </svg>
                </div>
                <div class="flex-1">
                  <label class="text-sm text-gray-600 font-bold mb-2 block">Shipping Address *</label>
                  <textarea
                    v-model="shippingAddress"
                    placeholder="Enter your shipping address..."
                    rows="3"
                    class="w-full px-4 py-3 border-2 border-purple-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-400 focus:border-transparent resize-none font-medium text-gray-800"
                  ></textarea>
                </div>
              </div>
            </div>
            
            <!-- Order Note -->
            <div class="bg-gradient-to-r from-purple-50/50 to-pink-50/50 p-5 rounded-xl border-2 border-purple-100 shadow-sm">
              <div class="flex items-start gap-4 mb-3">
                <div class="bg-gradient-to-r from-pink-600 to-purple-600 rounded-full p-2.5 flex-shrink-0 shadow-md">
                  <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"></path>
                  </svg>
                </div>
                <div class="flex-1">
                  <label class="text-sm text-gray-600 font-bold mb-2 block">Order Note</label>
                  <textarea
                    v-model="orderNote"
                    placeholder="Additional instructions or requests..."
                    rows="3"
                    class="w-full px-4 py-3 border-2 border-purple-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-400 focus:border-transparent resize-none font-medium text-gray-800"
                  ></textarea>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Order Summary Card -->
        <div class="bg-gradient-to-r from-purple-600 to-pink-600 rounded-2xl shadow-2xl overflow-hidden border-2 border-purple-400">
          <div class="p-6 sm:p-8">
            <div class="space-y-4 mb-6">
              <div class="flex justify-between items-center pb-4 border-b-2 border-white/30">
                <span class="text-purple-100 text-base font-bold">Total Items:</span>
                <span class="text-2xl font-extrabold text-white">{{ cartStore.totalItems }}</span>
              </div>
              
              <div class="flex justify-between items-center pb-4 border-b-2 border-white/30">
                <span class="text-purple-100 text-lg font-bold">Total Price:</span>
                <span class="text-4xl font-extrabold text-white drop-shadow-lg">฿{{ cartStore.totalPrice.toFixed(2) }}</span>
              </div>
            </div>

            <!-- Place Order Button -->
            <button 
              @click="handlePlaceOrder" 
              class="cursor-pointer w-full bg-white text-purple-600 px-8 py-4 rounded-full text-xl font-bold shadow-2xl hover:bg-purple-50 transition-all duration-300 hover:scale-105 flex items-center justify-center gap-2"
            >
              <span>🎉 Place Order Now</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
