import axios from 'axios';
import { API_URL, parseError, type ApiResponse } from '@/features/shared/api';
import type { UserData } from '@/features/shared/types';

export interface RegisterData {
  email: string;
  password: string;
  firstname: string;
  lastname: string;
  studentId?: string;
}

export interface LoginData {
  identifier: string; // email address or student_id
  password: string;
}

export interface AuthData {
  user: UserData;
  accessToken: string;
  refreshToken: string;
}

export const register = async (data: RegisterData): Promise<ApiResponse<AuthData>> => {
  try {
    const response = await axios.post(`${API_URL}/auth/register`, data);
    return response.data;
  } catch (error) {
    return parseError(error, 'Registration failed');
  }
};

export const login = async (data: LoginData): Promise<ApiResponse<AuthData>> => {
  try {
    const response = await axios.post(`${API_URL}/auth/login`, data);
    return response.data;
  } catch (error) {
    return parseError(error, 'Login failed');
  }
};

export const checkHealth = async (): Promise<ApiResponse> => {
  try {
    const response = await axios.get(`${API_URL}/auth/health`);
    return response.data;
  } catch {
    return { success: false, error: 'API is not reachable' };
  }
};
