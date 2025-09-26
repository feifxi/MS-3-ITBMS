<script setup>
import { useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";
import { fetchUserProfile, getOrderItems, updateUserProfile } from "@/api";
import { useCartStore } from "@/stores/cart";
import placeHolderImage from '@/assets/placeholder.svg'

const BASE_API = import.meta.env.VITE_BASE_API
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/images/"

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore()
const cartStore = useCartStore()

const orders = ref([])
const isLoading = ref(false)

const fetchOrders = async () => {
  try {
    isLoading.value = true
    const res = await getOrderItems(auth)
    if (!res.ok) {
      throw new Error("Failed to fetch orders")
    }
    const ordersRes = await res.json()
    orders.value = ordersRes
    console.log(ordersRes)
  } catch (error) {
    console.error("Failed to load orders:", error)
    statusMessageStore.setStatusMessage("Failed to load orders", false)
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await fetchOrders()
})

</script>

<template>
  <div class="px-16 py-8">
    <div class="bg-white px-16 py-8 rounded-xl shadow">
        <h1 class=" text-3xl font-bold">Orders</h1>
        <div v-if="isLoading">Loading...</div>
        <div v-else-if="!orders || orders.length === 0">No Orders</div>
        <div v-else>
          <ul class="space-y-4">
            <li v-for="order in orders" :key="order.id" class="border p-3 rounded-lg" >
              <h2 class="text-xl font-semibold mb-2">Order #{{ order.orderId }} - {{ order.orderStatus }}</h2>
              <p class="mb-2">Order Date: {{ new Date(order.orderDate).toLocaleString() }}</p>
              <p class="mb-2">Shipping Address: {{ order.shippingAddress }}</p>
              <p class="mb-2">Order Note: {{ order.orderNote }}</p>
              <h3 class="font-semibold mt-4 mb-2">Items:</h3>
              <ul class="flex gap-3">
                <li v-for="item in order.orderItems" :key="item.id" class="mb-2 border rounded-lg p-2 w-60">
                  <div class="flex items-center">
                    <img 
                      :src="placeHolderImage" 
                      alt="Item Image" 
                      class="w-16 h-16 object-cover mr-4" />
                    <div>
                      <p class="font-semibold">{{ item.saleItemName }}</p>
                      <p>Quantity: {{ item.quantity }}</p>
                    </div>
                  </div>
                </li>
              </ul>
                <p class="mb-2">Total: ฿{{ order.totalAmount.toFixed(2) }}</p>
            </li>
          </ul>
        </div>
    </div>
  </div>
</template>
