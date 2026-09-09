import request, { type ApiResult } from './request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  confirmPassword: string
  realName: string
  phone: string
  grade: string
  major: string
  className: string
}

export interface ChangePasswordParams {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

export interface UserInfo {
  id: number
  username: string
  realName: string
  phone: string
  grade: string
  major: string
  className: string
  role: string
}

export interface LoginResult {
  token: string
  user: UserInfo
}

export function login(data: LoginParams): Promise<ApiResult<LoginResult>> {
  return request.post('/auth/login', data)
}

export function register(data: RegisterParams): Promise<ApiResult<void>> {
  return request.post('/auth/register', data)
}

export function getMe(): Promise<ApiResult<UserInfo>> {
  return request.get('/auth/me')
}

export function logout(): Promise<ApiResult<void>> {
  return request.post('/auth/logout')
}

export function changePassword(data: ChangePasswordParams): Promise<ApiResult<void>> {
  return request.post('/auth/change-password', data)
}
