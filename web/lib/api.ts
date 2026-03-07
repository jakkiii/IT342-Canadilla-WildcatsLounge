import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

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

export interface UserData {
  email: string;
  firstname: string;
  lastname: string;
  studentId?: string;
  role?: string;
}

export interface AuthData {
  user: UserData;
  accessToken: string;
  refreshToken: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: string;
  timestamp?: string;
}

// Register a new user
export const register = async (data: RegisterData): Promise<ApiResponse<AuthData>> => {
  try {
    const response = await axios.post(`${API_URL}/auth/register`, data);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    return { success: false, error: error.message || 'Registration failed' };
  }
};

// Login user — identifier can be email or student_id
export const login = async (data: LoginData): Promise<ApiResponse<AuthData>> => {
  try {
    const response = await axios.post(`${API_URL}/auth/login`, data);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    return { success: false, error: error.message || 'Login failed' };
  }
};

// Check API health
export const checkHealth = async (): Promise<ApiResponse> => {
  try {
    const response = await axios.get(`${API_URL}/auth/health`);
    return response.data;
  } catch (error: any) {
    return { success: false, error: 'API is not reachable' };
  }
};
