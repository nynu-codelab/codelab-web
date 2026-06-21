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
  PENDING: '待审核',
  PRELIMINARY_PASSED: '初筛通过',
  INTERVIEWING: '面试中',
  PASSED: '已通过',
  REJECTED: '未通过',
  WITHDRAWN: '已撤回'
}

export const STATUS_TAG_TYPE: Record<string, 'warning' | 'primary' | '' | 'success' | 'danger' | 'info'> = {
  PENDING: 'warning',
  PRELIMINARY_PASSED: 'primary',
  INTERVIEWING: '',
  PASSED: 'success',
  REJECTED: 'danger',
  WITHDRAWN: 'info'
}

export function getApplications(status?: string): Promise<ApiResponse<ApplyRecord[]>> {
  return request.get('/admin/applications', { params: status ? { status } : {} })
}

export function getApplicationDetail(id: number): Promise<ApiResponse<ApplyRecord>> {
  return request.get(`/admin/applications/${id}`)
}

export function reviewApplication(id: number, data: ReviewParams): Promise<ApiResponse<null>> {
  return request.put(`/admin/applications/${id}/review`, data)
}
