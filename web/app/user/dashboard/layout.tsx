'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { CalendarDays, Coffee, Home, LogOut, ShoppingCart, User, Utensils } from 'lucide-react';
import type { UserData } from '@/lib/api';

type TabItem = {
  href: string;
  label: string;
  icon: typeof Home;
};

const tabs: TabItem[] = [
  { href: '/user/dashboard', label: 'Home', icon: Home },
  { href: '/user/dashboard/menu', label: 'Menu', icon: Utensils },
  { href: '/user/dashboard/cart', label: 'Cart', icon: ShoppingCart },
  { href: '/user/dashboard/events', label: 'Events', icon: CalendarDays },
  { href: '/user/dashboard/profile', label: 'Profile', icon: User },
];

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

export default function UserDashboardLayout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const [user, setUser] = useState<UserData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = getStoredUser();
    if (!storedUser?.id) {
      router.push('/user/login');
      return;
    }
    setUser(storedUser);
    setLoading(false);
  }, [router]);

  useEffect(() => {
    const handleUserUpdate = () => setUser(getStoredUser());
    window.addEventListener('wl-user-updated', handleUserUpdate);
    return () => window.removeEventListener('wl-user-updated', handleUserUpdate);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    router.push('/user/login');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#FDFBF7] font-poppins flex items-center justify-center">
        <div className="flex items-center gap-2 text-[#001C98]">
          <span className="h-4 w-4 rounded-full border-2 border-[#001C98] border-t-transparent animate-spin" />
          <span className="font-semibold">Loading your lounge dashboard...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen bg-[#FDFBF7] font-poppins flex flex-col overflow-hidden">
      <header className="bg-[#001C98] text-white px-6 h-16 flex items-center justify-between shadow-lg">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-white/20 flex items-center justify-center">
            <Coffee className="w-5 h-5 text-white" />
          </div>
          <span className="text-lg font-bold tracking-wide">Wildcats Lounge</span>
        </div>
        <p className="text-sm text-white/85">Good day, {user?.firstname || 'Guest'}</p>
      </header>

      <main className="flex-1 w-full overflow-hidden">
        <aside className="fixed left-0 top-16 h-[calc(100vh-4rem)] w-64 bg-white border-r border-[#E5D3B3] px-4 py-6 shadow-sm flex flex-col gap-4 overflow-hidden">
          <nav className="flex flex-col gap-2">
            {tabs.map((tab) => {
              const Icon = tab.icon;
              const isActive = tab.href === '/user/dashboard'
                ? pathname === tab.href
                : pathname.startsWith(tab.href);

              return (
                <Link
                  key={tab.href}
                  href={tab.href}
                  className={`flex items-center gap-2 px-3 py-2 rounded-xl text-sm font-semibold transition ${
                    isActive
                      ? 'bg-[#001C98] text-white'
                      : 'text-[#001C98] hover:bg-[#E5D3B3]/40'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  {tab.label}
                </Link>
              );
            })}
          </nav>

          <button
            onClick={handleLogout}
            className="mt-auto flex items-center justify-center gap-2 bg-[#001C98] hover:bg-[#0025B8] text-white text-sm font-semibold px-4 py-2 rounded-xl transition"
          >
            <LogOut className="w-4 h-4" />
            Logout
          </button>
        </aside>

        <section className="ml-64 h-full px-4 lg:px-6 py-6 overflow-y-auto">{children}</section>
      </main>
    </div>
  );
}
