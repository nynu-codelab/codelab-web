import request from './request'

export interface DashboardStats {
  userCount: number
  articleCount: number
  publishedArticleCount: number
  projectCount: number
  publishedProjectCount: number
  recruitPending: number
  recruitTotal: number
  recruitPassed: number
  recruitRejected: number
  memberCount: number
}

export function getDashboardStats(): Promise<{ code: number; data: DashboardStats; message?: string }> {
  return request.get('/admin/dashboard/stats')
}
