import request from './request'

export function toggleFavorite(scenicSpotId) {
  return request.post('/api/favorite/toggle', { scenicSpotId })
}

export function getMyFavorites(params) {
  return request.get('/api/favorite/my', { params })
}

export function checkFavorite(scenicSpotId) {
  return request.get(`/api/favorite/check/${scenicSpotId}`)
}
