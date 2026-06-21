import request from "./request";

export interface LoginParams {
  username: string;
  password: string;
}

export interface UserItem {
  id: number;
  username: string;
  nickname: string;
  role: string;
}

export interface LoginResponse {
  token: string;
  user: UserItem;
}

export interface ApiResponse<T> {
  code: number;
  data: T;
  message?: string;
}

export function login(data: LoginParams): Promise<ApiResponse<LoginResponse>> {
  return request.post("/auth/login", data);
}

export function getMe(): Promise<ApiResponse<UserItem>> {
  return request.get("/auth/me");
}
