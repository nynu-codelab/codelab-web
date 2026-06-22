import request from './request'

// ---- 类型定义 ----

export interface AdminUserVO {
  id: number
  username: string
  realName: string
  phone: string
  grade: string
  major: string
  className: string
  role: string
  status: number
  createTime: string
  updateTime: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}

export interface UserQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  role?: string
  status?: number
}

// ---- 常量 ----

export const ROLE_MAP: Record<string, string> = {
  ADMIN: '管理员',
  USER: '普通用户'
}

export const ROLE_TAG_TYPE: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
  ADMIN: 'danger',
  USER: 'info'
}

export const STATUS_MAP: Record<number, string> = {
  1: '正常',
  0: '禁用'
}

export const STATUS_TAG_TYPE: Record<number, '' | 'success' | 'warning' | 'danger' | 'info'> = {
  1: 'success',
  0: 'danger'
}

// ---- API 函数 ----

export function getUsers(params?: UserQueryParams): Promise<{ code: number; data: PageResult<AdminUserVO>; message?: string }> {
  return request.get('/admin/users', { params })
}

export function getUserDetail(id: number): Promise<{ code: number; data: AdminUserVO; message?: string }> {
  return request.get(`/admin/users/${id}`)
}

export function updateUserStatus(id: number, status: number): Promise<{ code: number; data: string; message?: string }> {
  return request.patch(`/admin/users/${id}/status`, { status })
}

export function updateUserRole(id: number, role: string): Promise<{ code: number; data: string; message?: string }> {
  return request.patch(`/admin/users/${id}/role`, { role })
}

export function resetUserPassword(id: number, newPassword: string): Promise<{ code: number; data: string; message?: string }> {
  return request.post(`/admin/users/${id}/reset-password`, { newPassword })
}
