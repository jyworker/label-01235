import request from './request'

export function getOverview() {
  return request({
    url: '/api/admin/statistics/overview',
    method: 'get'
  })
}

export function getVisitTrend() {
  // 复用订单趋势数据（后端没有单独的访问趋势接口）
  return request({
    url: '/api/admin/statistics/order-trend',
    method: 'get'
  })
}

export function getOrderTrend() {
  return request({
    url: '/api/admin/statistics/order-trend',
    method: 'get'
  })
}

export function getTopScenic() {
  return request({
    url: '/api/admin/statistics/hot-top10',
    method: 'get'
  })
}

export function getCategoryDistribution() {
  return request({
    url: '/api/admin/statistics/category-distribution',
    method: 'get'
  })
}
