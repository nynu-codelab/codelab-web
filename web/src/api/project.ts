import request, { type ApiResult } from './request'

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

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}

export interface ProjectListParams {
  page?: number
  pageSize?: number
}

export function getProjects(params?: ProjectListParams): Promise<ApiResult<PageResult<ProjectItem>>> {
  return request.get('/projects', { params })
}

export function getFeaturedProjects(): Promise<ApiResult<ProjectItem[]>> {
  return request.get('/projects/featured')
}

export function getProject(id: number): Promise<ApiResult<ProjectItem>> {
  return request.get(`/projects/${id}`)
}
