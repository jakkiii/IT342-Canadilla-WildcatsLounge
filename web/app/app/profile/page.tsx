'use client';

import { useRouter } from 'next/navigation';
import { useClientUser, clearSession } from '@/lib/auth';
import { UserCircle, Mail, Hash, Shield, LogOut } from 'lucide-react';
import { PageHeader, LoadingState, StudentCard } from '@/components/student/StudentUI';

export default function ProfilePage() {
  const router = useRouter();
  const { user, ready } = useClientUser();

  const logout = () => {
    clearSession();
    router.push('/login');
  };

  if (!ready) return <LoadingState message="Loading profile..." />;
  if (!user) return null;

  const initials = `${user.firstname?.[0] || ''}${user.lastname?.[0] || ''}`.toUpperCase();

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader title="Profile" subtitle="Your account details." />

      <div className="relative overflow-hidden rounded-3xl gradient-animated p-6 text-white shadow-xl shadow-[#001C98]/25">
        <div className="absolute -top-8 -right-8 w-32 h-32 rounded-full bg-white/10 blur-2xl pointer-events-none" />
        <div className="relative z-10 flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-white/20 backdrop-blur-sm flex items-center justify-center text-2xl font-bold shadow-lg">
            {initials || '?'}
          </div>
          <div>
            <p className="text-xl font-bold">
              {user.firstname} {user.lastname}
            </p>
            <p className="text-white/70 text-sm mt-0.5">{user.email}</p>
            {user.role && (
              <span className="inline-block mt-2 text-[10px] font-semibold uppercase tracking-wider bg-white/20 px-2.5 py-1 rounded-full capitalize">
                {user.role}
              </span>
            )}
          </div>
        </div>
      </div>

      <StudentCard className="overflow-hidden divide-y divide-[#E5D3B3]/40">
        <Row icon={UserCircle} label="Name" value={`${user.firstname} ${user.lastname}`} />
        <Row icon={Mail} label="Email" value={user.email} />
        {user.studentId && <Row icon={Hash} label="Student ID" value={user.studentId} />}
        {user.role && <Row icon={Shield} label="Role" value={user.role} capitalize />}
      </StudentCard>

      <button
        onClick={logout}
        className="flex items-center justify-center gap-2 w-full py-3.5 border-2 border-[#EF4444]/25 text-[#EF4444] font-semibold rounded-2xl hover:bg-[#EF4444]/5 transition"
      >
        <LogOut className="w-4 h-4" /> Logout
      </button>
    </div>
  );
}

function Row({
  icon: Icon,
  label,
  value,
  capitalize,
}: {
  icon: React.ComponentType<{ className?: string }>;
  label: string;
  value: string;
  capitalize?: boolean;
}) {
  return (
    <div className="flex items-center gap-4 px-5 py-4 hover:bg-[#FDFBF7]/50 transition">
      <div className="w-10 h-10 rounded-xl bg-[#001C98]/8 flex items-center justify-center">
        <Icon className="w-5 h-5 text-[#001C98]" />
      </div>
      <div>
        <p className="text-xs text-gray-400 font-medium">{label}</p>
        <p className={`text-sm font-semibold text-gray-800 ${capitalize ? 'capitalize' : ''}`}>
          {value}
        </p>
      </div>
    </div>
  );
}
