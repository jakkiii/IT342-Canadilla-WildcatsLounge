import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export interface RegisterData {
  name: string;
  email: string;
  password: string;
}

export interface LoginData {
  email: string;
  password: string;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  createdAt: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
}

// Register a new user
export const register = async (data: RegisterData): Promise<ApiResponse<UserResponse>> => {
  try {
    const response = await axios.post(`${API_URL}/auth/register`, data);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    return {
      success: false,
      message: error.message || 'Registration failed'
    };
  }
};

// Login user
export const login = async (data: LoginData): Promise<ApiResponse<UserResponse>> => {
  try {
    const response = await axios.post(`${API_URL}/auth/login`, data);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    return {
      success: false,
      message: error.message || 'Login failed'
    };
  }
};

// Check API health
export const checkHealth = async (): Promise<ApiResponse> => {
  try {
    const response = await axios.get(`${API_URL}/auth/health`);
    return response.data;
  } catch (error: any) {
    return {
      success: false,
      message: 'API is not reachable'
    };
  }
};
