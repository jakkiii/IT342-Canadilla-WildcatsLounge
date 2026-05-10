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

export type OrderStatus = 'PENDING' | 'PREPARING' | 'READY' | 'COMPLETED';
