import request from './request'

export function getOrderList(params) {
  return request({
    url: '/api/admin/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/api/admin/order/${id}`,
    method: 'get'
  })
}

export function updateOrderStatus(id, status) {
  return request({
    url: `/api/admin/order/${id}/status`,
    method: 'put',
    data: { status }
  })
}
