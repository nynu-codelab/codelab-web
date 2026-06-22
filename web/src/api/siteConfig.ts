import request from './request'

export function getSiteConfigMap(): Promise<{ code: number; data: Record<string, string>; message?: string }> {
  return request.get('/site-config')
}

export function getSiteConfigValue(key: string): Promise<{ code: number; data: string; message?: string }> {
  return request.get(`/site-config/${encodeURIComponent(key)}`)
}
