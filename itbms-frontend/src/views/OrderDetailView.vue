<script setup>
import { useRoute, useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";
import {
  getOrderItemsById,
} from "@/api";
import { useCartStore } from "@/stores/cart";
import placeHolderImage from "@/assets/placeholder.svg";
import BreadCrumb from "@/components/BreadCrumb.vue";

const BASE_API = import.meta.env.VITE_BASE_API;
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/images/";

const router = useRouter();
const route = useRoute();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore();
const cartStore = useCartStore();

const orderId = route.params.id;
const order = ref(null);
const isLoading = ref(true);

const fetchOrder = async () => {
  try {
    isLoading.value = true;
    const res = await getOrderItemsById(orderId, auth);
    if (!res.ok) {
      throw new Error("Failed to fetch order");
    }
    const orderRes = await res.json();
    order.value = orderRes;
    // console.log(orderRes);
  } catch (error) {
    console.error("Failed to load order:", error);
    statusMessageStore.setStatusMessage("Failed to load order", false);
  } finally {
    isLoading.value = false;
  }
};

onMounted(async () => {
  await fetchOrder();
});
</script>

<template>
  <div
    class="px-4 sm:px-16 py-8 bg-gradient-to-br from-purple-50 via-pink-50 to-white min-h-screen"
  >
    <div class="max-w-6xl mx-auto">
      <BreadCrumb
        :links="[
          { to: { name: 'order' }, label: 'Orders' },
          { to: '#', label: `Order #${orderId}` },
        ]"
      />
      <!-- Header -->
      <div
        class="bg-white rounded-2xl shadow-2xl mb-8 overflow-hidden border border-purple-100"
      >
        <div
          class="bg-gradient-to-r from-purple-600 to-pink-600 px-6 sm:px-12 py-8"
        >
          <div class="flex items-center justify-center gap-3 mb-2">
            <div class="bg-white/20 backdrop-blur-sm rounded-full p-3">
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
                  d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                ></path>
              </svg>
            </div>
            <h1 class="text-4xl font-bold text-white">Order #{{ orderId }}</h1>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div
        v-if="isLoading"
        class="bg-white rounded-2xl shadow-lg p-16 text-center"
      >
        <div
          class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-200 border-t-purple-600 mb-4"
        ></div>
        <p class="text-gray-600 text-lg">Loading order details...</p>
      </div>

      <!-- Order Content -->
      <div v-else class="space-y-6">
        <!-- Status & Delivery Details (Same Row) -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- Status Card -->
          <div
            class="bg-white rounded-2xl shadow-lg overflow-hidden border-2 border-purple-100"
          >
            <div
              class="bg-gradient-to-r from-purple-50 to-pink-50 px-6 py-4 border-b-2 border-purple-100"
            >
              <h2
                class="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent flex items-center gap-2"
              >
                <svg
                  class="w-6 h-6 text-purple-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                  ></path>
                </svg>
                Status
              </h2>
            </div>
            <div class="p-6 flex gap-3">
              <!-- Order Status -->
              <div class="space-y-4 flex-1">
                <div class="text-center">
                  <p class="text-sm text-gray-600 mb-2 font-semibold">
                    Order Status
                  </p>
                  <span
                    class="inline-block px-6 py-2 rounded-full text-base font-bold border-2 shadow-md"
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
                <div
                  class="bg-gradient-to-br from-purple-50 to-pink-50 rounded-xl p-4 text-center border-2 border-purple-100 shadow-md"
                >
                  <p class="text-sm text-gray-600 mb-1 font-semibold">
                    Order Date
                  </p>
                  <p class="text-gray-800 font-bold">
                    {{ new Date(order.orderDate).toLocaleDateString() }}
                  </p>
                  <p class="text-sm text-gray-600 font-medium">
                    {{ new Date(order.orderDate).toLocaleTimeString() }}
                  </p>
                </div>
              </div>
              <!-- Payment status -->
              <div class="space-y-4 flex-1">
                <div class="text-center">
                  <p class="text-sm text-gray-600 mb-2 font-semibold">
                    Payment Status
                  </p>
                  <span
                    class="inline-block px-6 py-2 rounded-full text-base font-bold border-2 shadow-md"
                    :class="{
                      // 'bg-yellow-100 text-yellow-800 border-yellow-300':
                      //   order.orderStatus === 'PENDING',
                      'bg-green-100 text-green-800 border-green-300':
                        true || order.orderStatus === 'COMPLETED',
                      // 'bg-red-100 text-red-800 border-red-300':
                      //   order.orderStatus === 'CANCELLED',
                      // 'bg-blue-100 text-blue-800 border-blue-300':
                      //   order.orderStatus === 'PROCESSING',
                      'bg-gray-100 text-gray-800 border-gray-300': ![
                        'PENDING',
                        'COMPLETED',
                        'CANCELLED',
                        'PROCESSING',
                      ].includes(order.orderStatus),
                    }"
                  >
                    <!-- {{ order.orderStatus }} -->
                    {{ order.paymentStatus || 'COMPLETED' }}
                  </span>
                </div>
                <div
                  class="bg-gradient-to-br from-purple-50 to-pink-50 rounded-xl p-4 text-center border-2 border-purple-100 shadow-md"
                >
                  <p class="text-sm text-gray-600 mb-1 font-semibold">
                    Payment Date
                  </p>
                  <p class="text-gray-800 font-bold">
                    {{ new Date(order.orderDate).toLocaleDateString() }}
                  </p>
                  <p class="text-sm text-gray-600 font-medium">
                    {{ new Date(order.orderDate).toLocaleTimeString() }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Order Items -->
          <div
            class="bg-white rounded-2xl shadow-lg overflow-hidden border-2 border-purple-100 lg:row-span-2"
          >
            <div
              class="bg-gradient-to-r from-purple-50 to-pink-50 px-6 py-4 border-b-2 border-purple-100"
            >
              <h2
                class="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent flex items-center gap-2"
              >
                <svg
                  class="w-6 h-6 text-purple-600"
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
                Items
              </h2>
            </div>

            <!-- ✅ ฟิกความสูง (เช่น 400px) + ให้เลื่อนเมื่อเกิน -->
            <div class="p-6 max-h-[550px] overflow-y-auto">
              <ul class="grid grid-cols-1 gap-4">
                <li
                  v-for="(item, index) in order.orderItems"
                  :key="index"
                  class="bg-gradient-to-br from-purple-50/50 to-pink-50/50 border-2 border-purple-100 rounded-xl p-4 hover:shadow-2xl hover:scale-105 transition-all duration-300 hover:border-purple-300"
                >
                  <div class="flex items-center gap-4">
                    <img
                      :src="
                        item.saleItemImage
                          ? `${IMAGE_ENDPOINT}${item.saleItemImage}`
                          : placeHolderImage
                      "
                      alt="Item Image"
                      class="w-20 h-20 sm:w-24 sm:h-24 object-cover rounded-xl border-2 border-purple-200 shadow-md flex-shrink-0"
                    />
                    <div class="flex-1 min-w-0 flex flex-col">
                      <RouterLink :to="{ name: 'SaleItemDetail', params: { id: item.saleItemId } }" class="hover:underline font-bold text-gray-800 mb-2 line-clamp-2 text-left">
                         {{ item.saleItemBrand }} - {{ item.saleItemName }}
                      </RouterLink>
                      <div
                        class="inline-block px-4 py-1.5 bg-white border-2 border-purple-200 rounded-lg font-bold text-gray-700 shadow-sm"
                      >
                        <p>Price: ฿{{ item.priceAtPurchase.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}</p>
                        <p>Quantity: {{ item.quantity }}</p>
                        <p>Storage: {{ item.saleItemStorageSize ? item.saleItemStorageSize + "GB" : '-' }}</p>
                        <p>Color: {{ item.saleItemColor || '-' }}</p>
                      </div>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>

          <!-- Delivery Details Card -->
          <div
            class="bg-white rounded-2xl shadow-lg overflow-hidden border-2 border-purple-100"
          >
            <div
              class="bg-gradient-to-r from-purple-50 to-pink-50 px-6 py-4 border-b-2 border-purple-100"
            >
              <h2
                class="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent flex items-center gap-2"
              >
                <svg
                  class="w-6 h-6 text-purple-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                  ></path>
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                  ></path>
                </svg>
                Details
              </h2>
            </div>
            <div class="p-6 space-y-4">
              <!-- Shipping Address -->
              <div
                class="flex items-start gap-4 bg-gradient-to-r from-purple-50/50 to-pink-50/50 p-5 rounded-xl border-2 border-purple-100 shadow-sm"
              >
                <div
                  class="bg-gradient-to-r from-purple-600 to-pink-600 rounded-full p-2.5 flex-shrink-0 shadow-md"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide lucide-user text-white"
                  >
                    <circle cx="12" cy="8" r="5" />
                    <path d="M20 21a8 8 0 0 0-16 0" />
                  </svg>
                </div>
                <div class="flex-1">
                  <p class="text-sm text-gray-600 font-bold mb-1">
                    {{ auth.user.id != order.buyer.id ? 'Buyer' : 'Seller' }}
                  </p>
                  <p class="text-gray-800 font-medium text-base">
                    {{ auth.user.id != order.buyer.id ? order.buyer.nickname : order.seller.shopName }}
                    <!-- {{ order.seller.nickname }} -->
                  </p>
                </div>
              </div>

              <!-- Shipping Address -->
              <div
                class="flex items-start gap-4 bg-gradient-to-r from-purple-50/50 to-pink-50/50 p-5 rounded-xl border-2 border-purple-100 shadow-sm"
              >
                <div
                  class="bg-gradient-to-r from-purple-600 to-pink-600 rounded-full p-2.5 flex-shrink-0 shadow-md"
                >
                  <svg
                    class="w-5 h-5 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                    ></path>
                  </svg>
                </div>
                <div class="flex-1">
                  <p class="text-sm text-gray-600 font-bold mb-1">
                    Shipping Address
                  </p>
                  <p class="text-gray-800 font-medium text-base">
                    {{ order.shippingAddress }}
                  </p>
                </div>
              </div>

              <!-- Order Note (if exists) -->
              <div
                v-if="order.orderNote"
                class="flex items-start gap-4 bg-gradient-to-r from-purple-50/50 to-pink-50/50 p-5 rounded-xl border-2 border-purple-100 shadow-sm"
              >
                <div
                  class="bg-gradient-to-r from-pink-600 to-purple-600 rounded-full p-2.5 flex-shrink-0 shadow-md"
                >
                  <svg
                    class="w-5 h-5 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"
                    ></path>
                  </svg>
                </div>
                <div class="flex-1">
                  <p class="text-sm text-gray-600 font-bold mb-1">Order Note</p>
                  <p class="text-gray-800 font-medium text-base">
                    {{ order.orderNote }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Total Amount -->
        <div
          class="bg-gradient-to-r from-purple-600 to-pink-600 rounded-2xl shadow-2xl overflow-hidden border-2 border-purple-400"
        >
          <div class="p-8">
            <div
              class="flex flex-col sm:flex-row justify-between items-center gap-4"
            >
              <div class="text-center sm:text-left">
                <p class="text-purple-100 text-base font-bold mb-2">Total</p>
                <p class="text-white text-5xl font-bold drop-shadow-lg">
                  ฿{{ order.totalAmount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}
                </p>
              </div>
              <div
                class="bg-white/20 backdrop-blur-sm rounded-full p-5 shadow-xl"
              >
                <svg
                  class="w-14 h-14 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  ></path>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
