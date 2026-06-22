import request from './request'

export interface DirectionItem {
  id: number
  name: string
  code: string
  summary: string
  description: string
  tags: string
  icon: string
  coverUrl: string
  sortOrder: number
  status: number
  createTime: string
  updateTime: string
}

export interface DirectionCreateParams {
  name: string
  code: string
  summary: string
  description?: string
  tags?: string
  icon?: string
  coverUrl?: string
  sortOrder?: number
}

export function getDirections(status?: number): Promise<{ code: number; data: DirectionItem[]; message?: string }> {
  return request.get('/admin/directions', { params: status !== undefined ? { status } : undefined })
}

export function getDirection(id: number): Promise<{ code: number; data: DirectionItem; message?: string }> {
  return request.get(`/admin/directions/${id}`)
}

export function createDirection(data: DirectionCreateParams): Promise<{ code: number; data: DirectionItem; message?: string }> {
  return request.post('/admin/directions', data)
}

export function updateDirection(id: number, data: DirectionCreateParams): Promise<{ code: number; data: DirectionItem; message?: string }> {
  return request.put(`/admin/directions/${id}`, data)
}

export function enableDirection(id: number): Promise<{ code: number; data: DirectionItem; message?: string }> {
  return request.put(`/admin/directions/${id}/enable`)
}

export function disableDirection(id: number): Promise<{ code: number; data: DirectionItem; message?: string }> {
  return request.put(`/admin/directions/${id}/disable`)
}

export function deleteDirection(id: number): Promise<{ code: number; data: null; message?: string }> {
  return request.delete(`/admin/directions/${id}`)
}
