'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Home, Coffee, ShoppingCart, Calendar, User, LogOut } from 'lucide-react';
import { clearSession } from '@/lib/auth';
import { useRouter } from 'next/navigation';
import { cn } from '@/lib/utils';

const links = [
  { href: '/app', label: 'Home', icon: Home },
  { href: '/app/menu', label: 'Menu', icon: Coffee },
  { href: '/app/cart', label: 'Cart', icon: ShoppingCart },
  { href: '/app/events', label: 'Events', icon: Calendar },
  { href: '/app/profile', label: 'Profile', icon: User },
];

export default function StudentNav() {
  const pathname = usePathname();
  const router = useRouter();

  const logout = () => {
    clearSession();
    router.push('/login');
  };

  const isActive = (href: string) =>
    pathname === href || (href !== '/app' && pathname.startsWith(href));

  return (
    <>
      <header className="lg:hidden gradient-animated text-white px-4 py-3.5 flex items-center justify-between sticky top-0 z-50 shadow-lg shadow-[#001C98]/20">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-xl bg-white/20 backdrop-blur-sm flex items-center justify-center">
            <Coffee className="w-4 h-4" />
          </div>
          <span className="font-bold text-sm tracking-wide">Wildcats Lounge</span>
        </div>
        <button
          onClick={logout}
          className="p-2 rounded-xl bg-white/15 hover:bg-white/25 transition"
          aria-label="Logout"
        >
          <LogOut className="w-4 h-4" />
        </button>
      </header>

      <nav className="fixed bottom-0 left-0 right-0 lg:top-0 lg:bottom-auto lg:w-60 lg:h-screen bg-white/90 backdrop-blur-md border-t lg:border-t-0 lg:border-r border-[#E5D3B3]/70 z-50 flex lg:flex-col shadow-[0_-4px_24px_rgba(0,28,152,0.08)] lg:shadow-none">
        <div className="hidden lg:block px-5 pt-6 pb-5">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl gradient-animated flex items-center justify-center shadow-md shadow-[#001C98]/30">
              <Coffee className="w-5 h-5 text-white" />
            </div>
            <div>
              <span className="font-bold text-[#001C98] text-sm block leading-tight">Wildcats Lounge</span>
              <span className="text-[10px] text-gray-400 font-medium">Student Portal</span>
            </div>
          </div>
        </div>

        <div className="flex lg:flex-col flex-1 justify-around lg:justify-start lg:gap-0.5 lg:px-3 lg:pt-2">
          {links.map(({ href, label, icon: Icon }) => {
            const active = isActive(href);
            return (
              <Link
                key={href}
                href={href}
                className={cn(
                  'relative flex flex-col lg:flex-row items-center gap-1 lg:gap-3 px-3 py-2.5 lg:py-3 lg:px-4 rounded-xl text-[10px] lg:text-sm font-medium transition-all duration-200',
                  active
                    ? 'text-[#001C98] bg-[#001C98]/8 lg:bg-[#001C98]/10'
                    : 'text-gray-400 hover:text-gray-700 hover:bg-gray-50/80'
                )}
              >
                {active && (
                  <span className="hidden lg:block absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 rounded-r-full bg-[#001C98]" />
                )}
                <Icon className={cn('w-5 h-5 lg:w-[18px] lg:h-[18px]', active && 'drop-shadow-sm')} />
                {label}
              </Link>
            );
          })}
        </div>

        <button
          onClick={logout}
          className="hidden lg:flex items-center gap-2.5 mx-3 mb-5 px-4 py-2.5 text-sm text-gray-400 hover:text-[#EF4444] hover:bg-[#EF4444]/5 rounded-xl transition"
        >
          <LogOut className="w-4 h-4" /> Logout
        </button>
      </nav>
    </>
  );
}
