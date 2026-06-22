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
  return request.post('/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getUploadedFiles(): Promise<{ code: number; data: UploadFileItem[]; message?: string }> {
  return request.get('/admin/upload')
}

export function deleteUploadedFile(id: number): Promise<{ code: number; data: null; message?: string }> {
  return request.delete(`/admin/upload/${id}`)
}
