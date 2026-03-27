import request from './request'

export function getReviewList(params) {
  return request({
    url: '/api/admin/review/list',
    method: 'get',
    params
  })
}

export function auditReview(id, status) {
  return request({
    url: `/api/admin/review/${id}/audit`,
    method: 'put',
    params: { status }
  })
}

export function deleteReview(id) {
  return request({
    url: `/api/admin/review/${id}`,
    method: 'delete'
  })
}
