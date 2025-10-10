<script setup>
import { RouterLink, useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { computed, onMounted, reactive, ref, watch } from "vue";
import { getOrderByBuyerId, getOrderBySellerId } from "@/api";
import { useCartStore } from "@/stores/cart";
import placeHolderImage from "@/assets/placeholder.svg";
import { ArrowDownWideNarrow, ArrowUpWideNarrow, List, Store } from "lucide-vue-next";

const BASE_API = import.meta.env.VITE_BASE_API;
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/images/";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore();
const cartStore = useCartStore();
const orderStatus = [
  'PENDING',
  'PAID',
  'SHIPPED',
  'DELIVERED',
  'CANCELLED',
  'RETURNED'
];

const getSessionStorageItem = (key) => {
  return sessionStorage.getItem(key);
};

const lastVisitedTab = getSessionStorageItem("lastVisitedTabOrderView");

const currentTab = ref(
  lastVisitedTab === "SELLER" && auth.user && auth.user.userType === "SELLER"
  ? "SELLER"
  : "BUYER"
); // SELLER or BUYER


// Order Status Filter
const persistSelectedOrderStatusJSON = getSessionStorageItem("selectedOrderStatus");
const persistSelectedOrderStatus = persistSelectedOrderStatusJSON
  ? JSON.parse(persistSelectedOrderStatusJSON)
  : "";
const selectedOrderStatus = ref(persistSelectedOrderStatus);

// Orders Data
const orders = ref([]);
const isLoading = ref(false);

const fetchOrders = async () => {
  try {
    isLoading.value = true;
    const res = await getOrderByBuyerId(
      auth,
      pagination.page,
      pagination.size,
      sortOption.sortField,
      sortOption.sortDirection,
      selectedOrderStatus.value,
    );
    if (!res.ok) {
      throw new Error("Failed to fetch orders");
    }
    const data = await res.json();
    orders.value = data.content;
    pagination.first = data.first;
    pagination.last = data.last;
    pagination.totalPages = data.totalPages;
    // console.log(data);
  } catch (error) {
    console.error("Failed to load orders:", error);
    statusMessageStore.setStatusMessage("Failed to load orders", false);
  } finally {
    isLoading.value = false;
  }
};

const fetchOrdersSellerView = async () => {
  try {
    isLoading.value = true;
    const res = await getOrderBySellerId(
      auth,
      pagination.page,
      pagination.size,
      sortOption.sortField,
      sortOption.sortDirection,
      selectedOrderStatus.value,
    );
    if (!res.ok) {
      throw new Error("Failed to fetch orders");
    }
    const data = await res.json();
    orders.value = data.content;
    pagination.first = data.first;
    pagination.last = data.last;
    pagination.totalPages = data.totalPages;
    // console.log(orders.value)
  } catch (error) {
    console.error("Failed to load orders:", error);
    statusMessageStore.setStatusMessage("Failed to load orders", false);
  } finally {
    isLoading.value = false;
  }
};

// Pagination and Sorting
const persistPaginationOptionJSON = getSessionStorageItem(
  currentTab.value === "BUYER"
    ? "paginationOptionOrderBuyer"
    : "paginationOptionOrderSeller"
);
const persistPaginationOption = persistPaginationOptionJSON
  ? JSON.parse(persistPaginationOptionJSON)
  : {
      pageNumber: 0,
      size: 10,
    };

const persistSortOptionJSON = getSessionStorageItem(
  currentTab.value === "BUYER"
    ? "sortOptionOrderBuyer"
    : "sortOptionOrderSeller"
);
const persistSortOption = persistSortOptionJSON
  ? JSON.parse(persistSortOptionJSON)
  : {
      sortField: "createdOn",
      sortDirection: null,
    };

const pagination = reactive({
  last: false,
  first: false,
  totalPages: 0,
  totalElements: 0,
  page: persistPaginationOption.pageNumber,
  size: persistPaginationOption.size,
});

const sortOption = reactive({
  sortField: persistSortOption.sortField,
  sortDirection: persistSortOption.sortDirection,
});

const paginatedPages = computed(() => {
  const current = pagination.page;
  const total = pagination.totalPages;
  const maxVisible = 10;
  let start = Math.max(current - Math.floor(maxVisible / 2), 1);
  let end = start + maxVisible - 1;
  if (end > total) {
    end = total;
    start = Math.max(end - maxVisible + 1, 1);
  }
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

// Sorting
const handleSortDefault = () => {
  pagination.page = 0;
  sortOption.sortField = "createdOn";
  sortOption.sortDirection = null;
};

const handleSortAsc = () => {
  pagination.page = 0;
  sortOption.sortField = "createdOn";
  sortOption.sortDirection = "asc";
};

const handleSortDesc = () => {
  pagination.page = 0;
  sortOption.sortField = "createdOn";
  sortOption.sortDirection = "desc";
};

// Pagination
const handleChangePage = (pageNumber) => {
  pagination.page = pageNumber;
  currentTab.value === "BUYER" ? fetchOrders() : fetchOrdersSellerView();
};

const handleGoFirst = () => {
  pagination.page = 0;
};

const handleGoLast = () => {
  pagination.page = pagination.totalPages - 1;
};

const handleClickNext = () => {
  if (pagination.page + 1 >= pagination.totalPages) return;
  pagination.page += 1;
};

const handleClickPrev = () => {
  if (pagination.page <= 0) return;
  pagination.page -= 1;
};

const saveSessionData = () => {
  sessionStorage.setItem(
    currentTab.value === "BUYER"
      ? "sortOptionOrderBuyer"
      : "sortOptionOrderSeller",
    JSON.stringify({
      sortField: sortOption.sortField,
      sortDirection: sortOption.sortDirection,
    })
  );

  sessionStorage.setItem(
    "selectedOrderStatus",
    JSON.stringify(selectedOrderStatus.value)
  );

  sessionStorage.setItem(
    currentTab.value === "BUYER"
      ? "paginationOptionOrderBuyer"
      : "paginationOptionOrderSeller",
    JSON.stringify({
      pageNumber: pagination.page,
      size: pagination.size,
    })
  );

  sessionStorage.setItem(
    "lastVisitedTabOrderView",
    currentTab.value === "BUYER" ? "BUYER" : "SELLER"
  );
};

onMounted(async () => {
  if (!auth.user) {
    router.push({ name: "Login" });
    return;
  }
  if (currentTab.value === "BUYER") {
    await fetchOrders();
  } else if (currentTab.value === "SELLER") {
    await fetchOrdersSellerView();
  }
});

watch(
  () => pagination.page,
  () => {
    saveSessionData();
    currentTab.value === "BUYER" ? fetchOrders() : fetchOrdersSellerView();
  }
);

watch(
  [() => pagination.size, sortOption, currentTab, selectedOrderStatus],
  () => {
    pagination.page = 0;
    saveSessionData();
    currentTab.value === "BUYER" ? fetchOrders() : fetchOrdersSellerView();
  },
  { deep: true }
);

// console.log("Auth User:", auth.user);

</script>

<template>
  <div
    class="px-4 sm:px-16 py-8 bg-gradient-to-br from-purple-50 via-pink-50 to-white min-h-screen"
  >
    <!-- Tab Buttons -->
    <div
      class="mb-3 max-w-7xl mx-auto"
      v-if="auth.user && auth.user.userType === 'SELLER'"
    >
      <button
        @click="currentTab = 'BUYER'"
        :class="
          currentTab === 'BUYER'
            ? 'bg-purple-600 text-white'
            : 'bg-purple-100 text-purple-700 hover:bg-purple-200'
        "
        class="px-6 py-2 rounded-l-lg font-semibold transition-colors duration-300"
      >
        BUYER VIEW
      </button>
      <button
        @click="currentTab = 'SELLER'"
        :class="
          currentTab === 'SELLER'
            ? 'bg-purple-600 text-white'
            : 'bg-purple-100 text-purple-700 hover:bg-purple-200'
        "
        class="px-6 py-2 rounded-r-lg font-semibold transition-colors duration-300"
      >
        SELLER VIEW
      </button>
    </div>

    <!-- Page Size & Sorting Option -->
    <div class="p-3 rounded-xl mb-4 bg-white max-w-7xl mx-auto">
      <div class="flex max-md:flex-col gap-5 md:items-start">
        <div class="flex-1 flex justify-start gap-3">
          <div class="flex gap-2 items-center">
            <h3>show</h3>
            <select
              v-model="pagination.size"
              class="itbms-page-size input sm:!w-30"
            >
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20">20</option>
            </select>
          </div>

          <div class="flex">
            <button
              @click="handleSortDefault"
              :class="[
                'itbms-brand-none paginationBtn !bg-purple-50 border border-white',
                {
                  '!bg-purple-200': 
                    sortOption.sortDirection === null &&
                    sortOption.sortField === 'createdOn',
                },
              ]"
            >
              <List />
            </button>

            <button
              @click="handleSortAsc"
              :class="[
                'itbms-brand-asc paginationBtn !bg-purple-50 border border-white',
                {
                  '!bg-purple-200':
                    sortOption.sortDirection === 'asc' &&
                    sortOption.sortField === 'createdOn',
                },
              ]"
            >
              <ArrowUpWideNarrow />
            </button>

            <button
              @click="handleSortDesc"
              :class="[
                'itbms-brand-desc paginationBtn !bg-purple-50 border border-white',
                {
                  '!bg-purple-200':
                    sortOption.sortDirection === 'desc' &&
                    sortOption.sortField === 'createdOn',
                },
              ]"
            >
              <ArrowDownWideNarrow />
            </button>
          </div>

          <div class="flex gap-2 items-center ml-auto">
            <h3>status</h3>
            <select
              v-model="selectedOrderStatus"
              class="input sm:!w-50"
            >
              <option value="">All</option>
              <option v-for="status in orderStatus" :key="status" :value="status">
                {{ status }}
              </option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <div
      class="bg-white px-4 sm:px-16 py-8 rounded-2xl shadow-2xl max-w-7xl mx-auto border border-purple-100"
    >
      <div class="mb-8 pb-6 border-b-2 border-purple-200">
        <!-- Buyer Header -->
        <div v-if="currentTab === 'BUYER'">
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
              My Orders
            </h1>
          </div>
          <p class="text-center text-gray-600">
            Track and manage your purchases
          </p>
        </div>

        <!-- Seller Header -->
        <div v-else-if="currentTab === 'SELLER'">
          <div class="flex items-center justify-center gap-3 mb-3">
            <div
              class="bg-gradient-to-r from-purple-500 to-pink-500 rounded-full p-3"
            >
              <Store class="w-8 h-8 text-white" />
            </div>
            <h1
              class="text-4xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent"
            >
              Customer Orders
            </h1>
          </div>
          <p class="text-center text-gray-600">
            See who ordered your products and track their status.
          </p>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="text-center py-16">
        <div
          class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-200 border-t-purple-600 mb-4"
        ></div>
        <p class="text-gray-600 text-lg">Loading...</p>
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
        <p v-if="currentTab === 'BUYER'" class="text-gray-500 mb-6">
          Start shopping to see your orders here
        </p>
        <RouterLink
          v-if="currentTab === 'BUYER'"
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
                  ฿{{ order.totalAmount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}
                </p>
              </div>
            </div>
          </div>
        </RouterLink>
      </div>
    </div>

    <!-- Pagination -->
    <div
      v-show="paginatedPages.length > 1"
      class="max-w-7xl mx-auto p-4 mt-5 flex gap-3 rounded-xl bg-white justify-center text-white font-bold"
    >
      <button
        @click="handleGoFirst"
        class="itbms-page-first paginationBtn !px-4"
        :disabled="pagination.first"
      >
        First
      </button>
      <button
        @click="handleClickPrev"
        class="itbms-page-prev paginationBtn !px-4"
        :disabled="pagination.first"
      >
        Prev
      </button>

      <button
        v-for="pageIndex in paginatedPages"
        :key="pageIndex"
        :class="[
          `itbms-page-${pageIndex - 1} paginationBtn w-10`,
          {
            '!bg-rose-500': pagination.page + 1 === pageIndex,
          },
        ]"
        @click="() => handleChangePage(pageIndex - 1)"
      >
        {{ pageIndex }}
      </button>

      <button
        @click="handleClickNext"
        class="itbms-page-next paginationBtn !px-4"
        :disabled="pagination.last"
      >
        Next
      </button>
      <button
        @click="handleGoLast"
        class="itbms-page-last paginationBtn !px-4"
        :disabled="pagination.last"
      >
        Last
      </button>
    </div>
  </div>
</template>
