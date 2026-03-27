import request from './request'

export function getUserList(params) {
  return request({
    url: '/api/admin/user/list',
    method: 'get',
    params
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/api/admin/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}
