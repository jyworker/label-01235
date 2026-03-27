import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import DefaultLayout from '@/layouts/DefaultLayout.vue'

const routes = [
  {
    path: '/',
    component: DefaultLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'scenic',
        name: 'ScenicList',
        component: () => import('@/views/scenic/ScenicList.vue'),
        meta: { title: '景点列表' }
      },
      {
        path: 'scenic/:id',
        name: 'ScenicDetail',
        component: () => import('@/views/scenic/ScenicDetail.vue'),
        meta: { title: '景点详情' }
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/Search.vue'),
        meta: { title: '搜索结果' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requireAuth: true }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/user/Favorites.vue'),
        meta: { title: '我的收藏', requireAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/user/Orders.vue'),
        meta: { title: '我的订单', requireAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/user/OrderDetail.vue'),
        meta: { title: '订单详情', requireAuth: true }
      },
      {
        path: 'reviews',
        name: 'Reviews',
        component: () => import('@/views/user/Reviews.vue'),
        meta: { title: '我的评论', requireAuth: true }
      },
      {
        path: 'booking/:id',
        name: 'Booking',
        component: () => import('@/views/booking/Booking.vue'),
        meta: { title: '预订', requireAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'TravelVista'} - TravelVista`

  if (to.meta.requireAuth) {
    const userStore = useUserStore()
    if (!userStore.token) {
      ElMessage.warning('请先登录')
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
