import request from './request'

export interface ProjectItem {
  id: number
  title: string
  summary: string
  coverUrl: string
  descriptionMarkdown: string
  projectType: string
  techStack: string
  leaderName: string
  membersText: string
  repoUrl: string
  demoUrl: string
  documentUrl: string
  status: string
  featured: number
  viewCount: number
  sortOrder: number
  authorId: number
  publishedAt: string | null
  createTime: string
  updateTime: string
}

export interface ProjectCreateParams {
  title: string
  summary?: string
  coverUrl?: string
  descriptionMarkdown: string
  projectType?: string
  techStack?: string
  leaderName?: string
  membersText?: string
  repoUrl?: string
  demoUrl?: string
  documentUrl?: string
  featured?: number
  sortOrder?: number
}

export const STATUS_MAP: Record<string, string> = {
  DRAFT: '草稿',
  PUBLISHED: '已发布',
  OFFLINE: '已下架'
}

export const STATUS_TAG_TYPE: Record<string, 'warning' | 'primary' | '' | 'success' | 'danger' | 'info'> = {
  DRAFT: 'info',
  PUBLISHED: 'success',
  OFFLINE: 'warning'
}

export function getProjects(status?: string, featured?: number): Promise<{ code: number; data: ProjectItem[]; message?: string }> {
  const params: any = {}
  if (status) params.status = status
  if (featured !== undefined) params.featured = featured
  return request.get('/admin/projects', { params })
}

export function getProject(id: number): Promise<{ code: number; data: ProjectItem; message?: string }> {
  return request.get(`/admin/projects/${id}`)
}

export function createProject(data: ProjectCreateParams): Promise<{ code: number; data: ProjectItem; message?: string }> {
  return request.post('/admin/projects', data)
}

export function updateProject(id: number, data: ProjectCreateParams): Promise<{ code: number; data: ProjectItem; message?: string }> {
  return request.put(`/admin/projects/${id}`, data)
}

export function publishProject(id: number): Promise<{ code: number; data: ProjectItem; message?: string }> {
  return request.put(`/admin/projects/${id}/publish`)
}

export function offlineProject(id: number): Promise<{ code: number; data: ProjectItem; message?: string }> {
  return request.put(`/admin/projects/${id}/offline`)
}

export function deleteProject(id: number): Promise<{ code: number; data: null; message?: string }> {
  return request.delete(`/admin/projects/${id}`)
}
