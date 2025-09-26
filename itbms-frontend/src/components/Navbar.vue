<script setup>
import { useAuthStore } from '@/stores/auth';
import { Menu } from 'lucide-vue-next';
import { RouterLink, useRouter , useRoute } from 'vue-router'
import Button from './Button.vue';
import { ref, watch } from 'vue';
import ConfirmModal from './ConfirmModal.vue';
import { useCartStore } from '@/stores/cart';

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const cartStore = useCartStore()

// Search Bar
const searchKeyword = ref('') 

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    if (route.name !== 'SaleItemGallery') {
      router.push({
        name: 'SaleItemGallery',
        query: { search: searchKeyword.value.trim() }
      })
    } else {
      router.replace({
        name: 'SaleItemGallery',
        query: { ...route.query, search: searchKeyword.value.trim() }
      })
    }
  } else {
    if (route.name === 'SaleItemGallery' && route.query.search) {
      const newQuery = { ...route.query }
      delete newQuery.search
      router.replace({
        name: 'SaleItemGallery',
        query: newQuery
      })
    }
  }
}
const clearSearch = () => {
  searchKeyword.value = ''
  if (route.name === 'SaleItemGallery' && route.query.search) {
    const newQuery = { ...route.query }
    delete newQuery.search
    router.replace({
      name: 'SaleItemGallery',
      query: newQuery
    })
  }
}

watch(() => route.query.search, (newSearch) => {
  if (newSearch) {
    searchKeyword.value = newSearch
  } else {
    searchKeyword.value = ''
  }
}, { immediate: true })


// Sign out
const showConfirmSignoutDialog = ref(false)

const handleShowSignoutDialog = () => {
  showConfirmSignoutDialog.value = true
}

const handleCloseSignoutDialog = () => {
  showConfirmSignoutDialog.value = false
}

const confirmSignout = async () => {
  await authStore.logout()
  showConfirmSignoutDialog.value = false
  cartStore.clearCart()
  router.push({ name: "login" })
}


</script>

<template>
  <header class="sticky top-0 z-50 bg-gradient-to-r from-purple-500 to-rose-500 text-white">
    <nav class="px-8 sm:px-16">
      <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <RouterLink :to="{ name: 'SaleItemGallery' }">
          <div class="flex items-center gap-2">
            <div class="text-white">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-smartphone"><rect width="14" height="20" x="5" y="2" rx="2" ry="2"/><path d="M12 18h.01"/></svg>
            </div>
            <h1 class="font-bold text-xl">ITBMS-MS3</h1>
          </div>
        </RouterLink>


        <div class="max-sm:hidden flex items-center gap-8">
          <!-- Search bar -->
          <div class="relative">
            <input 
              type="text" 
              placeholder="Search here..." 
              class="input !bg-white !rounded-full text-black"
              v-model="searchKeyword"
              @keyup.enter="handleSearch"
            >
            <button 
              v-if="searchKeyword" 
              @click="clearSearch"
              class="absolute right-10 top-2 text-gray-400 hover:text-gray-600"
            >
             <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-x"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
             </button>
              <button @click="handleSearch" class="absolute right-3 top-2 text-gray-400 hover:text-gray-600">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-search"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
            </button>  
          </div>

          <!-- Cart -->
          <RouterLink :to="{ name: 'cart' }" class="itbms-shopnow">
            <button class="relative cursor-pointer">
              <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-shopping-cart"><circle cx="8" cy="21" r="1"/><circle cx="19" cy="21" r="1"/><path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"/></svg>
              <span v-if="cartStore.items.length > 0" class="cursor-pointer absolute -top-2 -right-2 bg-rose-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                {{ cartStore.totalItems }}
              </span>
            </button>
          </RouterLink>

          <div class="flex gap-4">
            <!-- user profile -->
            <div v-if="authStore.user && authStore.accessToken" class="flex items-center gap-5">
              <RouterLink :to="{ name: 'order' }" >
                <button class="cursor-pointer bg-white text-rose-600 px-6 py-2 rounded-full font-medium hover:bg-opacity-90 transition-all transform hover:scale-105">
                  Orders
                </button>
              </RouterLink>
              <RouterLink v-if="authStore.user.userType === 'SELLER'" :to="{ name: 'SaleItemList' }" class="itbms-shopnow">
                <button class="cursor-pointer bg-white text-rose-600 px-6 py-2 rounded-full font-medium hover:bg-opacity-90 transition-all transform hover:scale-105">
                  Management
                </button>
              </RouterLink>
              <RouterLink :to="{name: 'userProfile'}">
                <button class="cursor-pointer w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center text-black">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-user"><circle cx="12" cy="8" r="5"/><path d="M20 21a8 8 0 0 0-16 0"/></svg>
                </button>
              </RouterLink>

              <Button class-name="ghost-btn" :onclick="handleShowSignoutDialog">
                Sign out
              </Button>
            </div>

            <!-- register/login -->
            <div v-else class="flex gap-2">
              <RouterLink :to="{name: 'login'}">
                <Button class-name="ghost-btn">
                  Sign in
                </Button>
              </RouterLink>
              <RouterLink :to="{name: 'register'}">
                <Button variant="secondary">
                  Sign up
                </Button>
              </RouterLink>
            </div>
          </div>
        </div>
        <div class="sm:hidden">
          <Menu ></Menu>
        </div>
      </div>
    </nav>
  </header>

  <ConfirmModal
    v-if="showConfirmSignoutDialog"
    :title="'Sign out Confirmation'"
    :message="`Are you sure you want to sign out?`"
    :button-label="'Sign out'"
    @confirm="confirmSignout"
    @cancel="handleCloseSignoutDialog"
  />
</template>

<style scoped>
</style>