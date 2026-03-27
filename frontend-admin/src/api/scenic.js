import request from './request'

export function getScenicList(params) {
  return request({
    url: '/api/admin/scenic/list',
    method: 'get',
    params
  })
}

export function getScenicDetail(id) {
  return request({
    url: `/api/admin/scenic/${id}`,
    method: 'get'
  })
}

export function addScenic(data) {
  return request({
    url: '/api/admin/scenic/add',
    method: 'post',
    data
  })
}

export function updateScenic(data) {
  return request({
    url: '/api/admin/scenic/update',
    method: 'put',
    data
  })
}

export function deleteScenic(id) {
  return request({
    url: `/api/admin/scenic/${id}`,
    method: 'delete'
  })
}

export function updateScenicStatus(id, status) {
  return request({
    url: `/api/admin/scenic/${id}/status`,
    method: 'put',
    data: { status }
  })
}
