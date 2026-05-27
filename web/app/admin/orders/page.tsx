'use client';

import { useEffect, useMemo, useState } from 'react';
import { staffGetMenu, staffGetOrders, staffUpdateOrderStatus, type Order, type MenuItem } from '@/lib/api';
import { Loader2, RefreshCw } from 'lucide-react';
import { createAddonNameSet, formatCustomizationMeta, groupOrderItems } from '@/lib/order-display';

const columns = [
  { key: 'pending', label: 'Pending', next: 'preparing', btn: 'Start Preparing' },
  { key: 'preparing', label: 'Preparing', next: 'ready', btn: 'Mark Ready' },
  { key: 'ready', label: 'Ready for Pickup', next: 'completed', btn: 'Picked Up' },
];

function normalizeOrderStatus(status: string): string {
  return (status || '').trim().toLowerCase();
}

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState<number | null>(null);
  const [error, setError] = useState('');

  const load = async () => {
    const [ordersRes, menuRes] = await Promise.all([staffGetOrders(), staffGetMenu()]);
    if (ordersRes.success && ordersRes.data) {
      setOrders(
        ordersRes.data.map((o) => ({ ...o, status: normalizeOrderStatus(o.status) }))
      );
      if (menuRes.success && menuRes.data) {
        setMenuItems(menuRes.data);
      }
      setError('');
    } else {
      setError(ordersRes.error || 'Could not load orders. Sign in as staff and ensure the backend is running.');
    }
    setLoading(false);
  };

  useEffect(() => {
    load();
    const interval = setInterval(load, 5000);
    return () => clearInterval(interval);
  }, []);

  const advance = async (id: number, status: string) => {
    setUpdating(id);
    setError('');
    const res = await staffUpdateOrderStatus(id, status);
    if (!res.success) {
      setError(res.error || 'Could not update order status');
    }
    await load();
    setUpdating(null);
  };

  const addonNames = useMemo(() => createAddonNameSet(menuItems), [menuItems]);

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-[#001C98]" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Active Orders</h1>
        <button onClick={load} className="p-2 rounded-lg hover:bg-gray-100">
          <RefreshCw className="w-4 h-4" />
        </button>
      </div>
      <p className="text-xs text-gray-500">Auto-refreshes every 5 seconds. Students see status updates live.</p>

      {error && (
        <div className="text-sm p-3.5 rounded-xl bg-[#EF4444]/10 text-[#EF4444] border border-[#EF4444]/20">
          {error}
        </div>
      )}

      <div className="grid lg:grid-cols-3 gap-4">
        {columns.map((col) => {
          const colOrders = orders.filter((o) => o.status === col.key);
          return (
            <div key={col.key} className="bg-white rounded-2xl border border-[#E5D3B3] p-4 min-h-[200px]">
              <h2 className="font-bold text-sm text-gray-700 mb-3 flex justify-between">
                {col.label}
                <span className="bg-[#001C98]/10 text-[#001C98] px-2 py-0.5 rounded-full text-xs">
                  {colOrders.length}
                </span>
              </h2>
              <div className="space-y-3">
                {colOrders.map((order) => (
                  <div key={order.id} className="border border-gray-100 rounded-xl p-3 text-sm">
                    <p className="font-bold">{order.orderNumber}</p>
                    <p className="text-xs text-gray-500">{order.customerName}</p>
                    {order.customerStudentId && (
                      <p className="text-xs text-gray-400">{order.customerStudentId}</p>
                    )}
                    <ul className="mt-2 text-xs text-gray-600">
                      {groupOrderItems(order.items, addonNames).map((i) => (
                        <li key={i.id} className="mb-1.5 last:mb-0">
                          <div>
                            {i.quantity}x {i.itemName}
                            {formatCustomizationMeta(i) && ` — ${formatCustomizationMeta(i)}`}
                          </div>
                          {i.addons.length > 0 && (
                            <ul className="mt-1 ml-4 text-[11px] text-gray-500 space-y-0.5">
                              {i.addons.map((addon) => (
                                <li key={addon.id}>+ {addon.itemName}</li>
                              ))}
                            </ul>
                          )}
                        </li>
                      ))}
                    </ul>
                    <p className="font-semibold text-[#001C98] mt-1">
                      ₱{Number(order.totalAmount).toFixed(2)}
                    </p>
                    <button
                      onClick={() => advance(order.id, col.next)}
                      disabled={updating === order.id}
                      className="mt-2 w-full py-2 bg-[#001C98] text-white text-xs font-semibold rounded-lg disabled:opacity-60"
                    >
                      {updating === order.id ? '...' : col.btn}
                    </button>
                  </div>
                ))}
                {colOrders.length === 0 && (
                  <p className="text-xs text-gray-400 text-center py-4">No orders</p>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
