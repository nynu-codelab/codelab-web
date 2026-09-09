import request, { type ApiResult } from './request'

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
}

export function getMembers(): Promise<ApiResult<MemberItem[]>> {
  return request.get('/members')
}
