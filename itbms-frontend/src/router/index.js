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
import EmailVerificationView from '@/views/EmailVerificationView.vue'
import UserProfile from '@/views/UserProfile.vue'
import { refreshAccessToken } from '@/api'
import CartView from '@/views/CartView.vue'
import UpdateUserProfile from '@/views/UpdateUserProfile.vue'
import OrderView from '@/views/OrderView.vue'
import OrderDetailView from '@/views/OrderDetailView.vue'

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
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/signin',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/verify-email',
      name: 'verifyEmail',
      component: EmailVerificationView,
    },
    {
      path: '/sale-items/add',
      name: 'AddSaleItem',
      component: CreateSaleItem,
      meta: { requiresAuth: true },
    },
    {
      path: '/sale-items/:id/edit',
      name: 'UpdateSaleItem',
      component: UpdateSaleItem,
      meta: { requiresAuth: true },
    },
    {
      path: '/sale-items/list',
      name: 'SaleItemList',
      component: ListSaelItem,
      meta: { requiresAuth: true },
    },
    {
      path: '/brands',
      name: 'BrandList',
      component: BrandList,
      meta: { requiresAuth: true },
    },
    {
      path: '/brands/:id/edit',
      name: 'UpdateBrand',
      component: BrandEdit,
      meta: { requiresAuth: true },
    },
    {
      path: '/brands/add',
      name: 'AddBrand',
      component: BrandAdd,
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'userProfile',
      component: UserProfile,
      meta: { requiresAuth: true },
    },
    {
      path: '/cart',
      name: 'cart',
      component: CartView,
      meta: { requiresAuth: true },
    },
    {
      path: '/chat-app',
      name: 'chatApp',
      component: Chat,
      meta: { requiresAuth: true },
    },
    {
      path: '/profile/edit',
      name: 'updatUserProfile',
      component: UpdateUserProfile,
      meta: { requiresAuth: true },
    },
    {
      path: '/orders',
      name: 'order',
      component: OrderView,
      meta: { requiresAuth: true },
    },
    {
      path: '/orders/:id',
      name: 'orderDetail',
      component: OrderDetailView,
      meta: { requiresAuth: true },
    },
  ],
})


router.beforeEach(async (to, from, next) => {
  const isAuthenticated = await checkIfUserIsAuthenticated();

  // Protected route
  if (to.meta.requiresAuth) {
    if (isAuthenticated) {
      next()
    }
    else {
      next({ name: 'login', query: { redirect: to.fullPath } }); // Redirect to the login page if not authenticated
    }
  } 
  // Register/Login route
  else if (isAuthenticated && (to.name === 'login' || to.name === 'register')) {
    next({ name: "Home" })
  }
  // Public route
  else {
    next()
  }
});

async function checkIfUserIsAuthenticated() {
  const auth = useAuthStore()
  // console.log("user : ", auth.user)
  // console.log("token : ", auth.accessToken)
  
  // Check by access token
  try {
    await refreshAccessToken(auth)
    return auth.accessToken != null
  } catch (err) {
    // console.log("Not authenticated in protected route : ", err)
    return false
  }
}


export default router
