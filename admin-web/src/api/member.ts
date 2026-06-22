import request from './request'

export interface MemberItem {
  id: number
  name: string
  avatarUrl: string
  roleTitle: string
  directionId: number | null
  grade: string
  bio: string
  skills: string
  githubUrl: string
  blogUrl: string
  email: string
  sortOrder: number
  status: number
  createTime: string
  updateTime: string
}

export interface MemberCreateParams {
  name: string
  avatarUrl?: string
  roleTitle: string
  directionId?: number | null
  grade?: string
  bio?: string
  skills?: string
  githubUrl?: string
  blogUrl?: string
  email?: string
  sortOrder?: number
  status?: number
}

export function getMembers(status?: number): Promise<{ code: number; data: MemberItem[]; message?: string }> {
  return request.get('/admin/members', { params: status !== undefined ? { status } : undefined })
}

export function getMember(id: number): Promise<{ code: number; data: MemberItem; message?: string }> {
  return request.get(`/admin/members/${id}`)
}

export function createMember(data: MemberCreateParams): Promise<{ code: number; data: MemberItem; message?: string }> {
  return request.post('/admin/members', data)
}

export function updateMember(id: number, data: MemberCreateParams): Promise<{ code: number; data: MemberItem; message?: string }> {
  return request.put(`/admin/members/${id}`, data)
}

export function deleteMember(id: number): Promise<{ code: number; data: null; message?: string }> {
  return request.delete(`/admin/members/${id}`)
}
