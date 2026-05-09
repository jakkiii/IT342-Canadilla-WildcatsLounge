'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import {
  checkoutCart,
  getCart,
  removeCartItem,
  updateCartItem,
  type CartData,
  type CartItemData,
  type OrderData,
  type ServingType,
  type UserData,
} from '@/lib/api';
import { CheckCircle2, Loader2, Minus, Plus, ShoppingBasket, Trash2 } from 'lucide-react';

const formatCurrency = (value: number) =>
  new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP', minimumFractionDigits: 0 }).format(value);

const servingTypeLabel = (servingType: ServingType): string => {
  if (servingType === 'HOT') return 'Hot';
  if (servingType === 'ICED') return 'Iced';
  if (servingType === 'BLENDED') return 'Blended';
  return 'Regular';
};

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

export default function CartPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserData | null>(null);
  const [cart, setCart] = useState<CartData | null>(null);
  const [latestOrder, setLatestOrder] = useState<OrderData | null>(null);
  const [loading, setLoading] = useState(true);
  const [processingItemId, setProcessingItemId] = useState<number | null>(null);
  const [checkoutLoading, setCheckoutLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const storedUser = getStoredUser();
    if (!storedUser?.id) {
      router.push('/user/login');
      return;
    }

    setUser(storedUser);

    const loadCart = async () => {
      setLoading(true);
      setError('');

      const cartRes = await getCart(storedUser.id);
      if (cartRes.success && cartRes.data) {
        setCart(cartRes.data);
      } else {
        setError(cartRes.error || 'Failed to load cart.');
      }

      setLoading(false);
    };

    void loadCart();
  }, [router]);

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
      setError('No orders yet. Add items from the menu to continue.');
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
      setSuccess(`Order ${response.data.orderNumber} placed successfully and is now pending.`);
    } else {
      setError(response.error || 'Unable to place order.');
    }

    setCheckoutLoading(false);
  };

  if (loading || !cart) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <div className="flex items-center gap-2 text-[#001C98]">
          <Loader2 className="w-5 h-5 animate-spin" />
          <span className="font-semibold">Loading your cart...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-5">
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

      <div className="bg-white border border-[#E5D3B3] rounded-2xl p-5 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-bold text-[#10203B] flex items-center gap-2">
            <ShoppingBasket className="w-5 h-5 text-[#001C98]" />
            Your Cart
          </h2>
          <span className="text-xs font-semibold text-gray-500">{cart.itemCount} items</span>
        </div>

        {cart.items.length === 0 ? (
          <div className="border border-dashed border-[#E5D3B3] rounded-xl p-6 text-center text-sm text-gray-500 space-y-3">
            <p>No orders yet. Add menu items to start your order.</p>
            <Link
              href="/user/dashboard/menu"
              className="inline-flex items-center justify-center rounded-xl bg-[#001C98] text-white px-4 py-2 text-sm font-semibold hover:bg-[#0025B8] transition"
            >
              Browse menu
            </Link>
          </div>
        ) : (
          <div className="space-y-3">
            {cart.items.map((item) => (
              <div key={item.id} className="border border-[#E5D3B3] rounded-xl p-3">
                <div className="flex justify-between gap-3">
                  <div>
                    <p className="text-sm font-bold text-[#10203B]">{item.itemName}</p>
                    <p className="text-xs text-gray-500">
                      {item.servingType !== 'NONE'
                        ? `${servingTypeLabel(item.servingType)} - ${item.customizationNotes || 'Regular preparation'}`
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
        )}

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
            {checkoutLoading ? 'Placing order...' : 'Buy and place order'}
          </button>

          {latestOrder && (
            <Link
              href="/user/dashboard"
              className="block text-center text-sm text-[#001C98] font-semibold"
            >
              View order status on Home
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}
