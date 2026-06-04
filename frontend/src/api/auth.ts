import api from './request'
import type { LoginRequest, LoginResponse, RegisterRequest, UserInfo } from '@/types'

export const authApi = {
  login: (data: LoginRequest): Promise<LoginResponse> => {
    return api.post('/auth/login', data)
  },

  register: (data: RegisterRequest): Promise<UserInfo> => {
    return api.post('/auth/register', data)
  },

  getCurrentUser: (): Promise<UserInfo> => {
    return api.get('/auth/me')
  }
}
