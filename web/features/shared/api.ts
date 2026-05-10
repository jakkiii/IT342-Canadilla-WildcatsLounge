import axios from 'axios';

export const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export interface ApiResponse<T = unknown> {
  success: boolean;
  data?: T;
  error?: string;
  timestamp?: string;
}

const isApiResponse = (value: unknown): value is ApiResponse => {
  if (!value || typeof value !== 'object') {
    return false;
  }
  return typeof (value as { success?: unknown }).success === 'boolean';
};

export const parseError = <T>(error: unknown, fallback: string): ApiResponse<T> => {
  if (axios.isAxiosError(error)) {
    const responseData = error.response?.data;
    if (isApiResponse(responseData)) {
      return responseData as ApiResponse<T>;
    }
    if (typeof responseData === 'string' && responseData) {
      return { success: false, error: responseData };
    }
    return { success: false, error: error.message || fallback };
  }

  if (error instanceof Error) {
    return { success: false, error: error.message || fallback };
  }

  return { success: false, error: fallback };
};
