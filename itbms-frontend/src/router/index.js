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
    {
      path: '/verify-email',
      name: 'verifyEmail',
      component: EmailVerificationView,
    },
    {
      path: '/profile',
      name: 'userProfile',
      component: UserProfile,
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
      next({ path: '/login', query: { redirect: to.fullPath } }); // Redirect to the login page if not authenticated
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
    return auth.accessToken
  } catch (err) {
    // console.log("Not authenticated in protected route : ", err)
    return false
  }
}


export default router
