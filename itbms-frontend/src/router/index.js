import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ProductView from '../views/ProductView.vue' // ✅ เพิ่ม
import ProductDetailView from '../views/ProductDetailView.vue' // ✅ เพิ่ม

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/sale-items',
      name: 'Product',
      component: ProductView,
    },
    {
      path: '/sale-items/:id',
      name: 'ProductDetail',
      component: ProductDetailView,
    },
  ],
})

export default router
