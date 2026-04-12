'use client';

import { useCallback, useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import {
  addCartItem,
  checkoutCart,
  getCart,
  getMenuItems,
  getOrders,
  removeCartItem,
  updateCartItem,
  type CartData,
  type CartItemData,
  type MenuItemData,
  type OrderData,
  type ServingType,
  type UserData,
} from '@/lib/api';
import {
  CakeSlice,
  CheckCircle2,
  Coffee,
  CupSoda,
  Loader2,
  LogOut,
  Minus,
  Plus,
  ShoppingBasket,
  Trash2,
} from 'lucide-react';

type FilterType =
  | 'all'
  | 'coffee'
  | 'flavored-latte'
  | 'matcha-series'
  | 'beverages'
  | 'coffee-add-on';

const FILTERS: { label: string; value: FilterType }[] = [
  { label: 'All Items', value: 'all' },
  { label: 'Coffee', value: 'coffee' },
  { label: 'Flavored Latte', value: 'flavored-latte' },
  { label: 'Matcha Series', value: 'matcha-series' },
  { label: 'Beverages / Cold Drinks', value: 'beverages' },
  { label: 'Coffee Add-ons', value: 'coffee-add-on' },
];

const formatCurrency = (value: number) =>
  new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP', minimumFractionDigits: 0 }).format(value);

type ServingOption = {
  type: ServingType;
  label: string;
  price: number;
};

const getServingOptions = (item: MenuItemData): ServingOption[] => {
  const options: ServingOption[] = [];

  if (typeof item.hotPrice === 'number') {
    options.push({ type: 'HOT', label: 'Hot', price: item.hotPrice });
  }
  if (typeof item.icedPrice === 'number') {
    options.push({ type: 'ICED', label: 'Iced', price: item.icedPrice });
  }
  if (typeof item.blendedPrice === 'number') {
    options.push({ type: 'BLENDED', label: 'Blended', price: item.blendedPrice });
  }

  return options;
};

const servingTypeLabel = (servingType: ServingType): string => {
  if (servingType === 'HOT') return 'Hot';
  if (servingType === 'ICED') return 'Iced';
  if (servingType === 'BLENDED') return 'Blended';
  return 'Regular';
};

const createEmptyCart = (userId: number): CartData => ({
  cartId: 0,
  userId,
  itemCount: 0,
  subtotal: 0,
  updatedAt: new Date().toISOString(),
  items: [],
});

export default function DashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserData | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItemData[]>([]);
  const [cart, setCart] = useState<CartData | null>(null);
  const [latestOrder, setLatestOrder] = useState<OrderData | null>(null);
  const [filter, setFilter] = useState<FilterType>('all');
  const [loading, setLoading] = useState(true);
  const [processingItemId, setProcessingItemId] = useState<number | null>(null);
  const [checkoutLoading, setCheckoutLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedServingByItem, setSelectedServingByItem] = useState<Record<number, ServingType>>({});

  const loadDashboardData = useCallback(async (userId: number) => {
    setLoading(true);
    setError('');

    const [menuRes, cartRes, orderRes] = await Promise.all([
      getMenuItems(),
      getCart(userId),
      getOrders(userId),
    ]);

    if (menuRes.success && menuRes.data) {
      setMenuItems(menuRes.data);
    } else {
      setError(menuRes.error || 'Failed to load menu items.');
    }

    if (cartRes.success && cartRes.data) {
      setCart(cartRes.data);
    } else {
      setCart(createEmptyCart(userId));
      setError(cartRes.error || 'Failed to load cart.');
    }

    if (orderRes.success && orderRes.data && orderRes.data.length > 0) {
      setLatestOrder(orderRes.data[0]);
    }

    setLoading(false);
  }, []);

  useEffect(() => {
    if (menuItems.length === 0) return;

    setSelectedServingByItem((previous) => {
      const next = { ...previous };

      for (const item of menuItems) {
        const options = getServingOptions(item);
        if (options.length === 0) {
          delete next[item.id];
          continue;
        }

        const current = next[item.id];
        if (!current || !options.some((option) => option.type === current)) {
          next[item.id] = options[0].type;
        }
      }

      return next;
    });
  }, [menuItems]);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      const parsedUser = JSON.parse(storedUser) as UserData;
      if (!parsedUser?.id) {
        setError('Missing user id. Please login again.');
        router.push('/login');
        return;
      }
      setUser(parsedUser);
      void loadDashboardData(parsedUser.id);
    } else {
      router.push('/login');
    }
  }, [loadDashboardData, router]);

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    router.push('/login');
  };

  const handleAddToCart = async (item: MenuItemData) => {
    if (!user) return;

    const servingOptions = getServingOptions(item);
    const selectedServingType = servingOptions.length > 0
      ? selectedServingByItem[item.id] ?? servingOptions[0].type
      : 'NONE';

    setProcessingItemId(item.id);
    setError('');
    setSuccess('');

    const response = await addCartItem(user.id, {
      menuItemId: item.id,
      quantity: 1,
      servingType: selectedServingType,
      customizationNotes: '',
    });

    if (response.success && response.data) {
      setCart(response.data);
      setSuccess(
        selectedServingType !== 'NONE'
          ? `${item.name} (${servingTypeLabel(selectedServingType)}) added to cart.`
          : `${item.name} added to cart.`,
      );
    } else {
      setError(response.error || 'Unable to add item to cart.');
    }

    setProcessingItemId(null);
  };

  const handleUpdateQuantity = async (item: CartItemData, quantity: number) => {
    if (!user || !cart) return;

    setProcessingItemId(item.id);
    setError('');

    const response = quantity <= 0
      ? await removeCartItem(user.id, item.id)
      : await updateCartItem(user.id, item.id, {
          quantity,
          servingType: item.servingType,
          customizationNotes: item.customizationNotes || '',
        });

    if (response.success && response.data) {
      setCart(response.data);
    } else {
      setError(response.error || 'Unable to update cart item.');
    }

    setProcessingItemId(null);
  };

  const handleCheckout = async () => {
    if (!user || !cart) return;

    if (cart.items.length === 0) {
      setError('Your cart is empty. Add items before checkout.');
      return;
    }

    setCheckoutLoading(true);
    setError('');
    setSuccess('');

    const response = await checkoutCart(user.id);
    if (response.success && response.data) {
      setLatestOrder(response.data);
      const cartRes = await getCart(user.id);
      if (cartRes.success && cartRes.data) {
        setCart(cartRes.data);
      }
      setSuccess(`Order ${response.data.orderNumber} placed successfully.`);
    } else {
      setError(response.error || 'Unable to place order.');
    }

    setCheckoutLoading(false);
  };

  const filteredItems = menuItems.filter((item) => {
    if (filter === 'all') return true;
    if (filter === 'coffee') return item.category === 'COFFEE';
    if (filter === 'flavored-latte') return item.category === 'FLAVORED_LATTE';
    if (filter === 'matcha-series') return item.category === 'MATCHA_SERIES';
    if (filter === 'beverages') return item.category === 'BEVERAGES';
    return item.category === 'COFFEE_ADD_ON';
  });

  if (!user || loading || !cart) {
    return (
      <div className="min-h-screen bg-[#FDFBF7] font-poppins flex items-center justify-center">
        <div className="flex items-center gap-2 text-[#001C98]">
          <Loader2 className="w-5 h-5 animate-spin" />
          <span className="font-semibold">Loading your lounge dashboard...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#FDFBF7] font-poppins">

      <header className="bg-[#001C98] text-white px-6 py-4 flex items-center justify-between shadow-lg">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-white/20 flex items-center justify-center">
            <Coffee className="w-5 h-5 text-white" />
          </div>
          <div>
            <p className="text-xs text-white/70">Good day, {user.firstname}</p>
            <span className="text-lg font-bold tracking-wide">Wildcats Lounge Ordering</span>
          </div>
        </div>
        <button
          onClick={handleLogout}
          className="flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white text-sm font-medium px-4 py-2 rounded-xl transition"
        >
          <LogOut className="w-4 h-4" />
          Logout
        </button>
      </header>

      <main className="max-w-7xl mx-auto px-4 lg:px-6 py-6 grid grid-cols-1 xl:grid-cols-[1.35fr_0.95fr] gap-6">
        <section className="space-y-5">
          {error && (
            <div className="rounded-xl border border-[#EF4444]/25 bg-[#EF4444]/10 text-[#EF4444] px-4 py-3 text-sm font-medium">
              {error}
            </div>
          )}

          {success && (
            <div className="rounded-xl border border-[#10B981]/25 bg-[#10B981]/10 text-[#0E8A62] px-4 py-3 text-sm font-medium flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              {success}
            </div>
          )}

          <div className="bg-white border border-[#E5D3B3] rounded-2xl p-4 shadow-sm">
            <div className="flex flex-wrap gap-2">
              {FILTERS.map((item) => (
                <button
                  key={item.value}
                  onClick={() => setFilter(item.value)}
                  className={`px-3 py-1.5 rounded-full text-sm font-semibold transition ${
                    filter === item.value
                      ? 'bg-[#001C98] text-white'
                      : 'bg-[#F5F1E9] text-[#001C98] hover:bg-[#E5D3B3]/40'
                  }`}
                >
                  {item.label}
                </button>
              ))}
            </div>
          </div>

          <div className="grid sm:grid-cols-2 gap-4">
            {filteredItems.map((item) => (
              <div key={item.id} className="bg-white border border-[#E5D3B3] rounded-2xl p-4 shadow-sm">
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-start gap-3 min-w-0">
                    <div className="w-12 h-12 rounded-xl bg-[#001C98]/10 border border-[#001C98]/20 flex items-center justify-center overflow-hidden flex-shrink-0">
                      {(item.category === 'COFFEE' || item.category === 'FLAVORED_LATTE') && (
                        <Coffee className="w-6 h-6 text-[#001C98]" />
                      )}
                      {(item.category === 'MATCHA_SERIES' || item.category === 'BEVERAGES') && (
                        <CupSoda className="w-6 h-6 text-[#001C98]" />
                      )}
                      {item.category === 'COFFEE_ADD_ON' && <CakeSlice className="w-6 h-6 text-[#001C98]" />}
                    </div>
                    <div className="min-w-0">
                      <p className="font-bold text-[#10203B] leading-tight">{item.name}</p>
                      <p className="text-xs text-gray-500 mt-1">{item.description}</p>

                      {getServingOptions(item).length > 0 ? (
                        <div className="mt-2 flex flex-wrap gap-1.5">
                          {getServingOptions(item).map((option) => (
                            <button
                              key={`${item.id}-${option.type}`}
                              onClick={() => {
                                setSelectedServingByItem((previous) => ({
                                  ...previous,
                                  [item.id]: option.type,
                                }));
                              }}
                              className={`px-2 py-1 rounded-lg text-xs font-semibold border transition ${
                                selectedServingByItem[item.id] === option.type
                                  ? 'bg-[#001C98] text-white border-[#001C98]'
                                  : 'bg-white text-[#001C98] border-[#C9D2FF] hover:bg-[#EEF1FF]'
                              }`}
                              title={`${option.label} ${formatCurrency(option.price)}`}
                            >
                              {option.label} {formatCurrency(option.price)}
                            </button>
                          ))}
                        </div>
                      ) : (
                        <p className="text-sm font-extrabold text-[#001C98] mt-2">{formatCurrency(item.price)}</p>
                      )}
                    </div>
                  </div>

                  <button
                    onClick={() => void handleAddToCart(item)}
                    disabled={processingItemId === item.id}
                    className="w-9 h-9 rounded-lg bg-[#001C98] hover:bg-[#0025B8] disabled:opacity-60 text-white flex items-center justify-center transition"
                    title={`Add ${item.name}`}
                  >
                    {processingItemId === item.id ? (
                      <Loader2 className="w-4 h-4 animate-spin" />
                    ) : (
                      <Plus className="w-4 h-4" />
                    )}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section className="space-y-5">
          <div className="bg-white border border-[#E5D3B3] rounded-2xl p-5 shadow-sm">
            <p className="text-xs uppercase tracking-wider font-semibold text-[#001C98] mb-2">Order Status</p>
            {latestOrder ? (
              <div>
                <p className="font-bold text-[#10203B]">{latestOrder.orderNumber}</p>
                <p className="text-sm text-gray-500 mt-1">
                  Latest order at {new Date(latestOrder.createdAt).toLocaleString()}
                </p>
                <span className="inline-flex mt-3 px-3 py-1 rounded-full text-xs font-bold bg-[#001C98]/10 text-[#001C98]">
                  {latestOrder.status}
                </span>
              </div>
            ) : (
              <p className="text-sm text-gray-500">No order yet. Add items and place your first pickup order.</p>
            )}
          </div>

          <div className="bg-white border border-[#E5D3B3] rounded-2xl p-5 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-bold text-[#10203B] flex items-center gap-2">
                <ShoppingBasket className="w-5 h-5 text-[#001C98]" />
                Your Cart
              </h2>
              <span className="text-xs font-semibold text-gray-500">{cart.itemCount} items</span>
            </div>

            <div className="space-y-3 max-h-[360px] overflow-auto pr-1">
              {cart.items.length === 0 && (
                <p className="text-sm text-gray-500 border border-dashed border-[#E5D3B3] rounded-xl p-4 text-center">
                  Cart is empty. Add menu items to start your order.
                </p>
              )}

              {cart.items.map((item) => (
                <div key={item.id} className="border border-[#E5D3B3] rounded-xl p-3">
                  <div className="flex justify-between gap-3">
                    <div>
                      <p className="text-sm font-bold text-[#10203B]">{item.itemName}</p>
                      <p className="text-xs text-gray-500">
                        {item.servingType !== 'NONE'
                          ? `${servingTypeLabel(item.servingType)} • ${item.customizationNotes || 'Regular preparation'}`
                          : item.customizationNotes || 'Regular preparation'}
                      </p>
                    </div>
                    <p className="text-sm font-bold text-[#001C98]">{formatCurrency(item.lineTotal)}</p>
                  </div>

                  <div className="mt-2 flex items-center justify-between">
                    <div className="inline-flex items-center rounded-lg border border-[#E5D3B3] overflow-hidden">
                      <button
                        onClick={() => void handleUpdateQuantity(item, item.quantity - 1)}
                        disabled={processingItemId === item.id}
                        className="w-8 h-8 flex items-center justify-center text-[#001C98] hover:bg-[#F5F1E9]"
                      >
                        <Minus className="w-4 h-4" />
                      </button>
                      <span className="w-8 text-center text-sm font-semibold">{item.quantity}</span>
                      <button
                        onClick={() => void handleUpdateQuantity(item, item.quantity + 1)}
                        disabled={processingItemId === item.id}
                        className="w-8 h-8 flex items-center justify-center text-[#001C98] hover:bg-[#F5F1E9]"
                      >
                        <Plus className="w-4 h-4" />
                      </button>
                    </div>

                    <button
                      onClick={() => void handleUpdateQuantity(item, 0)}
                      disabled={processingItemId === item.id}
                      className="text-[#EF4444] hover:text-[#DC2626]"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className="border-t border-[#E5D3B3] mt-4 pt-4 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Subtotal</span>
                <span className="text-lg font-extrabold text-[#001C98]">{formatCurrency(cart.subtotal)}</span>
              </div>

              <button
                onClick={() => void handleCheckout()}
                disabled={checkoutLoading || cart.items.length === 0}
                className="w-full py-3 rounded-xl bg-[#001C98] hover:bg-[#0025B8] disabled:opacity-60 text-white font-bold transition"
              >
                {checkoutLoading ? 'Placing order...' : 'Place Order for Pickup'}
              </button>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}
