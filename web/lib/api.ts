import axios from 'axios';
import { getAccessToken } from './auth';

// Use /backend-api in the browser (Next.js proxies to Spring Boot — avoids CORS).
const API_URL =
  process.env.NEXT_PUBLIC_API_URL ||
  (typeof window !== 'undefined' ? '/backend-api' : 'http://127.0.0.1:8080/api');
const REQUEST_TIMEOUT_MS = 15000;

const api = axios.create({
  baseURL: API_URL,
  timeout: REQUEST_TIMEOUT_MS,
});

api.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface RegisterData {
  email: string;
  password: string;
  firstname: string;
  lastname: string;
  studentId: string;
}

export interface LoginData {
  identifier: string;
  password: string;
}

export interface RegisterVerifyData extends RegisterData {
  verificationCode: string;
}

export interface LoginVerifyData extends LoginData {
  verificationCode: string;
}

export interface UserData {
  id?: number;
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

export interface ApiResponse<T = unknown> {
  success: boolean;
  data?: T;
  error?: string;
  timestamp?: string;
}

export interface MenuItem {
  id: number;
  name: string;
  description?: string;
  category: string;
  categoryLabel?: string;
  price: number;
  isAvailable: boolean;
  inventorySufficient?: boolean;
  maxServingsFromStock?: number;
  recipe?: MenuRecipeLine[];
  imageUrl?: string;
  allowHot?: boolean;
  allowIced?: boolean;
  allowBlended?: boolean;
  allowAddons?: boolean;
  allowSugarLevel?: boolean;
}

export interface MenuRecipeLine {
  ingredientId: number;
  ingredientName: string;
  unit: string;
  quantityPerServing: number;
  stockOnHand: number;
}

export interface Ingredient {
  id: number;
  name: string;
  unit: string;
  quantityOnHand: number;
  lowStockThreshold: number;
  lowStock: boolean;
  outOfStock: boolean;
}

export interface CartItem {
  id: number;
  menuItemId: number;
  itemName: string;
  unitPrice: number;
  quantity: number;
  customizationNotes?: string;
  parentItemId?: number | null;
  servingType?: string | null;
  sugarLevelPercent?: number | null;
  lineTotal: number;
}

export interface Cart {
  id: number;
  items: CartItem[];
  subtotal: number;
}

interface RawCartResponse {
  id?: number;
  cartId?: number;
  userId?: number;
  itemCount?: number;
  subtotal: number;
  items: CartItem[];
}

function mapCart(raw: RawCartResponse): Cart {
  return {
    id: raw.cartId ?? raw.id ?? 0,
    items: (raw.items || []).map((item) => ({
      ...item,
      unitPrice: Number(item.unitPrice),
      lineTotal: Number(item.lineTotal),
    })),
    subtotal: Number(raw.subtotal),
  };
}

export interface OrderItem {
  id: number;
  menuItemId?: number;
  itemName: string;
  quantity: number;
  priceAtPurchase: number;
  customizationNotes?: string;
  parentItemId?: number | null;
  servingType?: string | null;
  sugarLevelPercent?: number | null;
}

export interface Order {
  id: number;
  orderNumber: string;
  status: string;
  totalAmount: number;
  createdAt: string;
  updatedAt?: string;
  customerName?: string;
  customerEmail?: string;
  customerStudentId?: string;
  items: OrderItem[];
}

export interface LoungeStatus {
  occupancyLevel: string;
  displayLabel: string;
  color: string;
  lastUpdatedAt: string;
}

export interface Event {
  id: number;
  title: string;
  description?: string;
  postLink?: string;
  startDatetime: string;
  endDatetime: string;
}

export interface StaffDashboard {
  pendingOrders: number;
  loungeStatus: LoungeStatus;
}

export interface TrendPoint {
  label: string;
  orders: number;
  revenue: number;
}

export interface TopItemPoint {
  itemName: string;
  quantity: number;
  orderCount: number;
}

export interface StatusPoint {
  status: string;
  count: number;
}

export interface StaffOrderAnalytics {
  daily: TrendPoint[];
  weekly: TrendPoint[];
  monthly: TrendPoint[];
  topItems: TopItemPoint[];
  statusDistribution: StatusPoint[];
}

async function handle<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<ApiResponse<T>> {
  try {
    const response = await promise;
    return response.data;
  } catch (error: unknown) {
    const err = error as {
      response?: { data?: ApiResponse<T> & { message?: string; error?: string } };
      message?: string;
    };
    if (err.response?.data) {
      const d = err.response.data;
      // Return as-is if it's already our ApiResponse shape
      if ('success' in d) return d as ApiResponse<T>;
      // Otherwise extract the message/error field from Spring's default error body
      const msg = (d as { message?: string; error?: string }).message
        || (d as { error?: string }).error
        || 'Request failed';
      return { success: false, error: msg };
    }
    const msg = err.message || 'Request failed';
    if (msg === 'Network Error' || msg.includes('ERR_CONNECTION')) {
      return {
        success: false,
        error:
          'Cannot reach the backend. In IntelliJ: Stop → Build → Rebuild Project → Run WildcatsLoungeApplication. Then restart the Next.js dev server (npm run dev) and refresh.',
      };
    }
    return { success: false, error: msg };
  }
}

export const register = (data: RegisterData) =>
  handle<AuthData>(api.post('/auth/register', data));

export const login = (data: LoginData) =>
  handle<AuthData>(api.post('/auth/login', data));

export const sendRegisterCode = (data: RegisterData) =>
  handle<string>(api.post('/auth/register/send-code', data));

export const verifyRegister = (data: RegisterVerifyData) =>
  handle<AuthData>(api.post('/auth/register/verify', data));

export const sendLoginCode = (data: LoginData) =>
  handle<string>(api.post('/auth/login/send-code', data));

export const verifyLogin = (data: LoginVerifyData) =>
  handle<AuthData>(api.post('/auth/login/verify', data));

export const checkHealth = () =>
  handle(api.get('/auth/health'));

export const getMenu = () =>
  handle<MenuItem[]>(api.get('/auth/menu'));

export const getLoungeStatus = async (): Promise<ApiResponse<LoungeStatus>> => {
  const res = await handle<LoungeStatus>(api.get('/auth/lounge/status'));
  if (res.success) return res;
  return {
    success: true,
    data: {
      occupancyLevel: 'low',
      displayLabel: 'Available',
      color: 'green',
      lastUpdatedAt: new Date().toISOString(),
    },
  };
};

export const getEvents = async (): Promise<ApiResponse<Event[]>> => {
  const res = await handle<Event[]>(api.get('/auth/events'));
  if (res.success) return res;
  return { success: true, data: [] };
};

export const getTodayEvents = async (): Promise<ApiResponse<Event[]>> => {
  const res = await handle<Event[]>(api.get('/auth/events/today'));
  if (res.success) return res;
  return { success: true, data: [] };
};

function cartError(res: ApiResponse<RawCartResponse>): ApiResponse<Cart> {
  return { success: false, error: res.error || 'Cart request failed' };
}

export const getCart = async (): Promise<ApiResponse<Cart>> => {
  const res = await handle<RawCartResponse>(api.get('/auth/cart'));
  if (res.success && res.data) return { success: true, data: mapCart(res.data) };
  return cartError(res);
};

export const addToCart = async (
  menuItemId: number,
  quantity: number,
  options?: {
    customizationNotes?: string;
    servingType?: string;
    sugarLevelPercent?: number;
    addonIds?: number[];
  }
): Promise<ApiResponse<Cart>> => {
  const res = await handle<RawCartResponse>(
    api.post('/auth/cart/items', { menuItemId, quantity, ...options })
  );
  if (res.success && res.data) return { success: true, data: mapCart(res.data) };
  return cartError(res);
};

export const updateCartItem = async (id: number, quantity: number): Promise<ApiResponse<Cart>> => {
  const res = await handle<RawCartResponse>(api.put(`/auth/cart/items/${id}`, { quantity }));
  if (res.success && res.data) return { success: true, data: mapCart(res.data) };
  return cartError(res);
};

export const removeCartItem = async (id: number): Promise<ApiResponse<Cart>> => {
  const res = await handle<RawCartResponse>(api.delete(`/auth/cart/items/${id}`));
  if (res.success && res.data) return { success: true, data: mapCart(res.data) };
  return cartError(res);
};

export const placeOrder = async () => handle<Order>(api.post('/auth/orders'));

export const getMyOrders = async () => handle<Order[]>(api.get('/auth/orders/my'));

export const getOrder = async (id: number): Promise<ApiResponse<Order>> =>
  handle<Order>(api.get(`/auth/orders/${id}`));

// Staff APIs (prefer /auth/staff — available on the unified auth controller)
export const staffDashboard = () =>
  handle<StaffDashboard>(api.get('/auth/staff/dashboard'));

export const staffGetOrders = () =>
  handle<Order[]>(api.get('/auth/staff/orders'));

export const staffGetOrderAnalytics = () =>
  handle<StaffOrderAnalytics>(api.get('/auth/staff/orders/analytics'));

export const staffUpdateOrderStatus = (id: number, status: string) =>
  handle<Order>(api.put(`/auth/staff/orders/${id}/status`, { status }));

export const staffGetMenu = () =>
  handle<MenuItem[]>(api.get('/staff/menu'));

export const staffCreateMenu = (item: Partial<MenuItem>) =>
  handle<MenuItem>(api.post('/staff/menu', item));

export const staffUpdateMenu = (id: number, item: Partial<MenuItem>) =>
  handle<MenuItem>(api.put(`/staff/menu/${id}`, item));

export const staffToggleMenu = (id: number, isAvailable: boolean) =>
  handle<MenuItem>(api.patch(`/staff/menu/${id}/availability`, { isAvailable }));

export const staffGetIngredients = () =>
  handle<Ingredient[]>(api.get('/auth/staff/inventory/ingredients'));

export const staffRestockIngredient = (id: number, amount: number) =>
  handle<Ingredient>(api.post(`/auth/staff/inventory/ingredients/${id}/restock`, { amount }));

export const staffDeleteMenu = (id: number) =>
  handle(api.delete(`/staff/menu/${id}`));

export const staffGetEvents = () =>
  handle<Event[]>(api.get('/staff/events'));

export const staffCreateEvent = (event: {
  title: string;
  description?: string;
  postLink?: string;
  startDatetime: string;
  endDatetime: string;
}) => handle<Event>(api.post('/staff/events', event));

export const staffUpdateEvent = (
  id: number,
  event: {
    title: string;
    description?: string;
    postLink?: string;
    startDatetime: string;
    endDatetime: string;
  }
) => handle<Event>(api.put(`/staff/events/${id}`, event));

export const staffDeleteEvent = (id: number) =>
  handle(api.delete(`/staff/events/${id}`));

export const staffUpdateLoungeStatus = (occupancyLevel: string) =>
  handle<LoungeStatus>(api.put('/auth/staff/lounge/status', { occupancyLevel }));
