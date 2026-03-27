import request from './request'

export function createOrder(data) {
  return request.post('/api/order/create', data)
}

export function payOrder(id) {
  return request.post(`/api/order/${id}/pay`)
}

export function cancelOrder(id) {
  return request.post(`/api/order/${id}/cancel`)
}

export function getMyOrders(params) {
  return request.get('/api/order/my', { params })
}

export function getOrderDetail(id) {
  return request.get(`/api/order/${id}`)
}
