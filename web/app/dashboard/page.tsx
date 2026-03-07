'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { UserCircle, Mail, Hash, Shield, LogOut, Coffee, Check } from 'lucide-react';
import type { UserData } from '@/lib/api';

export default function DashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserData | null>(null);

  useEffect(() => {
    // Get user from localStorage
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    } else {
      // Redirect to login if no user found
      router.push('/login');
    }
  }, [router]);

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    router.push('/login');
  };

  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#FDFBF7] font-poppins">

      {/* ── HEADER ── */}
      <header className="bg-[#001C98] text-white px-6 py-4 flex items-center justify-between shadow-lg">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-white/20 flex items-center justify-center">
            <Coffee className="w-5 h-5 text-white" />
          </div>
          <span className="text-lg font-bold tracking-wide">Wildcats Lounge</span>
        </div>
        <button
          onClick={handleLogout}
          className="flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white text-sm font-medium px-4 py-2 rounded-xl transition"
        >
          <LogOut className="w-4 h-4" />
          Logout
        </button>
      </header>

      {/* ── MAIN ── */}
      <main className="max-w-3xl mx-auto px-6 py-10">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">Welcome back, {user.firstname}! 👋</h1>
          <p className="text-gray-500 text-sm mt-1">You are now logged in to Wildcats Lounge.</p>
        </div>

        {/* Success banner */}
        <div className="flex items-center gap-3 bg-[#10B981]/10 border border-[#10B981]/30 rounded-2xl px-5 py-4 mb-6">
          <div className="w-8 h-8 rounded-full bg-[#10B981]/20 flex items-center justify-center flex-shrink-0">
            <Check className="w-4 h-4 text-[#10B981]" />
          </div>
          <div>
            <p className="text-sm font-semibold text-[#10B981]">Login Successful</p>
            <p className="text-xs text-gray-500">Your session is active and secured with JWT.</p>
          </div>
        </div>

        {/* Profile card */}
        <div className="bg-white rounded-2xl border border-[#E5D3B3] shadow-sm overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-100 bg-[#E5D3B3]/20">
            <p className="text-xs font-semibold text-[#001C98] uppercase tracking-wider">Account Details</p>
          </div>
          <div className="divide-y divide-gray-100">

            <div className="flex items-center gap-4 px-6 py-4">
              <div className="w-10 h-10 rounded-xl bg-[#001C98]/10 flex items-center justify-center flex-shrink-0">
                <UserCircle className="w-5 h-5 text-[#001C98]" />
              </div>
              <div>
                <p className="text-xs text-gray-400 font-medium">Full Name</p>
                <p className="text-sm font-semibold text-gray-800">{user.firstname} {user.lastname}</p>
              </div>
            </div>

            <div className="flex items-center gap-4 px-6 py-4">
              <div className="w-10 h-10 rounded-xl bg-[#001C98]/10 flex items-center justify-center flex-shrink-0">
                <Mail className="w-5 h-5 text-[#001C98]" />
              </div>
              <div>
                <p className="text-xs text-gray-400 font-medium">Email</p>
                <p className="text-sm font-semibold text-gray-800">{user.email}</p>
              </div>
            </div>

            {user.studentId && (
              <div className="flex items-center gap-4 px-6 py-4">
                <div className="w-10 h-10 rounded-xl bg-[#001C98]/10 flex items-center justify-center flex-shrink-0">
                  <Hash className="w-5 h-5 text-[#001C98]" />
                </div>
                <div>
                  <p className="text-xs text-gray-400 font-medium">Student ID</p>
                  <p className="text-sm font-semibold text-gray-800">{user.studentId}</p>
                </div>
              </div>
            )}

            {user.role && (
              <div className="flex items-center gap-4 px-6 py-4">
                <div className="w-10 h-10 rounded-xl bg-[#001C98]/10 flex items-center justify-center flex-shrink-0">
                  <Shield className="w-5 h-5 text-[#001C98]" />
                </div>
                <div>
                  <p className="text-xs text-gray-400 font-medium">Role</p>
                  <span className={`inline-block text-xs font-semibold px-3 py-1 rounded-full capitalize ${
                    user.role === 'student'
                      ? 'bg-[#10B981]/10 text-[#10B981]'
                      : 'bg-[#F59E0B]/10 text-[#F59E0B]'
                  }`}>
                    {user.role}
                  </span>
                </div>
              </div>
            )}

          </div>
        </div>
      </main>
    </div>
  );
}
      <div className="max-w-4xl mx-auto pt-12">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-4xl font-bold text-gray-900">Wildcats Lounge</h1>
          <Button variant="outline" onClick={handleLogout}>
            <LogOut className="mr-2 h-4 w-4" />
            Logout
          </Button>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="text-2xl">Welcome to your Dashboard!</CardTitle>
            <CardDescription>You have successfully logged in</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex items-center space-x-4 p-4 bg-muted rounded-lg">
              <UserCircle className="w-12 h-12 text-primary" />
              <div>
                <p className="text-sm font-medium text-muted-foreground">Name</p>
                <p className="text-lg font-semibold">{user.firstname} {user.lastname}</p>
              </div>
            </div>

            <div className="flex items-center space-x-4 p-4 bg-muted rounded-lg">
              <Mail className="w-12 h-12 text-primary" />
              <div>
                <p className="text-sm font-medium text-muted-foreground">Email</p>
                <p className="text-lg font-semibold">{user.email}</p>
              </div>
            </div>

            {user.studentId && (
              <div className="flex items-center space-x-4 p-4 bg-muted rounded-lg">
                <Hash className="w-12 h-12 text-primary" />
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Student ID</p>
                  <p className="text-lg font-semibold">{user.studentId}</p>
                </div>
              </div>
            )}

            {user.role && (
              <div className="flex items-center space-x-4 p-4 bg-muted rounded-lg">
                <Shield className="w-12 h-12 text-primary" />
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Role</p>
                  <p className="text-lg font-semibold capitalize">{user.role}</p>
                </div>
              </div>
            )}

            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <p className="text-green-800 font-medium">✓ Login Successful</p>
              <p className="text-green-700 text-sm mt-1">
                You are now logged in to the Wildcats Lounge platform.
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
