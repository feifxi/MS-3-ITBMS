<script setup>
import { ref, onMounted, reactive, computed, watch } from "vue";
import { fetchAllSaleItemsBySeller } from "@/api/index.js";
import CardItemList from "@/components/CardItemList.vue";
import Button from "@/components/Button.vue";
import { useRouter } from "vue-router";
import {
  CirclePlus,
  Package,
  ChartNoAxesGantt,
  ArrowDownWideNarrow,
  ArrowUpWideNarrow,
  List,
} from "lucide-vue-next";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const auth = useAuthStore();

const getSessionStorageItem = (key) => {
  return sessionStorage.getItem(key);
};

const persistPaginationOptionJSON = getSessionStorageItem("paginationOptionSeller");
const persistPaginationOption = persistPaginationOptionJSON
  ? JSON.parse(persistPaginationOptionJSON)
  : {
      pageNumber: 0,
      size: 10,
    };

const persistSortOptionJSON = getSessionStorageItem("sortOptionSeller");
const persistSortOption = persistSortOptionJSON
  ? JSON.parse(persistSortOptionJSON)
  : {
      sortField: "createdOn",
      sortDirection: null,
    };

const saleItems = ref([]);
const isLoading = ref(true);

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

const fetchSaleItems = async () => {
  try {
    // const res = await fetchAllSaleItems(auth)
    const res = await fetchAllSaleItemsBySeller(
      auth,
      pagination.page,
      pagination.size,
      sortOption.sortField,
      sortOption.sortDirection
    );

    if (!res.ok) throw new Error("Failed to fetch");
    const data = await res.json();
    // console.log(data)
    saleItems.value = data.content;
    pagination.first = data.first;
    pagination.last = data.last;
    pagination.totalPages = data.totalPages;
  } catch (err) {
    console.error(err);
    saleItems.value = [];
  } finally {
    isLoading.value = false;
  }
};

// Sorting
const handleSortDefault = () => {
  pagination.page = 0;
  sortOption.sortField = "createdOn";
  sortOption.sortDirection = null;
};

const handleSortAsc = () => {
  pagination.page = 0;
  sortOption.sortField = "brand.name";
  sortOption.sortDirection = "asc";
};

const handleSortDesc = () => {
  pagination.page = 0;
  sortOption.sortField = "brand.name";
  sortOption.sortDirection = "desc";
};

// Pagination
const handleChangePage = (pageNumber) => {
  pagination.page = pageNumber;
  fetchSaleItems();
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

function goToManageBrand() {
  router.push("/brands");
}

function handleDeleted(deletedItemId) {
  saleItems.value = saleItems.value.filter((item) => item.id !== deletedItemId);
}

const saveSessionData = () => {
  sessionStorage.setItem(
    "sortOptionSeller",
    JSON.stringify({
      sortField: sortOption.sortField,
      sortDirection: sortOption.sortDirection,
    })
  );

  sessionStorage.setItem(
    "paginationOptionSeller",
    JSON.stringify({
      pageNumber: pagination.page,
      size: pagination.size,
    })
  );
};

const redirectIfNotSeller = () => {
  if (auth.user?.userType !== "SELLER") {
    router.push({ name: "SaleItemGallery" });
  }
};

onMounted(async () => {
  redirectIfNotSeller();
  await fetchSaleItems();
});

watch(
  () => pagination.page,
  () => {
    saveSessionData();
    fetchSaleItems();
  }
);

watch(
  [() => pagination.size, sortOption],
  () => {
    pagination.page = 0;
    saveSessionData();
    fetchSaleItems();
  },
  { deep: true }
);
</script>

<template>
  <main class="p-4 md:p-8 max-w-7xl mx-auto">
    <div
      class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6"
    >
      <!-- Heading -->
      <h1
        class="flex items-center gap-2 text-2xl md:text-3xl font-bold text-gray-800 justify-center md:justify-start"
      >
        <Package />
        <span>Your Sale Items</span>
      </h1>

      <div class="flex gap-2 flex-wrap justify-center md:justify-end">
        <RouterLink to="/sale-items/add">
          <Button variant="primary" class="itbms-sale-item-add">
            <CirclePlus />
            Add Sale Item
          </Button>
        </RouterLink>
        <Button
          @click="goToManageBrand"
          variant="secondary"
          class="itbms-manage-brand"
        >
          <ChartNoAxesGantt />
          Manage Brand
        </Button>
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
    <div
      v-else-if="saleItems.length === 0"
      class="text-center text-gray-500 text-lg"
    >
      No sale item found.
    </div>

    <!-- Sale Item List -->
    <div v-else class="overflow-x-auto rounded-lg">
      <!-- Page Size & Sorting Option -->
      <div class="p-3 rounded-xl mb-4 bg-white">
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
                  'itbms-brand-none paginationBtn !bg-rose-50 border border-white',
                  {
                    '!bg-rose-200': sortOption.sortField === 'createdOn',
                  },
                ]"
              >
                <List />
              </button>

              <button
                @click="handleSortAsc"
                :class="[
                  'itbms-brand-asc paginationBtn !bg-rose-50 border border-white',
                  {
                    '!bg-rose-200':
                      sortOption.sortDirection === 'asc' &&
                      sortOption.sortField === 'brand.name',
                  },
                ]"
              >
                <ArrowUpWideNarrow />
              </button>

              <button
                @click="handleSortDesc"
                :class="[
                  'itbms-brand-desc paginationBtn !bg-rose-50 border border-white',
                  {
                    '!bg-rose-200':
                      sortOption.sortDirection === 'desc' &&
                      sortOption.sortField === 'brand.name',
                  },
                ]"
              >
                <ArrowDownWideNarrow />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Headings (Desktop Only) -->
      <div
        class="hidden md:grid grid-cols-10 gap-3 bg-gradient-to-r from-rose-200 to-rose-100 p-4 font-semibold text-center"
      >
        <div>ID</div>
        <div>Brand</div>
        <div class="col-span-2">Model</div>
        <div>RAM</div>
        <div>Storage</div>
        <div>Color</div>
        <div>Price</div>
        <div class="col-span-2">Actions</div>
      </div>

      <!-- Mobile Header -->
      <div
        class="md:hidden bg-gradient-to-r from-rose-200 to-rose-100 p-4 font-semibold text-gray-700 text-center"
      >
        Sale Items ({{ saleItems.length }})
      </div>

      <!-- Sale Item Rows -->
      <CardItemList
        v-for="item in saleItems"
        :key="item.id"
        :item="item"
        @deleted="handleDeleted"
      />

      <!-- Pagination -->
      <div
        :v-show="paginatedPages.length > 1"
        class="p-4 mt-5 flex gap-3 rounded-xl bg-white justify-center text-white font-bold"
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
  </main>
</template>
