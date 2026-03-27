import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, getAdminInfo, logoutApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const adminInfo = ref(null)

  async function login(loginForm) {
    const res = await loginApi(loginForm)
    if (res.code === 200) {
      token.value = res.data.token || res.data
      localStorage.setItem('admin_token', token.value)
      ElMessage.success('登录成功')
      const redirect = router.currentRoute.value.query.redirect || '/dashboard'
      router.push(redirect)
      return true
    } else {
      ElMessage.error(res.message || '登录失败')
      return false
    }
  }

  async function fetchAdminInfo() {
    try {
      const res = await getAdminInfo()
      if (res.code === 200) {
        adminInfo.value = res.data
      }
    } catch (e) {
      // 静默处理
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } catch (e) {
      // ignore logout error
    }
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem('admin_token')
    router.push('/login')
    ElMessage.success('已退出登录')
  }

  return {
    token,
    adminInfo,
    login,
    fetchAdminInfo,
    logout
  }
})
