import { createRouter, createWebHistory } from 'vue-router'

/**
 * 两级路由空间：
 * `/`        商城前台（游客 + 普通用户）
 * `/admin`   管理后台（平台管理员 + 商家，按 JWT type 显示菜单）
 */
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/MallLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('@/views/mall/Home.vue') },
        { path: 'product/:id', name: 'product-detail', component: () => import('@/views/mall/ProductDetail.vue') },
        { path: 'login', name: 'login', component: () => import('@/views/mall/Login.vue') },
        { path: 'register', name: 'register', component: () => import('@/views/mall/Register.vue') },
        // 购物车、订单等页面按里程碑 5 补充
      ],
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      children: [
        { path: '', name: 'admin-home', component: () => import('@/views/admin/AdminHome.vue') },
        { path: 'categories', name: 'admin-categories', component: () => import('@/views/admin/AdminCategories.vue') },
        { path: 'adverts', name: 'admin-adverts', component: () => import('@/views/admin/AdminAdverts.vue') },
        { path: 'products', name: 'merchant-products', component: () => import('@/views/admin/MerchantProducts.vue') },
        // 商家审核/订单管理/报表等页面按里程碑 4-7 补充
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

export default router
