<script setup>
import { RouterLink, useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";
import { fetchUserProfile, getOrderItems, updateUserProfile } from "@/api";
import { useCartStore } from "@/stores/cart";
import placeHolderImage from "@/assets/placeholder.svg";

const BASE_API = import.meta.env.VITE_BASE_API;
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/images/";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore();
const cartStore = useCartStore();

const orders = ref([]);
const isLoading = ref(false);

const fetchOrders = async () => {
  try {
    isLoading.value = true;
    const res = await getOrderItems(auth);
    if (!res.ok) {
      throw new Error("Failed to fetch orders");
    }
    const ordersRes = await res.json();
    orders.value = ordersRes;
    // console.log(ordersRes)
  } catch (error) {
    console.error("Failed to load orders:", error);
    statusMessageStore.setStatusMessage("Failed to load orders", false);
  } finally {
    isLoading.value = false;
  }
};

onMounted(async () => {
  await fetchOrders();
});
</script>

<template>
  <div
    class="px-4 sm:px-16 py-8 bg-gradient-to-br from-purple-50 via-pink-50 to-white min-h-screen"
  >
    <div
      class="bg-white px-4 sm:px-16 py-8 rounded-2xl shadow-2xl max-w-7xl mx-auto border border-purple-100"
    >
      <!-- Header -->
      <div class="mb-8 pb-6 border-b-2 border-purple-200">
        <div class="flex items-center justify-center gap-3 mb-3">
          <div
            class="bg-gradient-to-r from-purple-500 to-pink-500 rounded-full p-3"
          >
            <svg
              class="w-8 h-8 text-white"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
              ></path>
            </svg>
          </div>
          <h1
            class="text-4xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent"
          >
            Your Orders
          </h1>
        </div>
        <p class="text-center text-gray-600">Track and manage your purchases</p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="text-center py-16">
        <div
          class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-200 border-t-purple-600 mb-4"
        ></div>
        <p class="text-gray-600 text-lg">Loading your orders...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="!orders || orders.length === 0" class="text-center py-16">
        <div
          class="bg-gradient-to-r from-purple-100 to-pink-100 rounded-full w-32 h-32 flex items-center justify-center mx-auto mb-6"
        >
          <svg
            class="w-16 h-16 text-purple-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
            ></path>
          </svg>
        </div>
        <h3 class="text-2xl font-bold text-gray-700 mb-2">No Orders Yet</h3>
        <p class="text-gray-500 mb-6">Start shopping to see your orders here</p>
        <RouterLink
          :to="{ name: 'SaleItemGallery' }"
          class="inline-block px-8 py-3 bg-gradient-to-r from-purple-600 to-pink-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-pink-700 transition-all shadow-lg hover:shadow-xl"
        >
          Start Shopping
        </RouterLink>
      </div>

      <!-- Orders List -->
      <div v-else class="space-y-4">
        <RouterLink
          v-for="order in orders"
          :key="order.id"
          :to="{ name: 'orderDetail', params: { id: order.orderId } }"
          class="block bg-white rounded-xl shadow-lg hover:shadow-2xl transition-all duration-300 hover:scale-[1.01] overflow-hidden border-2 border-purple-100 hover:border-purple-300"
        >
          <div
            class="flex flex-col sm:flex-row items-start gap-4 p-6 bg-gradient-to-r from-purple-50/50 to-pink-50/50"
          >
            <!-- Order Info -->
            <div
              class="flex-1 min-w-0 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 w-full"
            >
              <div class="flex-1 min-w-0 text-center sm:text-left">
                <div
                  class="flex items-center justify-center sm:justify-start gap-3 mb-2 flex-wrap"
                >
                  <h3
                    class="text-lg font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent"
                  >
                    Order #{{ order.orderId }}
                  </h3>
                  <span
                    class="px-3 py-1 rounded-full text-xs font-semibold border-2"
                    :class="{
                      'bg-yellow-100 text-yellow-800 border-yellow-300':
                        order.orderStatus === 'PENDING',
                      'bg-green-100 text-green-800 border-green-300':
                        order.orderStatus === 'COMPLETED',
                      'bg-red-100 text-red-800 border-red-300':
                        order.orderStatus === 'CANCELLED',
                      'bg-blue-100 text-blue-800 border-blue-300':
                        order.orderStatus === 'PROCESSING',
                      'bg-gray-100 text-gray-800 border-gray-300': ![
                        'PENDING',
                        'COMPLETED',
                        'CANCELLED',
                        'PROCESSING',
                      ].includes(order.orderStatus),
                    }"
                  >
                    {{ order.orderStatus }}
                  </span>
                </div>

                <p class="text-sm text-gray-600 mb-2 font-medium">
                  {{ new Date(order.orderDate).toLocaleString() }}
                </p>

                <div class="flex gap-2 text-sm text-purple-600 font-medium">
                  <div v-for="(item, index) of order.orderItems" :key="index">
                    <div
                      v-if="index < 2"
                      class="flex gap-3 items-center bg-white rounded-xl p-3 shadow-md border-2 border-purple-100"
                    >
                      <img
                        :src="
                          item.saleItemImage
                            ? `${IMAGE_ENDPOINT}${item.saleItemImage}`
                            : placeHolderImage
                        "
                        alt="Item Image"
                        class="w-15 h-15 object-cover rounded-xl border-2 border-purple-200 shadow-md flex-shrink-0"
                      />
                      <div>
                        <p class="font-semibold">{{ item.saleItemName }}</p>
                        <p>Quantity: {{ item.quantity }}</p>
                      </div>
                    </div>

                    <div
                      v-if="index === 2"
                      class="flex items-center bg-white rounded-xl p-3 shadow-md border-2 border-purple-100"
                    >
                      +{{ order.orderItems.length - 2 }} more
                    </div>
                  </div>
                </div>
              </div>

              <!-- Price -->
              <div
                class="flex-shrink-0 text-center sm:text-right bg-white rounded-xl p-4 shadow-md border-2 border-purple-100"
              >
                <p class="text-xs text-gray-500 font-semibold mb-1">Total</p>
                <p
                  class="text-2xl sm:text-3xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent"
                >
                  ฿{{ order.totalAmount.toFixed(2) }}
                </p>
              </div>
            </div>
          </div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>
