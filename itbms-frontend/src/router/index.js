import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SaleItemView from '../views/SaleItemView.vue' // ✅ เพิ่ม
import SaleItemDetailView from '../views/SaleItemDetailView.vue' // ✅ เพิ่ม
import CreateSaleItem from '@/views/CreateSaleItem.vue'
import UpdateSaleItem from '@/views/UpdateSaleItem.vue'

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
      name: 'SaleItem',
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
  ],
})

export default router
