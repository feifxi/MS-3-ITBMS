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
        <h1 class=" text-3xl font-bold">Cart</h1>
        <div v-if="isLoading">Loading...</div>
        <div v-else-if="!cartStore.items || cartStore.items.length === 0">No items in cart</div>
        <div v-else>
          <!-- DO UI HERE ... ทำตรงนี้นะ -->
          <ul>
            <li v-for="item in cartStore.items" :key="item.id" class="border p-3 flex items-center">
              <img 
                  :src="item.saleItemImages[0] ? `${IMAGE_ENDPOINT}${item.saleItemImages[0].fileName}` : placeHolder" 
                  alt="product"
                  class="size-20" 
                  @error="event => event.target.src = placeHolder"
              />
              <p class="mx-auto"><span class="font-bold">{{ item.brandName }}</span> {{ item.model }}</p>
              <div class="grid grid-cols-3 border rounded-lg">
                <div @click="decreaseQuantity(item)" class="size-10 cursor-pointer flex items-center justify-center">
                  -
                </div>
                <div class="flex items-center justify-center">
                  {{ item.quantity }}
                </div>
                <div @click="increaseQuantity({...item})" class="flex items-center justify-center cursor-pointer">
                  +
                </div>
              </div>
            </li>
          </ul>
          <!-- Summary and order button -->
          <div class=" mt-5 border max-w-[300px] ml-auto p-4 rounded-sm">
            <p class="mt-4 text-xl font-bold">Total items: {{ cartStore.totalItems }}</p>
            <p class="mt-4 text-xl font-bold">Total price: Bath {{ cartStore.totalPrice.toFixed(2) }}</p>
            <button @click="handlePlaceOrder" class="bg-blue-500 text-white px-4 py-2 rounded-lg mt-4 hover:bg-blue-600">Place Order</button>
          </div>

        </div>
    </div>
  </div>
</template>
