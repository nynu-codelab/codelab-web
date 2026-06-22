import request from './request'

export interface DirectionItem {
  id: number
  name: string
  code: string
  summary: string
  description: string
  tags: string
  icon: string
  coverUrl: string
  sortOrder: number
  status: number
}

export function getDirections(): Promise<{ code: number; data: DirectionItem[]; message?: string }> {
  return request.get('/directions')
}
