'use client';

import { useCallback, useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import {
  addCartItem,
  getCart,
  getMenuItems,
  removeCartItem,
  updateCartItem,
  type CartData,
  type CartItemData,
  type MenuItemData,
  type ServingType,
  type UserData,
} from '@/lib/api';
import { CakeSlice, Coffee, CupSoda, Loader2, Minus, Plus, Trash2 } from 'lucide-react';

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

const getStoredUser = (): UserData | null => {
  if (typeof window === 'undefined') return null;
  const stored = localStorage.getItem('user');
  if (!stored) return null;
  try {
    return JSON.parse(stored) as UserData;
  } catch {
    return null;
  }
};

export default function MenuPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserData | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItemData[]>([]);
  const [cart, setCart] = useState<CartData | null>(null);
  const [filter, setFilter] = useState<FilterType>('all');
  const [loading, setLoading] = useState(true);
  const [processingItemId, setProcessingItemId] = useState<number | null>(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedServingByItem, setSelectedServingByItem] = useState<Record<number, ServingType>>({});

  const loadMenuData = useCallback(async (userId: number) => {
    setLoading(true);
    setError('');

    const [menuRes, cartRes] = await Promise.all([
      getMenuItems(),
      getCart(userId),
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
    const storedUser = getStoredUser();
    if (!storedUser?.id) {
      router.push('/user/login');
      return;
    }
    setUser(storedUser);
    void loadMenuData(storedUser.id);
  }, [loadMenuData, router]);

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
      <div className="min-h-[60vh] flex items-center justify-center">
        <div className="flex items-center gap-2 text-[#001C98]">
          <Loader2 className="w-5 h-5 animate-spin" />
          <span className="font-semibold">Loading the menu...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-5 pb-24">
      {error && (
        <div className="rounded-xl border border-[#EF4444]/25 bg-[#EF4444]/10 text-[#EF4444] px-4 py-3 text-sm font-medium">
          {error}
        </div>
      )}

      {success && (
        <div className="rounded-xl border border-[#10B981]/25 bg-[#10B981]/10 text-[#0E8A62] px-4 py-3 text-sm font-medium">
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
        {filteredItems.length === 0 && (
          <div className="col-span-full bg-white border border-dashed border-[#E5D3B3] rounded-2xl p-6 text-center text-sm text-gray-500">
            No menu items found for this filter.
          </div>
        )}

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

            {cart.items.some((cartItem) => cartItem.menuItemId === item.id) && (
              <div className="mt-4 border-t border-[#E5D3B3] pt-3">
                {cart.items
                  .filter((cartItem) => cartItem.menuItemId === item.id)
                  .map((cartItem) => (
                    <div key={cartItem.id} className="flex items-center justify-between">
                      <div>
                        <p className="text-xs text-gray-500">
                          {cartItem.servingType !== 'NONE'
                            ? `${servingTypeLabel(cartItem.servingType)} - ${cartItem.customizationNotes || 'Regular preparation'}`
                            : cartItem.customizationNotes || 'Regular preparation'}
                        </p>
                      </div>
                      <div className="inline-flex items-center rounded-lg border border-[#E5D3B3] overflow-hidden">
                        <button
                          onClick={() => void handleUpdateQuantity(cartItem, cartItem.quantity - 1)}
                          disabled={processingItemId === cartItem.id}
                          className="w-8 h-8 flex items-center justify-center text-[#001C98] hover:bg-[#F5F1E9]"
                        >
                          <Minus className="w-4 h-4" />
                        </button>
                        <span className="w-8 text-center text-sm font-semibold">{cartItem.quantity}</span>
                        <button
                          onClick={() => void handleUpdateQuantity(cartItem, cartItem.quantity + 1)}
                          disabled={processingItemId === cartItem.id}
                          className="w-8 h-8 flex items-center justify-center text-[#001C98] hover:bg-[#F5F1E9]"
                        >
                          <Plus className="w-4 h-4" />
                        </button>
                      </div>
                      <button
                        onClick={() => void handleUpdateQuantity(cartItem, 0)}
                        disabled={processingItemId === cartItem.id}
                        className="text-[#EF4444] hover:text-[#DC2626]"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  ))}
              </div>
            )}
          </div>
        ))}
      </div>

      {cart.items.length > 0 && (
        <div className="fixed left-4 right-4 bottom-5 max-w-4xl mx-auto">
          <div className="bg-[#001C98] text-white rounded-2xl px-4 py-3 shadow-xl flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
            <div>
              <p className="text-sm font-semibold">{cart.itemCount} item(s) in your cart</p>
              <p className="text-xs text-white/70">Subtotal {formatCurrency(cart.subtotal)}</p>
            </div>
            <Link
              href="/user/dashboard/cart"
              className="inline-flex items-center justify-center rounded-xl bg-white text-[#001C98] px-4 py-2 text-sm font-semibold hover:bg-[#F5F1E9] transition"
            >
              Check order
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}
