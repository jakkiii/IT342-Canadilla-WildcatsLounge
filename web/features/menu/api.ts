import axios from 'axios';
import { API_URL, parseError, type ApiResponse } from '@/features/shared/api';
import type { MenuCategory, ServingType } from '@/features/shared/types';

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
