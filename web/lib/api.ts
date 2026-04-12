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
  id: number;
  email: string;
  firstname: string;
  lastname: string;
  studentId?: string;
  role?: string;
}

export type MenuCategory = 'COFFEE' | 'FLAVORED_LATTE' | 'MATCHA_SERIES' | 'BEVERAGES' | 'COFFEE_ADD_ON';

export type ServingType = 'HOT' | 'ICED' | 'BLENDED' | 'NONE';

export interface MenuItemData {
  id: number;
  name: string;
  description: string;
  category: MenuCategory;
  price: number;
  hotPrice?: number;
  icedPrice?: number;
  blendedPrice?: number;
  availableServingTypes: ServingType[];
  isAvailable: boolean;
  imageUrl?: string;
}

export interface AddCartItemData {
  menuItemId: number;
  quantity: number;
  servingType?: ServingType;
  customizationNotes?: string;
}

export interface UpdateCartItemData {
  quantity: number;
  servingType?: ServingType;
  customizationNotes?: string;
}

export interface CartItemData {
  id: number;
  menuItemId: number;
  itemName: string;
  category: MenuCategory;
  unitPrice: number;
  quantity: number;
  servingType: ServingType;
  customizationNotes?: string;
  imageUrl?: string;
  lineTotal: number;
}

export interface CartData {
  cartId: number;
  userId: number;
  itemCount: number;
  subtotal: number;
  updatedAt: string;
  items: CartItemData[];
}

export type OrderStatus = 'PENDING' | 'PREPARING' | 'READY' | 'COMPLETED';

export interface OrderItemData {
  id: number;
  menuItemId: number;
  itemName: string;
  quantity: number;
  priceAtPurchase: number;
  servingType: ServingType;
  customizationNotes?: string;
  lineTotal: number;
}

export interface OrderData {
  id: number;
  orderNumber: string;
  userId: number;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  items: OrderItemData[];
}

export interface AuthData {
  user: UserData;
  accessToken: string;
  refreshToken: string;
}

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

const parseError = <T>(error: unknown, fallback: string): ApiResponse<T> => {
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

// Register a new user
export const register = async (data: RegisterData): Promise<ApiResponse<AuthData>> => {
  try {
    const response = await axios.post(`${API_URL}/auth/register`, data);
    return response.data;
  } catch (error) {
    return parseError(error, 'Registration failed');
  }
};

// Login user — identifier can be email or student_id
export const login = async (data: LoginData): Promise<ApiResponse<AuthData>> => {
  try {
    const response = await axios.post(`${API_URL}/auth/login`, data);
    return response.data;
  } catch (error) {
    return parseError(error, 'Login failed');
  }
};

// Check API health
export const checkHealth = async (): Promise<ApiResponse> => {
  try {
    const response = await axios.get(`${API_URL}/auth/health`);
    return response.data;
  } catch {
    return { success: false, error: 'API is not reachable' };
  }
};

export const getMenuItems = async (category?: string): Promise<ApiResponse<MenuItemData[]>> => {
  try {
    const response = await axios.get(`${API_URL}/menu-items`, {
      params: category && category !== 'all' ? { category } : undefined,
    });
    return response.data;
  } catch (error) {
    return parseError(error, 'Failed to load menu items');
  }
};

export const getCart = async (userId: number): Promise<ApiResponse<CartData>> => {
  try {
    const response = await axios.get(`${API_URL}/carts/${userId}`);
    return response.data;
  } catch (error) {
    return parseError(error, 'Failed to load cart');
  }
};

export const addCartItem = async (userId: number, payload: AddCartItemData): Promise<ApiResponse<CartData>> => {
  try {
    const response = await axios.post(`${API_URL}/carts/${userId}/items`, payload);
    return response.data;
  } catch (error) {
    return parseError(error, 'Failed to add cart item');
  }
};

export const updateCartItem = async (
  userId: number,
  cartItemId: number,
  payload: UpdateCartItemData,
): Promise<ApiResponse<CartData>> => {
  try {
    const response = await axios.put(`${API_URL}/carts/${userId}/items/${cartItemId}`, payload);
    return response.data;
  } catch (error) {
    return parseError(error, 'Failed to update cart item');
  }
};

export const removeCartItem = async (userId: number, cartItemId: number): Promise<ApiResponse<CartData>> => {
  try {
    const response = await axios.delete(`${API_URL}/carts/${userId}/items/${cartItemId}`);
    return response.data;
  } catch (error) {
    return parseError(error, 'Failed to remove cart item');
  }
};

export const checkoutCart = async (userId: number): Promise<ApiResponse<OrderData>> => {
  try {
    const response = await axios.post(`${API_URL}/orders/${userId}/checkout`);
    return response.data;
  } catch (error) {
    return parseError(error, 'Failed to place order');
  }
};

export const getOrders = async (userId: number): Promise<ApiResponse<OrderData[]>> => {
  try {
    const response = await axios.get(`${API_URL}/orders/${userId}`);
    return response.data;
  } catch (error) {
    return parseError(error, 'Failed to load orders');
  }
};
