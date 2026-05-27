'use client';

import { useEffect, useMemo, useState } from 'react';
import Link from 'next/link';
import {
  staffDashboard,
  staffGetMenu,
  staffGetOrderAnalytics,
  staffUpdateLoungeStatus,
  type StaffDashboard,
  type MenuItem,
  type StaffOrderAnalytics,
  type TopItemPoint,
  type TrendPoint,
  type StatusPoint,
} from '@/lib/api';
import { isAddonItem } from '@/lib/menu';
import { ClipboardList, Activity, Loader2, BarChart3, DollarSign, PieChart } from 'lucide-react';

const levels = [
  { key: 'low', label: 'Available', emoji: '🟢', desc: 'Plenty of seats' },
  { key: 'medium', label: 'Almost Full', emoji: '🟡', desc: 'Limited seating' },
  { key: 'full', label: 'Full', emoji: '🔴', desc: 'No seats available' },
];

const periods = [
  { key: 'daily', label: 'Daily' },
  { key: 'weekly', label: 'Weekly' },
  { key: 'monthly', label: 'Monthly' },
] as const;

type PeriodKey = (typeof periods)[number]['key'];

const statusMeta: Record<string, { label: string; color: string; bg: string }> = {
  pending: { label: 'Pending', color: '#6B7280', bg: 'bg-gray-500' },
  preparing: { label: 'Preparing', color: '#F59E0B', bg: 'bg-amber-500' },
  ready: { label: 'Ready', color: '#10B981', bg: 'bg-emerald-500' },
  completed: { label: 'Completed', color: '#001C98', bg: 'bg-[#001C98]' },
};

const activeStatuses = ['pending', 'preparing', 'ready'] as const;

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-PH', {
    style: 'currency',
    currency: 'PHP',
    maximumFractionDigits: 0,
  }).format(value);
}

function normalizeTrend(points: TrendPoint[]): TrendPoint[] {
  return points.map((point) => ({
    ...point,
    orders: Number(point.orders || 0),
    revenue: Number(point.revenue || 0),
  }));
}

function normalizeAnalytics(data: StaffOrderAnalytics): StaffOrderAnalytics {
  return {
    daily: normalizeTrend(data.daily || []),
    weekly: normalizeTrend(data.weekly || []),
    monthly: normalizeTrend(data.monthly || []),
    topItems: (data.topItems || []).map((item) => ({
      ...item,
      quantity: Number(item.quantity || 0),
      orderCount: Number(item.orderCount || 0),
    })),
    statusDistribution: (data.statusDistribution || []).map((item) => ({
      ...item,
      count: Number(item.count || 0),
    })),
  };
}

function filterAnalyticsTopItems(topItems: TopItemPoint[], menuItems: MenuItem[]): TopItemPoint[] {
  const addonNames = new Set(
    menuItems
      .filter(isAddonItem)
      .map((item) => item.name.trim().toLowerCase())
  );

  return topItems.filter((item) => !addonNames.has(item.itemName.trim().toLowerCase()));
}

export default function AdminDashboardPage() {
  const [data, setData] = useState<StaffDashboard | null>(null);
  const [analytics, setAnalytics] = useState<StaffOrderAnalytics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updating, setUpdating] = useState(false);
  const [loungeError, setLoungeError] = useState('');
  const [selectedPeriod, setSelectedPeriod] = useState<PeriodKey>('daily');

  const load = async () => {
    const [dashboardRes, analyticsRes, menuRes] = await Promise.all([
      staffDashboard(),
      staffGetOrderAnalytics(),
      staffGetMenu(),
    ]);

    if (dashboardRes.success && dashboardRes.data) {
      setData(dashboardRes.data);
    }

    if (analyticsRes.success && analyticsRes.data) {
      const normalizedAnalytics = normalizeAnalytics(analyticsRes.data);
      const filteredTopItems = menuRes.success && menuRes.data
        ? filterAnalyticsTopItems(normalizedAnalytics.topItems, menuRes.data)
        : normalizedAnalytics.topItems;

      setAnalytics({
        ...normalizedAnalytics,
        topItems: filteredTopItems,
      });
    }

    if (dashboardRes.success && analyticsRes.success) {
      setError('');
    } else if (loading || !data || !analytics) {
      setError(
        dashboardRes.error
          || analyticsRes.error
          || 'Could not reach staff analytics. Log in as staff and restart the backend.'
      );
    }

    setLoading(false);
  };

  useEffect(() => {
    load();
    const interval = setInterval(load, 10000);
    return () => clearInterval(interval);
  }, []);

  const setLevel = async (level: string) => {
    setUpdating(true);
    setLoungeError('');
    const res = await staffUpdateLoungeStatus(level);
    if (res.success && res.data) {
      setData((prev) => (prev ? { ...prev, loungeStatus: res.data! } : prev));
    } else {
      setLoungeError(res.error || 'Update failed');
    }
    setUpdating(false);
  };

  const status = data?.loungeStatus;
  const trendPoints = analytics?.[selectedPeriod] || [];
  const periodLabel = periods.find((period) => period.key === selectedPeriod)?.label || 'Daily';
  const totalOrdersInView = useMemo(
    () => trendPoints.reduce((sum, point) => sum + Number(point.orders || 0), 0),
    [trendPoints]
  );
  const totalRevenueInView = useMemo(
    () => trendPoints.reduce((sum, point) => sum + Number(point.revenue || 0), 0),
    [trendPoints]
  );

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-[#001C98]" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Staff Dashboard</h1>
        <p className="text-gray-500 text-sm">Manage lounge operations in real time.</p>
      </div>

      {error && (
        <div className="text-sm p-3.5 rounded-xl bg-[#EF4444]/10 text-[#EF4444] border border-[#EF4444]/20">
          {error}
        </div>
      )}

      <div className="grid sm:grid-cols-2 gap-4">
        <div className="bg-white rounded-2xl border border-[#E5D3B3] p-6">
          <div className="flex items-center gap-3 mb-2">
            <ClipboardList className="w-6 h-6 text-[#001C98]" />
            <span className="text-sm text-gray-500">Active Orders</span>
          </div>
          <p className="text-4xl font-bold text-[#001C98]">{data?.pendingOrders ?? 0}</p>
          <Link href="/admin/orders" className="text-xs text-[#001C98] font-semibold mt-2 inline-block">
            Manage orders →
          </Link>
        </div>
        <div className="bg-white rounded-2xl border border-[#E5D3B3] p-6 space-y-4">
          <div className="flex flex-col gap-4 xl:flex-row xl:items-start xl:justify-between">
            <div className="min-w-0">
              <div className="flex items-center gap-3 mb-2">
                <Activity className="w-6 h-6 text-[#001C98]" />
                <span className="text-sm text-gray-500">Lounge Status</span>
              </div>
              <p className="text-2xl font-bold text-gray-900">{status?.displayLabel || 'Available'}</p>
              <p className="text-xs text-gray-500 mt-1">Synced to student devices immediately.</p>
            </div>

            <div className="flex flex-col gap-2 w-full sm:w-56 xl:w-60">
              {levels.map((l) => (
                <button
                  key={l.key}
                  onClick={() => setLevel(l.key)}
                  disabled={updating}
                  className={`flex items-center gap-2 px-3 py-2 rounded-xl border-2 text-left transition disabled:opacity-60 ${
                    status?.occupancyLevel === l.key
                      ? 'border-[#001C98] bg-[#001C98]/5'
                      : 'border-[#E5D3B3] hover:border-[#001C98]/40'
                  }`}
                >
                  <span className="text-base shrink-0 leading-none">{l.emoji}</span>
                  <span className="text-sm font-bold leading-tight">{l.label}</span>
                </button>
              ))}
            </div>
          </div>

          {loungeError && (
            <div className="text-sm p-3 rounded-xl border bg-[#EF4444]/10 text-[#EF4444] border-[#EF4444]/20">
              {loungeError}
            </div>
          )}
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-[#E5D3B3] p-5">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 className="font-bold text-gray-900">Order Analytics</h2>
            <p className="text-sm text-gray-500">
              Track order volume, revenue, popular items, and status distribution.
            </p>
          </div>
          <div className="inline-flex rounded-xl border border-[#E5D3B3] p-1 bg-[#FDFBF7]">
            {periods.map((period) => (
              <button
                key={period.key}
                onClick={() => setSelectedPeriod(period.key)}
                className={`px-4 py-2 rounded-lg text-sm font-semibold transition ${
                  selectedPeriod === period.key
                    ? 'bg-[#001C98] text-white shadow-sm'
                    : 'text-gray-600 hover:bg-white'
                }`}
              >
                {period.label}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="grid xl:grid-cols-2 gap-4">
        <AnalyticsCard
          title="Orders trend"
          subtitle={`${periodLabel} trend view`}
          icon={<BarChart3 className="w-5 h-5 text-[#001C98]" />}
          stat={`${totalOrdersInView} orders`}
        >
          <VerticalTrendChart
            points={trendPoints}
            valueKey="orders"
            emptyMessage="No orders to chart yet."
            formatValue={(value) => `${value} orders`}
            barClassName="bg-gradient-to-t from-[#001C98] to-[#4A67E8]"
          />
        </AnalyticsCard>

        <AnalyticsCard
          title="Revenue trend"
          subtitle={`${periodLabel} sales totals`}
          icon={<DollarSign className="w-5 h-5 text-[#10B981]" />}
          stat={formatCurrency(totalRevenueInView)}
        >
          <VerticalTrendChart
            points={trendPoints}
            valueKey="revenue"
            emptyMessage="No revenue data available yet."
            formatValue={(value) => formatCurrency(value)}
            barClassName="bg-gradient-to-t from-[#10B981] to-[#34D399]"
          />
        </AnalyticsCard>
      </div>

      <div className="grid xl:grid-cols-2 gap-4">
        <AnalyticsCard
          title="Top-selling menu items"
          subtitle="By total quantity ordered"
          icon={<BarChart3 className="w-5 h-5 text-[#001C98]" />}
        >
          <TopItemsChart items={analytics?.topItems || []} />
        </AnalyticsCard>

        <AnalyticsCard
          title="Order status distribution"
          subtitle="Current mix across active orders"
          icon={<PieChart className="w-5 h-5 text-[#001C98]" />}
        >
          <StatusDonutChart items={analytics?.statusDistribution || []} />
        </AnalyticsCard>
      </div>

    </div>
  );
}

function AnalyticsCard({
  title,
  subtitle,
  icon,
  stat,
  children,
}: {
  title: string;
  subtitle?: string;
  icon: React.ReactNode;
  stat?: string;
  children: React.ReactNode;
}) {
  return (
    <div className="bg-white rounded-2xl border border-[#E5D3B3] p-5 space-y-4">
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-start gap-3">
          <div className="w-10 h-10 rounded-xl bg-[#001C98]/10 flex items-center justify-center shrink-0">
            {icon}
          </div>
          <div>
            <h3 className="font-bold text-gray-900">{title}</h3>
            {subtitle && <p className="text-sm text-gray-500 mt-1">{subtitle}</p>}
          </div>
        </div>
        {stat && <div className="text-sm font-semibold text-[#001C98] whitespace-nowrap">{stat}</div>}
      </div>
      {children}
    </div>
  );
}

function VerticalTrendChart({
  points,
  valueKey,
  formatValue,
  emptyMessage,
  barClassName,
}: {
  points: TrendPoint[];
  valueKey: 'orders' | 'revenue';
  formatValue: (value: number) => string;
  emptyMessage: string;
  barClassName: string;
}) {
  const values = points.map((point) => Number(point[valueKey] || 0));
  const maxValue = Math.max(...values, 0);

  if (points.length === 0 || maxValue === 0) {
    return <p className="text-sm text-gray-400 py-10 text-center">{emptyMessage}</p>;
  }

  return (
    <div className="space-y-3">
      <div className="h-64 flex items-end gap-2">
        {points.map((point) => {
          const value = Number(point[valueKey] || 0);
          const height = Math.max((value / maxValue) * 100, value > 0 ? 8 : 0);
          return (
            <div key={point.label} className="flex-1 min-w-0 flex flex-col justify-end items-center gap-2">
              <span className="text-[11px] font-semibold text-gray-500">
                {valueKey === 'orders' ? value : formatCurrency(value)}
              </span>
              <div className="w-full h-44 flex items-end">
                <div
                  title={`${point.label}: ${formatValue(value)}`}
                  className={`w-full rounded-t-xl transition-all ${barClassName}`}
                  style={{ height: `${height}%` }}
                />
              </div>
              <span className="text-[10px] leading-tight text-center text-gray-500 break-words">
                {point.label}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function TopItemsChart({ items }: { items: TopItemPoint[] }) {
  const visibleItems = items.slice(0, 3);
  const maxQuantity = Math.max(...visibleItems.map((item) => item.quantity), 0);

  if (visibleItems.length === 0 || maxQuantity === 0) {
    return <p className="text-sm text-gray-400 py-10 text-center">No sales data available yet.</p>;
  }

  return (
    <div className="space-y-4">
      {visibleItems.map((item) => {
        const width = `${(item.quantity / maxQuantity) * 100}%`;
        return (
          <div key={item.itemName} className="space-y-1.5">
            <div className="flex items-center justify-between gap-3">
              <div className="min-w-0">
                <p className="text-sm font-semibold text-gray-800 truncate">{item.itemName}</p>
                <p className="text-xs text-gray-500">{item.orderCount} orders</p>
              </div>
              <p className="text-sm font-bold text-[#001C98]">{item.quantity}</p>
            </div>
            <div className="h-3 rounded-full bg-[#E5D3B3]/40 overflow-hidden">
              <div
                className="h-full rounded-full bg-gradient-to-r from-[#001C98] to-[#4A67E8]"
                style={{ width }}
              />
            </div>
          </div>
        );
      })}
    </div>
  );
}

function StatusDonutChart({ items }: { items: StatusPoint[] }) {
  const ordered = activeStatuses.map((status) => {
    const item = items.find((entry) => entry.status === status);
    return { status, count: Number(item?.count || 0) };
  });
  const total = ordered.reduce((sum, item) => sum + item.count, 0);

  if (total === 0) {
    return <p className="text-sm text-gray-400 py-10 text-center">No order status data available yet.</p>;
  }

  const radius = 54;
  const circumference = 2 * Math.PI * radius;
  let offset = 0;

  return (
    <div className="flex flex-col lg:flex-row items-center gap-6">
      <div className="relative w-40 h-40 shrink-0">
        <svg viewBox="0 0 140 140" className="w-full h-full -rotate-90">
          <circle cx="70" cy="70" r={radius} fill="none" stroke="#F3F4F6" strokeWidth="16" />
          {ordered.map((item) => {
            const fraction = item.count / total;
            const dash = circumference * fraction;
            const segment = (
              <circle
                key={item.status}
                cx="70"
                cy="70"
                r={radius}
                fill="none"
                stroke={statusMeta[item.status].color}
                strokeWidth="16"
                strokeDasharray={`${dash} ${circumference - dash}`}
                strokeDashoffset={-offset}
                strokeLinecap="butt"
              />
            );
            offset += dash;
            return segment;
          })}
        </svg>
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <p className="text-3xl font-bold text-gray-900">{total}</p>
          <p className="text-xs text-gray-500">Active orders</p>
        </div>
      </div>

      <div className="flex-1 space-y-3 w-full">
        {ordered.map((item) => (
          <div key={item.status} className="flex items-center justify-between gap-3">
            <div className="flex items-center gap-2 min-w-0">
              <span className={`w-3 h-3 rounded-full ${statusMeta[item.status].bg}`} />
              <span className="text-sm text-gray-700">{statusMeta[item.status].label}</span>
            </div>
            <div className="text-right">
              <p className="text-sm font-semibold text-gray-900">{item.count}</p>
              <p className="text-xs text-gray-500">
                {((item.count / total) * 100).toFixed(0)}%
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
