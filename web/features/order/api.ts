import axios from 'axios';
import { API_URL, parseError, type ApiResponse } from '@/features/shared/api';
import type { OrderStatus, ServingType } from '@/features/shared/types';

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
