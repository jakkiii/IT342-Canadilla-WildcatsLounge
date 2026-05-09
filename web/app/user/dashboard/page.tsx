'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { CalendarDays, Coffee, ShoppingBasket } from 'lucide-react';
import { getMenuItems, getOrders, type MenuItemData, type OrderData, type UserData } from '@/lib/api';

type EventHighlight = {
  id: string;
  title: string;
  date: string;
  time: string;
  location: string;
};

const FEATURED_EVENTS: EventHighlight[] = [
  {
    id: 'open-mic',
    title: 'Open Mic Friday',
    date: '2026-05-16',
    time: '5:00 PM',
    location: 'Main Lounge',
  },
  {
    id: 'barista-lab',
    title: 'Barista Skills Lab',
    date: '2026-05-22',
    time: '2:00 PM',
    location: 'Brewing Lab',
  },
];

const formatCurrency = (value: number) =>
  new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP', minimumFractionDigits: 0 }).format(value);

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

export default function UserHomePage() {
  const router = useRouter();
  const [user, setUser] = useState<UserData | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItemData[]>([]);
  const [latestOrder, setLatestOrder] = useState<OrderData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const storedUser = getStoredUser();
    if (!storedUser?.id) {
      router.push('/user/login');
      return;
    }

    setUser(storedUser);

    const loadHome = async () => {
      setLoading(true);
      setError('');

      const [menuRes, orderRes] = await Promise.all([
        getMenuItems(),
        getOrders(storedUser.id),
      ]);

      if (menuRes.success && menuRes.data) {
        setMenuItems(menuRes.data);
      } else {
        setError(menuRes.error || 'Failed to load menu highlights.');
      }

      if (orderRes.success && orderRes.data && orderRes.data.length > 0) {
        setLatestOrder(orderRes.data[0]);
      }

      setLoading(false);
    };

    void loadHome();
  }, [router]);

  const featuredMenu = menuItems.find((item) => item.isAvailable) ?? menuItems[0];
  const featuredEvent = FEATURED_EVENTS[0];

  if (loading) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <div className="flex items-center gap-2 text-[#001C98]">
          <span className="h-4 w-4 rounded-full border-2 border-[#001C98] border-t-transparent animate-spin" />
          <span className="font-semibold">Loading your home feed...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {error && (
        <div className="rounded-xl border border-[#EF4444]/25 bg-[#EF4444]/10 text-[#EF4444] px-4 py-3 text-sm font-medium">
          {error}
        </div>
      )}

      <section className="bg-white border border-[#E5D3B3] rounded-2xl p-6 shadow-sm">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm text-gray-500">Welcome back</p>
            <h1 className="text-2xl font-bold text-[#10203B]">{user?.firstname}, ready for your next order?</h1>
          </div>
          <Link
            href="/user/dashboard/menu"
            className="inline-flex items-center justify-center rounded-xl bg-[#001C98] text-white px-4 py-2 text-sm font-semibold hover:bg-[#0025B8] transition"
          >
            Start order
          </Link>
        </div>
      </section>

      <div className="grid gap-6 lg:grid-cols-3">
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
            <p className="text-sm text-gray-500">No order yet. Start your first pickup order from the menu.</p>
          )}
        </div>

        <div className="bg-white border border-[#E5D3B3] rounded-2xl p-5 shadow-sm">
          <div className="flex items-center gap-2 mb-3">
            <ShoppingBasket className="w-5 h-5 text-[#001C98]" />
            <h2 className="text-lg font-bold text-[#10203B]">Featured Menu</h2>
          </div>
          {featuredMenu ? (
            <div>
              <p className="font-bold text-[#10203B]">{featuredMenu.name}</p>
              <p className="text-sm text-gray-500 mt-1">{featuredMenu.description}</p>
              <p className="text-lg font-extrabold text-[#001C98] mt-3">
                {formatCurrency(featuredMenu.price)}
              </p>
            </div>
          ) : (
            <p className="text-sm text-gray-500">Menu highlights will appear here once items are available.</p>
          )}
        </div>

        <div className="bg-white border border-[#E5D3B3] rounded-2xl p-5 shadow-sm">
          <div className="flex items-center gap-2 mb-3">
            <CalendarDays className="w-5 h-5 text-[#001C98]" />
            <h2 className="text-lg font-bold text-[#10203B]">Featured Event</h2>
          </div>
          {featuredEvent ? (
            <div>
              <p className="font-bold text-[#10203B]">{featuredEvent.title}</p>
              <p className="text-sm text-gray-500 mt-1">{featuredEvent.date} at {featuredEvent.time}</p>
              <p className="text-sm text-gray-500">{featuredEvent.location}</p>
              <Link
                href="/user/dashboard/events"
                className="inline-flex items-center gap-2 text-[#001C98] text-sm font-semibold mt-3"
              >
                View events
                <Coffee className="w-4 h-4" />
              </Link>
            </div>
          ) : (
            <p className="text-sm text-gray-500">Events will show up here once the admin posts them.</p>
          )}
        </div>
      </div>
    </div>
  );
}
