import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import AdminSidebar from '@/components/AdminSidebar';
import SessionCookieSync from '@/components/SessionCookieSync';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const cookieStore = cookies();
  const hasSession = cookieStore.get('wl_session')?.value === '1';
  const role = cookieStore.get('wl_role')?.value?.toLowerCase();

  if (!hasSession) {
    redirect('/login');
  }

  if (role !== 'staff') {
    redirect('/app');
  }

  return (
    <div className="min-h-screen flex bg-[#FDFBF7] font-poppins">
      <SessionCookieSync />
      <AdminSidebar />
      <main className="flex-1 p-6 lg:p-8 overflow-auto">{children}</main>
    </div>
  );
}
