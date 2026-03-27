import request from './request'

export function loginApi(data) {
  return request({
    url: '/api/admin/login',
    method: 'post',
    data
  })
}

export function logoutApi() {
  return request({
    url: '/api/admin/logout',
    method: 'post'
  })
}

export function getAdminInfo() {
  return request({
    url: '/api/admin/info',
    method: 'get'
  })
}
