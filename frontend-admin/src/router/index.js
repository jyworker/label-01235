import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '管理员登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板', icon: 'DataAnalysis' }
      },
      {
        path: 'scenic',
        name: 'Scenic',
        component: () => import('@/views/scenic/ScenicList.vue'),
        meta: { title: '景点管理', icon: 'Place' }
      },
      {
        path: 'scenic/add',
        name: 'ScenicAdd',
        component: () => import('@/views/scenic/ScenicFormPage.vue'),
        meta: { title: '新增景点', icon: 'Place' }
      },
      {
        path: 'scenic/edit/:id',
        name: 'ScenicEdit',
        component: () => import('@/views/scenic/ScenicFormPage.vue'),
        meta: { title: '编辑景点', icon: 'Place' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/CategoryList.vue'),
        meta: { title: '分类管理', icon: 'Menu' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'review',
        name: 'Review',
        component: () => import('@/views/review/ReviewList.vue'),
        meta: { title: '评论管理', icon: 'ChatDotSquare' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { title: '订单管理', icon: 'Tickets' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')

  if (to.meta.requiresAuth !== false && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }

  if (to.meta.title) {
    document.title = `${to.meta.title} - TravelVista 管理后台`
  }
})

export default router
