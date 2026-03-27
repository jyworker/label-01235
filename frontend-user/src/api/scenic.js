import request from './request'

export function getScenicList(params) {
  return request.get('/api/scenic/list', { params })
}

export function getScenicDetail(id) {
  return request.get(`/api/scenic/${id}`)
}

export function getHotScenic() {
  return request.get('/api/scenic/hot')
}

export function searchScenic(params) {
  return request.get('/api/scenic/search', { params })
}

export function getCategories() {
  return request.get('/api/scenic/categories')
}

export function getRecommended() {
  return request.get('/api/scenic/recommend')
}
