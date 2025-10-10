<script setup>
import { formatNumber } from "../libs/utils.js";
import { RouterLink, useRouter } from 'vue-router'
import { Heart, Plus } from "lucide-vue-next";
import placeHolder from '@/assets/placeholder.svg' 
import { useAuthStore } from "@/stores/auth.js";
import ConfirmModal from "./ConfirmModal.vue";
import { ref } from "vue";
import { useCartStore } from "@/stores/cart.js";
import { useStatusMessageStore } from "@/stores/statusMessage.js";

const BASE_API = import.meta.env.VITE_BASE_API
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/images/"

defineProps({
    item: {
        type: Object,
        required: true
    },
    query: {
      type: String,
      required: false
    }
})

const auth = useAuthStore()
const cartStore = useCartStore()
const router = useRouter()
const statusMessageStore = useStatusMessageStore()

const addToCart = (saleItem) => {
  if (!auth.user) {
    return handleShowDialog()
  } 
  else if (auth.user.id === saleItem.sellerId) {
    return statusMessageStore.setStatusMessage("You cannot add your own item to cart.", false)
  }
  cartStore.addToCart({ ...saleItem })
  // console.log("Add:", saleItem)
  // console.log("Cart items:", cartStore.items)
}

const showLoginSuggestDialog = ref(false);

const handleShowDialog = () => {
  showLoginSuggestDialog.value = true;
};

const handleCloseDialog = () => {
  showLoginSuggestDialog.value = false;
};

const goToSignin = async () => {
  handleCloseDialog();
  router.push({ name: "login" });
};

</script>
    
<template>
    <RouterLink :to="query 
      ? `/sale-items/${item.id}?${query}` 
      : `/sale-items/${item.id}`" 
      class="itbms-row relative"
    >
        <div class="bg-white rounded-xl overflow-hidden border border-gray-100 hover:shadow-lg transition-all    transform hover:-translate-y-1">
          <div class="relative">
            <img 
              :src="item.saleItemImages[0] ? `${IMAGE_ENDPOINT}${item.saleItemImages[0].fileName}` : placeHolder" 
              :alt="item.model" 
              class="w-full h-48 object-cover p-4" 
            />
            <button class="cursor-pointer absolute top-2 right-2 w-8 h-8 rounded-full bg-white/80 backdrop-blur-sm flex items-center justify-center text-rose-500 hover:bg-rose-500 hover:text-white transition-colors">
              <Heart class="size-4" />
            </button>
          </div>
          <div class="p-4">
            <p class="itbms-brand text-sm text-gray-600 mb-2">{{ item.brandName || 'No Brand' }}</p>
            <div class="flex items-center gap-1 mb-1">
              <div class="flex">
                <svg v-for="i in 5" :key="i" xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="i <= product.rating ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-star text-amber-400"><path d="m12 2-2.2 6.6H3l5.5 4-2.1 6.6 5.6-4 5.6 4-2.1-6.6 5.5-4h-6.8Z"/></svg>
              </div>
              <span class="text-xs text-gray-500">{{ 128 }} reviews</span>
            </div>
            <h3 class="itbms-model font-medium text-gray-900">{{ item.model }}</h3>
            <p class="text-sm text-gray-700 mt-2">
                <span class="itbms-ramGb">{{ item.ramGb || '-' }}</span> 
                <span>/</span>
                <span class="itbms-storageGb">{{ item.storageGb || '-' }}</span>
                <span class="itbms-storageGb-unit">GB</span>
            </p>
            <div>
               <span v-if="item.quantity > 0" class="text-xs text-gray-500">{{ String(item.quantity).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }} available</span>
               <span v-else class="text-xs text-gray-500">Out of stock</span>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <span class="itbms-price text-lg font-bold text-rose-600">
                    {{ formatNumber(item.price) }}
                    <span class="itbms-price-unit text-rose-600 text-lg">Baht</span>
                </span>
              </div>
              <button 
                v-if="item.quantity > 0"
                @click.stop.prevent="addToCart({...item})" 
                class="cursor-pointer w-8 h-8 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center hover:bg-rose-600 hover:text-white transition-colors"
              >
                <Plus class="size-4" />
              </button>
            </div>
          </div>
        </div>
    </RouterLink>

    <ConfirmModal
      v-if="showLoginSuggestDialog"
      :title="'Login require'"
      :message="`Login is require before added item to cart`"
      :button-label="'Sign in'"
      @confirm="goToSignin"
      @cancel="handleCloseDialog"
    />
</template>