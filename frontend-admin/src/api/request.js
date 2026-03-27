import { createRequest } from './createRequest'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = createRequest({
  tokenKey: 'admin_token',
  loginPath: '/login',
  onLogout: () => {
    ElMessage.error('登录已过期，请重新登录')
    router.push('/login')
  },
  onForbidden: () => {
    ElMessage.error('没有操作权限')
  },
  onError: (error) => {
    if (error.response) {
      const status = error.response.status
      if (status === 403) {
        ElMessage.error('没有操作权限')
      } else if (status === 413) {
        ElMessage.error('上传文件过大，已超出服务器限制')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器内部错误')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接异常，请稍后重试')
    }
  }
})

export default request
