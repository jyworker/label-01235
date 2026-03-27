import request from './request'

export function getCategoryList(params) {
  return request({
    url: '/api/admin/category/list',
    method: 'get',
    params
  })
}

export function addCategory(data) {
  return request({
    url: '/api/admin/category/add',
    method: 'post',
    data
  })
}

export function updateCategory(data) {
  return request({
    url: '/api/admin/category/update',
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/api/admin/category/${id}`,
    method: 'delete'
  })
}
