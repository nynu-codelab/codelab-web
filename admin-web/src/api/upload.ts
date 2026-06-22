import request from './request'

export interface UploadFileItem {
  id: number
  originalName: string
  storedName: string
  fileUrl: string
  filePath: string
  mimeType: string
  fileSize: number
  usageType: string
  uploaderId: number
  createTime: string
}

export function uploadFile(file: File, usageType: string = 'other'): Promise<{ code: number; data: UploadFileItem; message?: string }> {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('usageType', usageType)
  // 不手动设置 Content-Type，让浏览器自动生成带 boundary 的 multipart/form-data 头
  return request.post('/admin/upload', formData)
}

export function getUploadedFiles(): Promise<{ code: number; data: UploadFileItem[]; message?: string }> {
  return request.get('/admin/upload')
}

export function deleteUploadedFile(id: number): Promise<{ code: number; data: null; message?: string }> {
  return request.delete(`/admin/upload/${id}`)
}
