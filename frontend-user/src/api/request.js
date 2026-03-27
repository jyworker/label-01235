import { createRequest } from './createRequest'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = createRequest({
  tokenKey: 'token',
  loginPath: '/login',
  onLogout: () => {
    localStorage.removeItem('userInfo')
    ElMessage.error('登录已过期，请重新登录')
    router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
  },
  onForbidden: () => {
    ElMessage.error('没有权限访问')
  },
  onError: (error) => {
    if (error.response) {
      const status = error.response.status
      if (status === 403) {
        ElMessage.error('没有权限访问')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else {
        ElMessage.error(error.response.data?.message || '服务器错误')
      }
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }
  }
})

export default request
