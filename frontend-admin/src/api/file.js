import axios from 'axios'

export async function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  const token = localStorage.getItem('admin_token')
  const response = await axios.post('/api/admin/file/upload', formData, {
    timeout: 120000,
    headers: token ? { satoken: token } : undefined
  })
  return response.data
}
