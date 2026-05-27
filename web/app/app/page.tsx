'use client';

import { useEffect, useMemo, useState } from 'react';
import Link from 'next/link';
import { useClientUser } from '@/lib/auth';
import { getLoungeStatus, getMenu, getMyOrders, getTodayEvents, type LoungeStatus, type Event, type Order, type MenuItem } from '@/lib/api';
import {
  Activity,
  Coffee,
  Calendar,
  RefreshCw,
  ClipboardList,
  Sparkles,
  ExternalLink,
  Check,
  ChefHat,
  Package,
  CircleDot,
} from 'lucide-react';
import { StudentCard } from '@/components/student/StudentUI';
import { cn } from '@/lib/utils';
import { createAddonNameSet, formatCustomizationMeta, groupOrderItems } from '@/lib/order-display';

const statusStyles: Record<string, { card: string; dot: string; label: string }> = {
  green: {
    card: 'bg-gradient-to-br from-[#10B981]/12 to-[#10B981]/5 border-[#10B981]/30',
    dot: 'bg-[#10B981]',
    label: 'text-[#10B981]',
  },
  yellow: {
    card: 'bg-gradient-to-br from-[#F59E0B]/12 to-[#F59E0B]/5 border-[#F59E0B]/30',
    dot: 'bg-[#F59E0B]',
    label: 'text-[#F59E0B]',
  },
  red: {
    card: 'bg-gradient-to-br from-[#EF4444]/12 to-[#EF4444]/5 border-[#EF4444]/30',
    dot: 'bg-[#EF4444]',
    label: 'text-[#EF4444]',
  },
};

const orderStatusConfig: Record<string, { badge: string; label: string; step: number }> = {
  pending: { badge: 'bg-gray-100 text-gray-600', label: 'Pending', step: 0 },
  preparing: { badge: 'bg-[#F59E0B]/15 text-[#F59E0B]', label: 'Preparing', step: 1 },
  ready: { badge: 'bg-[#10B981]/15 text-[#10B981]', label: 'Ready', step: 2 },
  completed: { badge: 'bg-gray-100 text-gray-400', label: 'Completed', step: 3 },
};

const orderSteps = [
  { icon: CircleDot, label: 'Placed' },
  { icon: ChefHat, label: 'Preparing' },
  { icon: Package, label: 'Ready' },
  { icon: Check, label: 'Done' },
];

function getGreeting() {
  const hour = new Date().getHours();
  if (hour < 12) return 'Good morning';
  if (hour < 17) return 'Good afternoon';
  return 'Good evening';
}

function OrderProgress({ status }: { status: string }) {
  const current = orderStatusConfig[status]?.step ?? 0;
  const done = status === 'completed';

  return (
    <div className="flex items-center gap-1 mt-4 mb-1">
      {orderSteps.map((step, i) => {
        const Icon = step.icon;
        const active = i <= current;
        const isLast = i === orderSteps.length - 1;

        return (
          <div key={step.label} className="flex items-center flex-1 last:flex-none">
            <div className="flex flex-col items-center gap-1">
              <div
                className={cn(
                  'w-8 h-8 rounded-full flex items-center justify-center transition',
                  active
                    ? done && i === current
                      ? 'bg-gray-200 text-gray-400'
                      : 'bg-[#001C98] text-white shadow-sm shadow-[#001C98]/30'
                    : 'bg-gray-100 text-gray-300'
                )}
              >
                <Icon className="w-4 h-4" />
              </div>
              <span className={cn('text-[10px] font-medium', active ? 'text-gray-600' : 'text-gray-300')}>
                {step.label}
              </span>
            </div>
            {!isLast && (
              <div
                className={cn(
                  'h-0.5 flex-1 mx-1 mb-5 rounded-full',
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

export default function StudentHomePage() {
  const { user, ready } = useClientUser();
  const [greeting, setGreeting] = useState('Welcome');
  const [status, setStatus] = useState<LoungeStatus | null>(null);
  const [events, setEvents] = useState<Event[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setGreeting(getGreeting());
  }, []);

  const load = async () => {
    setLoading(true);
    const [s, e, o, m] = await Promise.all([getLoungeStatus(), getTodayEvents(), getMyOrders(), getMenu()]);
    if (s.success && s.data) setStatus(s.data);
    if (e.success && e.data) setEvents(e.data);
    if (o.success && o.data) setOrders(o.data);
    if (m.success && m.data) setMenuItems(m.data);
    setLoading(false);
  };

  useEffect(() => {
    load();
    const interval = setInterval(load, 10000);
    return () => clearInterval(interval);
  }, []);

  const style = status ? statusStyles[status.color] || statusStyles.green : null;
  const activeOrder = orders.find((order) => ['pending', 'preparing', 'ready'].includes(order.status));
  const activeOrderConfig = activeOrder ? orderStatusConfig[activeOrder.status] || orderStatusConfig.pending : null;
  const addonNames = useMemo(() => createAddonNameSet(menuItems), [menuItems]);
  const activeOrderItems = useMemo(
    () => (activeOrder ? groupOrderItems(activeOrder.items, addonNames) : []),
    [activeOrder, addonNames]
  );

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="relative overflow-hidden rounded-3xl gradient-animated p-6 sm:p-7 text-white shadow-xl shadow-[#001C98]/25">
        <div className="absolute -top-10 -right-10 w-40 h-40 rounded-full bg-white/10 blur-2xl pointer-events-none" />
        <div className="absolute -bottom-8 -left-8 w-32 h-32 rounded-full bg-white/5 blur-xl pointer-events-none" />
        <div className="relative z-10">
          <div className="flex items-center gap-2 text-white/70 text-xs font-medium mb-2">
            <Sparkles className="w-3.5 h-3.5" />
            {greeting}
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">
            Hello, {ready && user?.firstname ? user.firstname : 'there'}! 👋
          </h1>
          <p className="text-white/70 text-sm mt-2 max-w-sm">
            Grab a drink, check the lounge vibe, and plan your campus break.
          </p>
        </div>
      </div>

      <div
        className={`rounded-2xl border p-5 transition-all ${
          style ? style.card : 'bg-white/80 border-[#E5D3B3]/60'
        }`}
      >
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-2xl bg-white/60 flex items-center justify-center shadow-sm">
              <Activity className={`w-6 h-6 ${style?.label || 'text-gray-600'}`} />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <p className="text-xs font-semibold uppercase tracking-wider text-gray-500">
                  Lounge Occupancy
                </p>
                {style && (
                  <span className={`w-2 h-2 rounded-full ${style.dot} animate-pulse`} />
                )}
              </div>
              <p className={`text-xl font-bold mt-0.5 ${style?.label || 'text-gray-900'}`}>
                {loading ? 'Checking...' : status?.displayLabel || 'Available'}
              </p>
            </div>
          </div>
          <button
            onClick={load}
            className="p-2.5 rounded-xl bg-white/60 hover:bg-white/80 transition shadow-sm"
            aria-label="Refresh status"
          >
            <RefreshCw className={`w-4 h-4 text-gray-600 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {activeOrder && activeOrderConfig && (
        <StudentCard className="p-5">
          <div className="flex items-start justify-between gap-3">
            <div className="flex items-start gap-3">
              <div className="w-11 h-11 rounded-2xl bg-[#001C98]/10 flex items-center justify-center shrink-0">
                <ClipboardList className="w-5 h-5 text-[#001C98]" />
              </div>
              <div>
                <p className="text-xs font-semibold uppercase tracking-wider text-gray-500">Current Order</p>
                <p className="font-bold text-gray-900 mt-1">{activeOrder.orderNumber}</p>
                <p className="text-xs text-gray-400 mt-1">
                  {new Date(activeOrder.createdAt).toLocaleString()}
                </p>
              </div>
            </div>
            <span className={cn('text-xs font-bold px-3 py-1 rounded-full capitalize', activeOrderConfig.badge)}>
              {activeOrderConfig.label}
            </span>
          </div>

          <OrderProgress status={activeOrder.status} />

          <ul className="text-sm text-gray-600 space-y-2 mt-4 pt-4 border-t border-[#E5D3B3]/40">
            {activeOrderItems.slice(0, 3).map((item) => {
              const meta = formatCustomizationMeta(item);
              return (
                <li key={item.id}>
                  <div>
                    <span className="font-semibold text-gray-800">{item.quantity}x</span> {item.itemName}
                    {meta && (
                      <span className="text-gray-400 text-xs"> ({meta})</span>
                    )}
                  </div>
                  {item.addons.length > 0 && (
                    <ul className="mt-1 ml-5 space-y-1 text-xs text-gray-500">
                      {item.addons.map((addon) => (
                        <li key={addon.id}>+ {addon.itemName}</li>
                      ))}
                    </ul>
                  )}
                </li>
              );
            })}
            {activeOrderItems.length > 3 && (
              <li className="text-xs text-gray-400">+{activeOrderItems.length - 3} more item(s)</li>
            )}
          </ul>

          <div className="flex items-center justify-between gap-3 mt-4">
            <p className="font-bold text-[#001C98] text-lg">₱{Number(activeOrder.totalAmount).toFixed(2)}</p>
            <Link href="/app/orders" className="text-xs font-semibold text-[#001C98] hover:underline">
              View all orders →
            </Link>
          </div>
        </StudentCard>
      )}

      <Link
        href="/app/menu"
        className="flex items-center justify-center gap-2.5 w-full gradient-animated text-white font-semibold py-4 rounded-2xl shadow-lg shadow-[#001C98]/25 hover:shadow-xl hover:shadow-[#001C98]/30 transition-all active:scale-[0.99]"
      >
        <Coffee className="w-5 h-5" />
        Start Order
      </Link>

      {events.length > 0 && (
        <StudentCard className="p-5">
          <div className="flex items-center gap-2.5 mb-4">
            <div className="w-8 h-8 rounded-lg bg-[#001C98]/10 flex items-center justify-center">
              <Calendar className="w-4 h-4 text-[#001C98]" />
            </div>
            <h2 className="font-bold text-gray-900">Today at the Lounge</h2>
          </div>
          <div className="space-y-3">
            {events.map((ev) => (
              <div
                key={ev.id}
                className="flex items-start gap-3 p-3 rounded-xl bg-[#FDFBF7] border border-[#E5D3B3]/40"
              >
                <div className="w-1 self-stretch rounded-full bg-[#001C98]/30 shrink-0" />
                <div>
                  <p className="font-semibold text-sm text-gray-900">{ev.title}</p>
                  <p className="text-xs text-gray-500 mt-0.5">
                    {new Date(ev.startDatetime).toLocaleTimeString([], {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}{' '}
                    –{' '}
                    {new Date(ev.endDatetime).toLocaleTimeString([], {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </p>
                  {ev.postLink && (
                    <a
                      href={ev.postLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-flex items-center gap-1 text-xs text-[#001C98] font-semibold mt-1 hover:underline"
                    >
                      <ExternalLink className="w-3 h-3" />
                      View post
                    </a>
                  )}
                </div>
              </div>
            ))}
          </div>
          <Link
            href="/app/events"
            className="inline-flex items-center gap-1 text-[#001C98] text-xs font-semibold mt-4 hover:underline"
          >
            View all events →
          </Link>
        </StudentCard>
      )}
    </div>
  );
}
