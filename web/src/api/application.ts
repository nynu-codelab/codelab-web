import request from './request'

export interface ApplyParams {
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
}

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

export const STATUS_MAP: Record<string, string> = {
  PENDING: '待审核',
  PRELIMINARY_PASSED: '初筛通过',
  INTERVIEWING: '面试中',
  PASSED: '已通过',
  REJECTED: '未通过',
  WITHDRAWN: '已撤回'
}

export const STATUS_COLORS: Record<string, string> = {
  PENDING: '#ffb74d',
  PRELIMINARY_PASSED: '#64b5f6',
  INTERVIEWING: '#ba68c8',
  PASSED: '#81c784',
  REJECTED: '#ef5350',
  WITHDRAWN: '#78909c'
}

export function submitApply(data: ApplyParams): Promise<ApplyRecord> {
  return request.post('/applications', data)
}

export function getMyApply(): Promise<ApplyRecord> {
  return request.get('/applications/my')
}

export function updateMyApply(data: ApplyParams): Promise<ApplyRecord> {
  return request.put('/applications/my', data)
}
