import http from './http'

export const authApi = {
  captcha: () => http.get('/auth/captcha'),
  login: (data) => http.post('/auth/login', data),
  register: (data) => http.post('/auth/register', data),
  profile: () => http.get('/auth/profile'),
  updateProfile: (data) => http.put('/auth/profile', data),
  changePassword: (data) => http.put('/auth/password', data)
}

export const detectApi = {
  detectText: (data) => http.post('/detect/text', data),
  detectFile: (data) => http.post('/detect/file', data, { headers: { 'Content-Type': 'multipart/form-data' } }),
  history: () => http.get('/detect/history'),
  results: () => http.get('/detect/results')
}

export const auditApi = {
  tasks: () => http.get('/audit/tasks'),
  taskDetails: () => http.get('/audit/task-details'),
  records: () => http.get('/audit/records'),
  handle: (data) => http.post('/audit/handle', data)
}

export const warningApi = {
  list: () => http.get('/warnings'),
  handle: (id) => http.put(`/warnings/${id}/handle`)
}

export const sensitiveApi = {
  categories: () => http.get('/sensitive-words/categories'),
  list: (keyword) => http.get('/sensitive-words', { params: { keyword } }),
  save: (data) => http.post('/sensitive-words', data),
  delete: (id) => http.delete(`/sensitive-words/${id}`)
}

export const dashboardApi = {
  stats: () => http.get('/dashboard/stats'),
  riskDistribution: () => http.get('/dashboard/risk-distribution')
}

export const userApi = {
  list: (keyword) => http.get('/users', { params: { keyword } }),
  toggleStatus: (id, status) => http.put(`/users/${id}/status`, null, { params: { status } })
}

export const aiReviewApi = {
  review: (data) => http.post('/ai-review', data),
  reviewFile: (data) => http.post('/ai-review/file', data, { headers: { 'Content-Type': 'multipart/form-data' } }),
  records: () => http.get('/ai-review/records')
}

export const adminApi = {
  roles: () => http.get('/admin/roles'),
  saveRole: (data) => http.post('/admin/roles', data),
  permissions: () => http.get('/admin/permissions'),
  savePermission: (data) => http.post('/admin/permissions', data),
  configs: () => http.get('/admin/configs'),
  saveConfig: (data) => http.post('/admin/configs', data),
  logs: () => http.get('/admin/logs'),
  riskLevels: () => http.get('/admin/risk-levels'),
  saveRiskLevel: (data) => http.post('/admin/risk-levels', data)
}
