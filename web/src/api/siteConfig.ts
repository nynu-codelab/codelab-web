import request, { type ApiResult } from './request'

export function getSiteConfigMap(): Promise<ApiResult<Record<string, string>>> {
  return request.get('/site-config')
}

export function getSiteConfigValue(key: string): Promise<ApiResult<string>> {
  return request.get(`/site-config/${encodeURIComponent(key)}`)
}
