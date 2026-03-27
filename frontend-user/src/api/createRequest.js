import axios from 'axios'

/**
 * 创建 Axios 实例的工厂函数
 * @param {Object} options
 * @param {string} options.tokenKey - localStorage 中 token 的 key
 * @param {string} options.loginPath - 登录页路径
 * @param {Function} options.onLogout - 401 登出回调
 * @param {Function} options.onForbidden - 403 无权限回调（可选）
 * @param {Function} options.onError - 请求错误回调（可选，用于 HTTP 错误、网络异常等）
 */
export function createRequest({
  tokenKey = 'token',
  loginPath = '/login',
  onLogout,
  onForbidden,
  onError
} = {}) {
  const instance = axios.create({
    baseURL: '',
    timeout: 30000
    // 不在此处设置 Content-Type，让 axios 根据数据类型自动决定
  })

  instance.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem(tokenKey)
      if (token) {
        config.headers['satoken'] = token
      }
      if (config.data instanceof FormData) {
        delete config.headers['Content-Type']
        delete config.headers['content-type']
      } else if (!config.headers['Content-Type'] && !config.headers['content-type']) {
        config.headers['Content-Type'] = 'application/json'
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  instance.interceptors.response.use(
    (response) => {
      const res = response.data
      if (res.code === 401) {
        localStorage.removeItem(tokenKey)
        if (onLogout) onLogout()
        else window.location.href = loginPath
        return Promise.reject(new Error(res.message || '未登录'))
      }
      if (res.code === 403) {
        if (onForbidden) onForbidden()
        return Promise.reject(new Error(res.message || '无权限'))
      }
      if (res.code && res.code !== 200) {
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      return res
    },
    (error) => {
      if (error.response) {
        const status = error.response.status
        if (status === 401) {
          localStorage.removeItem(tokenKey)
          if (onLogout) onLogout()
          else window.location.href = loginPath
        } else if (onError) {
          onError(error)
        }
      } else if (onError) {
        onError(error)
      }
      return Promise.reject(error)
    }
  )

  return instance
}
