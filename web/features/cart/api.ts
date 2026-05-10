import axios from 'axios';
import { API_URL, parseError, type ApiResponse } from '@/features/shared/api';
import type { MenuCategory, ServingType } from '@/features/shared/types';

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
