import request from './request'

export interface SiteConfigItem {
  id: number
  configKey: string
  configValue: string
  configType: string
  groupName: string
  remark: string
  createTime: string
  updateTime: string
}

export interface SiteConfigUpdateParams {
  configKey: string
  configValue: string
  configType?: string
  groupName?: string
  remark?: string
}

export function getSiteConfigs(): Promise<{ code: number; data: SiteConfigItem[]; message?: string }> {
  return request.get('/admin/site-config')
}

export function updateSiteConfig(key: string, data: SiteConfigUpdateParams): Promise<{ code: number; data: SiteConfigItem; message?: string }> {
  return request.put(`/admin/site-config/${encodeURIComponent(key)}`, data)
}
