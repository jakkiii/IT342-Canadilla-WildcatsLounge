'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import type { UserData } from '@/lib/api';
import {
  LayoutDashboard,
  ClipboardList,
  UtensilsCrossed,
  Calendar,
  LogOut,
  Coffee,
} from 'lucide-react';
import { clearSession, getUser } from '@/lib/auth';

const links = [
  { href: '/admin', label: 'Dashboard', icon: LayoutDashboard },
  { href: '/admin/orders', label: 'Orders', icon: ClipboardList },
  { href: '/admin/menu', label: 'Inventory', icon: UtensilsCrossed },
  { href: '/admin/events', label: 'Events', icon: Calendar },
];

export default function AdminSidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const [user, setUser] = useState<UserData | null>(null);

  useEffect(() => {
    setUser(getUser());
  }, []);

  const logout = () => {
    clearSession();
    router.push('/login');
  };

  return (
    <aside className="w-64 min-h-screen bg-[#001C98] text-white flex flex-col shrink-0">
      <div className="px-5 py-6 border-b border-white/10">
        <div className="flex items-center gap-2">
          <div className="w-10 h-10 rounded-xl bg-white/20 flex items-center justify-center">
            <Coffee className="w-5 h-5" />
          </div>
          <div>
            <p className="font-bold text-sm">Staff Portal</p>
            <p className="text-white/60 text-xs">Wildcats Lounge</p>
          </div>
        </div>
      </div>
      <nav className="flex-1 p-3 space-y-1">
        {links.map(({ href, label, icon: Icon }) => {
          const active = pathname === href;
          return (
            <Link
              key={href}
              href={href}
              className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition ${
                active ? 'bg-white text-[#001C98]' : 'text-white/80 hover:bg-white/10'
              }`}
            >
              <Icon className="w-4 h-4" />
              {label}
            </Link>
          );
        })}
      </nav>
      <div className="p-4 border-t border-white/10">
        {user && (
          <p className="text-xs text-white/60 mb-2 truncate">
            {user.firstname} {user.lastname}
          </p>
        )}
        <button
          onClick={logout}
          className="flex items-center gap-2 w-full px-4 py-2 text-sm rounded-xl bg-white/10 hover:bg-white/20"
        >
          <LogOut className="w-4 h-4" /> Logout
        </button>
      </div>
    </aside>
  );
}
