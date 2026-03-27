import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getUserInfo, logout as logoutApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const nickname = computed(() => userInfo.value?.nickname || '用户')
  const avatar = computed(() => userInfo.value?.avatar || '')

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  async function login(form) {
    const res = await loginApi(form)
    if (res.code === 200) {
      setToken(res.data.token)
      setUserInfo(res.data.userInfo)
      return res
    }
    return res
  }

  async function register(form) {
    const res = await registerApi(form)
    if (res.code === 200 && res.data) {
      setToken(res.data.token)
      if (res.data.userInfo) setUserInfo(res.data.userInfo)
      return res
    }
    return res
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      if (res.code === 200) {
        setUserInfo(res.data)
      }
    } catch (e) {
      // 静默处理
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } catch (e) {
      // ignore
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    nickname,
    avatar,
    setToken,
    setUserInfo,
    login,
    register,
    fetchUserInfo,
    logout
  }
})
