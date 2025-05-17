import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SaleItemView from '../views/SaleItemGalleryView.vue' // ✅ เพิ่ม
import SaleItemDetailView from '../views/SaleItemDetailView.vue' // ✅ เพิ่ม
import CreateSaleItem from '@/views/CreateSaleItem.vue'
import UpdateSaleItem from '@/views/UpdateSaleItem.vue'
import ListSaelItem from '../views/SaleItemListView.vue'
import BrandList from '../views/BrandList.vue'
import BrandEdit from '../views/BrandEdit.vue'
import BrandAdd from '../views/BrandAdd.vue'
import MockUIView from '@/views/MockUIView.vue'

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
      path: '/mock-ui',
      name: 'Mockui',
      component: MockUIView,
    },
  ],
})

export default router
