import request from './request'

export interface ArticleItem {
  id: number
  title: string
  summary: string
  coverUrl: string
  contentMarkdown: string
  category: string
  tags: string
  status: string
  viewCount: number
  sortOrder: number
  authorId: number
  publishedAt: string | null
  createTime: string
  updateTime: string
}

export interface ArticleCreateParams {
  title: string
  summary?: string
  coverUrl?: string
  contentMarkdown: string
  category?: string
  tags?: string
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

export function getArticles(status?: string): Promise<{ code: number; data: ArticleItem[]; message?: string }> {
  return request.get('/admin/articles', { params: status ? { status } : undefined })
}

export function getArticle(id: number): Promise<{ code: number; data: ArticleItem; message?: string }> {
  return request.get(`/admin/articles/${id}`)
}

export function createArticle(data: ArticleCreateParams): Promise<{ code: number; data: ArticleItem; message?: string }> {
  return request.post('/admin/articles', data)
}

export function updateArticle(id: number, data: ArticleCreateParams): Promise<{ code: number; data: ArticleItem; message?: string }> {
  return request.put(`/admin/articles/${id}`, data)
}

export function publishArticle(id: number): Promise<{ code: number; data: ArticleItem; message?: string }> {
  return request.put(`/admin/articles/${id}/publish`)
}

export function offlineArticle(id: number): Promise<{ code: number; data: ArticleItem; message?: string }> {
  return request.put(`/admin/articles/${id}/offline`)
}

export function deleteArticle(id: number): Promise<{ code: number; data: null; message?: string }> {
  return request.delete(`/admin/articles/${id}`)
}
