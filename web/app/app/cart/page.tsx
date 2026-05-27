'use client';

import { useEffect, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import {
  getCart,
  getMenu,
  updateCartItem,
  removeCartItem,
  placeOrder,
  type Cart,
  type MenuItem,
} from '@/lib/api';
import { Loader2, Trash2, Minus, Plus, ShoppingBag } from 'lucide-react';
import { PageHeader, LoadingState, EmptyState, StudentCard } from '@/components/student/StudentUI';
import { createAddonNameSet, formatCustomizationMeta, groupCartItems } from '@/lib/order-display';

export default function CartPage() {
  const router = useRouter();
  const [cart, setCart] = useState<Cart | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [placing, setPlacing] = useState(false);
  const [error, setError] = useState('');

  const load = async () => {
    const [cartRes, menuRes] = await Promise.all([getCart(), getMenu()]);
    if (cartRes.success && cartRes.data) setCart(cartRes.data);
    if (menuRes.success && menuRes.data) setMenuItems(menuRes.data);
    setLoading(false);
  };

  useEffect(() => {
    load();
  }, []);

  const changeQty = async (id: number, qty: number) => {
    const res = await updateCartItem(id, qty);
    if (res.success && res.data) setCart(res.data);
  };

  const remove = async (id: number) => {
    const res = await removeCartItem(id);
    if (res.success && res.data) setCart(res.data);
  };

  const checkout = async () => {
    setPlacing(true);
    setError('');
    const res = await placeOrder();
    if (res.success) {
      router.push('/app/orders');
    } else {
      setError(res.error || 'Could not place order');
    }
    setPlacing(false);
  };

  const items = cart?.items || [];
  const addonNames = useMemo(() => createAddonNameSet(menuItems), [menuItems]);
  const groupedItems = useMemo(() => groupCartItems(items, addonNames), [items, addonNames]);

  if (loading) return <LoadingState message="Loading your cart..." />;

  return (
    <div className="space-y-5 animate-fade-in">
      <PageHeader
        title="Your Cart"
        subtitle={
          groupedItems.length > 0
            ? `${groupedItems.length} item${groupedItems.length !== 1 ? 's' : ''} ready to order`
            : undefined
        }
      />

      {error && (
        <div className="text-sm p-3.5 rounded-xl bg-[#EF4444]/10 text-[#EF4444] border border-[#EF4444]/20">
          {error}
        </div>
      )}

      {groupedItems.length === 0 ? (
        <EmptyState
          icon={ShoppingBag}
          title="Your cart is empty"
          description="Browse the menu and add your favorite drinks or treats."
          actionLabel="Browse Menu"
          actionHref="/app/menu"
        />
      ) : (
        <>
          <div className="space-y-3">
            {groupedItems.map((item) => {
              const meta = formatCustomizationMeta(item);
              const addonTotal = item.addons.reduce((sum, addon) => sum + Number(addon.lineTotal), 0);
              return (
                <StudentCard key={item.id} className="p-4">
                  <div className="flex justify-between gap-3">
                    <div className="flex-1 min-w-0">
                      <p className="font-bold text-gray-900">{item.itemName}</p>
                      {meta && (
                        <p className="text-xs text-gray-500 mt-0.5 italic">
                          &ldquo;{meta}&rdquo;
                        </p>
                      )}
                      {item.addons.length > 0 && (
                        <ul className="mt-2 space-y-1 text-xs text-gray-500">
                          {item.addons.map((addon) => (
                            <li key={addon.id}>+ {addon.itemName}</li>
                          ))}
                        </ul>
                      )}
                      <p className="text-[#001C98] font-bold text-sm mt-1.5">
                        ₱{(Number(item.lineTotal) + addonTotal).toFixed(2)}
                      </p>
                    </div>
                    <button
                      onClick={() => remove(item.id)}
                      className="p-2 rounded-lg text-[#EF4444]/70 hover:text-[#EF4444] hover:bg-[#EF4444]/5 transition shrink-0"
                      aria-label="Remove item"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                  <div className="flex items-center gap-3 mt-4">
                    <button
                      onClick={() => changeQty(item.id, item.quantity - 1)}
                      className="w-9 h-9 rounded-xl border border-[#E5D3B3]/60 bg-[#FDFBF7] flex items-center justify-center hover:bg-white transition"
                    >
                      <Minus className="w-4 h-4 text-gray-600" />
                    </button>
                    <span className="font-bold text-gray-900 w-6 text-center">{item.quantity}</span>
                    <button
                      onClick={() => changeQty(item.id, item.quantity + 1)}
                      className="w-9 h-9 rounded-xl border border-[#E5D3B3]/60 bg-[#FDFBF7] flex items-center justify-center hover:bg-white transition"
                    >
                      <Plus className="w-4 h-4 text-gray-600" />
                    </button>
                  </div>
                </StudentCard>
              );
            })}
          </div>

          <StudentCard className="p-5">
            <div className="flex justify-between items-center">
              <span className="font-medium text-gray-600">Subtotal</span>
              <span className="text-2xl font-bold text-[#001C98]">
                ₱{Number(cart?.subtotal || 0).toFixed(2)}
              </span>
            </div>
            <p className="text-xs text-gray-400 mt-1">Pay at pickup — no online payment needed.</p>
          </StudentCard>

          <button
            onClick={checkout}
            disabled={placing}
            className="w-full py-4 gradient-animated text-white font-bold rounded-2xl disabled:opacity-60 flex items-center justify-center gap-2 shadow-lg shadow-[#001C98]/25 hover:shadow-xl transition active:scale-[0.99]"
          >
            {placing ? (
              <Loader2 className="w-5 h-5 animate-spin" />
            ) : (
              'Place Order for Pickup'
            )}
          </button>
        </>
      )}
    </div>
  );
}
