'use client';

import { useEffect, useMemo, useState } from 'react';
import { getMenu, getMyOrders, type MenuItem, type Order } from '@/lib/api';
import { RefreshCw, ClipboardList, Check, ChefHat, Package, CircleDot } from 'lucide-react';
import { PageHeader, LoadingState, EmptyState, StudentCard } from '@/components/student/StudentUI';
import { cn } from '@/lib/utils';
import { createAddonNameSet, formatCustomizationMeta, groupOrderItems } from '@/lib/order-display';

const statusConfig: Record<
  string,
  { badge: string; label: string; step: number }
> = {
  pending: { badge: 'bg-gray-100 text-gray-600', label: 'Pending', step: 0 },
  preparing: { badge: 'bg-[#F59E0B]/15 text-[#F59E0B]', label: 'Preparing', step: 1 },
  ready: { badge: 'bg-[#10B981]/15 text-[#10B981]', label: 'Ready', step: 2 },
  completed: { badge: 'bg-gray-100 text-gray-400', label: 'Completed', step: 3 },
};

const steps = [
  { icon: CircleDot, label: 'Placed' },
  { icon: ChefHat, label: 'Preparing' },
  { icon: Package, label: 'Ready' },
  { icon: Check, label: 'Done' },
];

function OrderProgress({ status }: { status: string }) {
  const current = statusConfig[status]?.step ?? 0;
  const done = status === 'completed';

  return (
    <div className="flex items-center gap-1 mt-3 mb-1">
      {steps.map((step, i) => {
        const Icon = step.icon;
        const active = i <= current;
        const isLast = i === steps.length - 1;
        return (
          <div key={step.label} className="flex items-center flex-1 last:flex-none">
            <div className="flex flex-col items-center gap-1">
              <div
                className={cn(
                  'w-7 h-7 rounded-full flex items-center justify-center transition',
                  active
                    ? done && i === current
                      ? 'bg-gray-200 text-gray-400'
                      : 'bg-[#001C98] text-white shadow-sm shadow-[#001C98]/30'
                    : 'bg-gray-100 text-gray-300'
                )}
              >
                <Icon className="w-3.5 h-3.5" />
              </div>
              <span className={cn('text-[9px] font-medium', active ? 'text-gray-600' : 'text-gray-300')}>
                {step.label}
              </span>
            </div>
            {!isLast && (
              <div
                className={cn(
                  'h-0.5 flex-1 mx-1 mb-4 rounded-full',
                  i < current ? 'bg-[#001C98]/40' : 'bg-gray-100'
                )}
              />
            )}
          </div>
        );
      })}
    </div>
  );
}

export default function OrdersPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    const [ordersRes, menuRes] = await Promise.all([getMyOrders(), getMenu()]);
    if (ordersRes.success && ordersRes.data) setOrders(ordersRes.data);
    if (menuRes.success && menuRes.data) setMenuItems(menuRes.data);
    setLoading(false);
  };

  useEffect(() => {
    load();
    const interval = setInterval(load, 5000);
    return () => clearInterval(interval);
  }, []);

  const addonNames = useMemo(() => createAddonNameSet(menuItems), [menuItems]);

  if (loading) return <LoadingState message="Loading your orders..." />;

  return (
    <div className="space-y-5 animate-fade-in">
      <PageHeader
        title="My Orders"
        subtitle="Status updates automatically every 5 seconds."
        action={
          <button
            onClick={load}
            className="p-2.5 rounded-xl bg-white/80 border border-[#E5D3B3]/60 hover:bg-white transition shadow-sm"
            aria-label="Refresh orders"
          >
            <RefreshCw className="w-4 h-4 text-gray-500" />
          </button>
        }
      />

      {orders.length === 0 ? (
        <EmptyState
          icon={ClipboardList}
          title="No orders yet"
          description="Place your first order from the menu and track it here."
          actionLabel="Browse Menu"
          actionHref="/app/menu"
        />
      ) : (
        <div className="space-y-4">
          {orders.map((order) => {
            const config = statusConfig[order.status] || statusConfig.pending;
            return (
              <StudentCard key={order.id} className="p-4">
                <div className="flex justify-between items-start mb-1">
                  <div>
                    <p className="font-bold text-gray-900">{order.orderNumber}</p>
                    <p className="text-xs text-gray-400 mt-0.5">
                      {new Date(order.createdAt).toLocaleString()}
                    </p>
                  </div>
                  <span
                    className={cn(
                      'text-xs font-bold px-3 py-1 rounded-full capitalize',
                      config.badge
                    )}
                  >
                    {config.label}
                  </span>
                </div>

                {order.status !== 'completed' && <OrderProgress status={order.status} />}

                <ul className="text-sm text-gray-600 space-y-2 mt-3 pt-3 border-t border-[#E5D3B3]/40">
                  {groupOrderItems(order.items, addonNames).map((i) => (
                    <li key={i.id} className="flex justify-between gap-2">
                      <div>
                        <span>
                          <span className="font-semibold text-gray-800">{i.quantity}x</span> {i.itemName}
                          {formatCustomizationMeta(i) && (
                            <span className="text-gray-400 text-xs"> ({formatCustomizationMeta(i)})</span>
                          )}
                        </span>
                        {i.addons.length > 0 && (
                          <ul className="mt-1 ml-5 text-xs text-gray-500 space-y-1">
                            {i.addons.map((addon) => (
                              <li key={addon.id}>+ {addon.itemName}</li>
                            ))}
                          </ul>
                        )}
                      </div>
                    </li>
                  ))}
                </ul>
                <p className="font-bold text-[#001C98] text-lg mt-3">
                  ₱{Number(order.totalAmount).toFixed(2)}
                </p>
              </StudentCard>
            );
          })}
        </div>
      )}
    </div>
  );
}
