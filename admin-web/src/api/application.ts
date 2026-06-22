import request from './request'
import type { ApiResponse } from './auth'

export interface ApplyRecord {
  id: number
  userId: number
  realName: string
  grade: string
  major: string
  className: string
  phone: string
  qq: string
  direction: string
  hasProgrammingBasis: number
  skills: string
  introduction: string
  reason: string
  weeklyAvailableTime: string
  portfolioUrl: string
  status: string
  reviewRemark: string
  reviewerId: number | null
  reviewedAt: string | null
  createTime: string
  updateTime: string
}

export interface ReviewParams {
  status: string
  reviewRemark?: string
}

export const STATUS_MAP: Record<string, string> = {
  PENDING: '待处理',
  VIEWED: '已查看',
  CONTACTED: '已联系',
  PRELIMINARY_PASSED: '通过初筛',
  INTERVIEWING: '面试中',
  PASSED: '已通过',
  REJECTED: '已拒绝',
  WITHDRAWN: '已撤回'
}

export const STATUS_TAG_TYPE: Record<string, 'warning' | 'primary' | '' | 'success' | 'danger' | 'info'> = {
  PENDING: 'warning',
  VIEWED: 'info',
  CONTACTED: 'info',
  PRELIMINARY_PASSED: 'primary',
  INTERVIEWING: '',
  PASSED: 'success',
  REJECTED: 'danger',
  WITHDRAWN: 'info'
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}

export interface ApplicationListParams {
  page?: number
  pageSize?: number
  status?: string
  direction?: string
  keyword?: string
}

export function getApplications(params?: ApplicationListParams): Promise<ApiResponse<PageResult<ApplyRecord>>> {
  return request.get('/admin/applications', { params })
}

export function getApplicationDetail(id: number): Promise<ApiResponse<ApplyRecord>> {
  return request.get(`/admin/applications/${id}`)
}

export function reviewApplication(id: number, data: ReviewParams): Promise<ApiResponse<null>> {
  return request.put(`/admin/applications/${id}/review`, data)
}
