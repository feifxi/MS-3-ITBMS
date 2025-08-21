import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SaleItemView from '../views/SaleItemGalleryView.vue' 
import SaleItemDetailView from '../views/SaleItemDetailView.vue' 
import CreateSaleItem from '@/views/CreateSaleItem.vue'
import UpdateSaleItem from '@/views/UpdateSaleItem.vue'
import ListSaelItem from '../views/SaleItemListView.vue'
import BrandList from '../views/BrandList.vue'
import BrandEdit from '../views/BrandEdit.vue'
import BrandAdd from '../views/BrandAdd.vue'
import Chat from '@/views/Chat.vue'
import RegisterView from '@/views/RegisterView.vue'
import LoginView from '@/views/LoginView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomeView,
    },
    {
      path: '/sale-items',
      name: 'SaleItemGallery',
      component: SaleItemView,
    },
    {
      path: '/sale-items/:id',
      name: 'SaleItemDetail',
      component: SaleItemDetailView,
    },
    {
      path: '/sale-items/add',
      name: 'AddSaleItem',
      component: CreateSaleItem,
    },
    {
      path: '/sale-items/:id/edit',
      name: 'UpdateSaleItem',
      component: UpdateSaleItem,
    },
    {
      path: '/sale-items/list',
      name: 'SaleItemList',
      component: ListSaelItem,
    },
    {
      path: '/brands',
      name: 'BrandList',
      component: BrandList,
    },
    {
      path: '/brands/:id/edit',
      name: 'UpdateBrand',
      component: BrandEdit,
    },
    {
      path: '/brands/add',
      name: 'AddBrand',
      component: BrandAdd,
    },
    {
      path: '/chat-app',
      name: 'chatApp',
      component: Chat,
      meta: { requiresAuth: true },
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore()
  
  // ensure user is fetched if token exists
  if (auth.token && !auth.user) {
    await auth.fetchUser()
  }
  console.log("Logged in user : " ,auth.user)
  if (to.meta.requiresAuth && !auth.user) { 
    next({ name: "login" })
  } else if (auth.user && (to.name === "register" || to.name === "login")) {
    next({ name: "SaleItemGallery" })
  } else {
    next()
  }
})

export default router
