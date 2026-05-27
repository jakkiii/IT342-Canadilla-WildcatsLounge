import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import SessionCookieSync from '@/components/SessionCookieSync';
import StudentNav from '@/components/StudentNav';

export default function AppLayout({ children }: { children: React.ReactNode }) {
  const cookieStore = cookies();
  const hasSession = cookieStore.get('wl_session')?.value === '1';
  const role = cookieStore.get('wl_role')?.value?.toLowerCase();

  if (!hasSession) {
    redirect('/login');
  }

  if (role === 'staff') {
    redirect('/admin');
  }

  return (
    <div className="min-h-screen student-bg font-poppins">
      <SessionCookieSync />
      <StudentNav />
      <main className="student-content lg:ml-60 w-full lg:w-[calc(100%-15rem)] pb-24 lg:pb-10 px-4 sm:px-6 lg:px-8 xl:px-10 2xl:px-12 py-6">
        {children}
      </main>
    </div>
  );
}
