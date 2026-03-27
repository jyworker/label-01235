import request from './request'

export function getReviewList(params) {
  return request.get('/api/review/list', { params })
}

/** 有图片时使用 multipart 方式提交 */
export function addReviewWithImages(formData) {
  return request.post('/api/review/add', formData)
}

/** 无图片时使用 JSON 方式提交 */
export function addReview(data) {
  return request.post('/api/review/submit', data)
}

export function deleteReview(id) {
  return request.delete(`/api/review/${id}`)
}

export function getMyReviews(params) {
  return request.get('/api/review/my', { params })
}
