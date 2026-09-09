import request, { type ApiResult } from './request'

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

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}

export interface ArticleListParams {
  page?: number
  pageSize?: number
  category?: string
}

export function getArticles(params?: ArticleListParams): Promise<ApiResult<PageResult<ArticleItem>>> {
  return request.get('/articles', { params })
}

export function getArticle(id: number): Promise<ApiResult<ArticleItem>> {
  return request.get(`/articles/${id}`)
}
