'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { login, type LoginData } from '@/lib/api';
import { getUser, isStaff, saveSession, syncSessionCookiesFromStorage } from '@/lib/auth';
import { Loader2, Coffee } from 'lucide-react';

export default function LoginPage() {
  const router = useRouter();
  const [formData, setFormData] = useState<LoginData>({
    identifier: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const existingUser = getUser();
    if (!existingUser) return;
    syncSessionCookiesFromStorage();
    router.replace(isStaff() ? '/admin' : '/app');
  }, [router]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
  };

  const validateLogin = (requireCode: boolean) => {
    if (!formData.identifier || !formData.password) {
      return 'Email or Student ID and password are required';
    }

    return '';
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    const validationError = validateLogin(false);
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);

    try {
      const response = await login(formData);

      if (response.success && response.data) {
        saveSession(response.data.user, response.data.accessToken, response.data.refreshToken);
        if (response.data.user.role === 'staff') {
          router.push('/admin');
        } else {
          router.push('/app');
        }
      } else {
        setError(response.error || 'Login failed');
      }
    } catch (err) {
      setError('An unexpected error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col lg:flex-row font-poppins">

      {/* ── LEFT PANEL ── */}
      <div className="hidden lg:flex lg:w-1/2 relative overflow-hidden flex-col items-center justify-center p-14 gradient-animated">
        <div className="absolute -top-24 -left-24 w-80 h-80 rounded-full bg-white/10 blur-3xl pointer-events-none" />
        <div className="absolute -bottom-24 -right-24 w-96 h-96 rounded-full bg-white/5 blur-3xl pointer-events-none" />
        <div className="absolute top-1/3 right-1/4 w-48 h-48 rounded-full bg-blue-300/10 blur-2xl pointer-events-none" />
        <div className="relative z-10 text-white max-w-md w-full">
          <div className="flex items-center gap-3 mb-10">
            <div className="w-12 h-12 rounded-2xl bg-white/20 backdrop-blur-sm flex items-center justify-center shadow-lg">
              <Coffee className="w-6 h-6 text-white" />
            </div>
            <span className="text-xl font-bold tracking-wide">Wildcats Lounge</span>
          </div>
          <h1 className="text-4xl font-extrabold leading-tight mb-4">
            Welcome back.<br />
            <span className="text-blue-200">Good to see you.</span>
          </h1>
          <p className="text-white/70 text-sm mb-10">
            Sign in with your email or Student ID to continue.
          </p>
        </div>
      </div>

      {/* ── RIGHT PANEL ── */}
      <div className="w-full lg:w-1/2 flex items-center justify-center bg-[#FDFBF7] px-6 py-10 lg:px-16">
        <div className="w-full max-w-md">
          {/* Mobile logo */}
          <div className="flex lg:hidden items-center gap-2 mb-8">
            <div className="w-9 h-9 rounded-xl bg-[#001C98] flex items-center justify-center">
              <Coffee className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-bold text-[#001C98]">Wildcats Lounge</span>
          </div>

          <h2 className="text-2xl font-bold text-gray-900 mb-1">Sign in</h2>
          <p className="text-gray-500 text-sm mb-8">
            Don&apos;t have an account?{' '}
            <Link href="/register" className="text-[#001C98] font-semibold hover:underline">Create one</Link>
          </p>

          {error && (
            <div className="bg-[#EF4444]/10 text-[#EF4444] text-sm p-3 rounded-xl border border-[#EF4444]/20 mb-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">Email or Student ID</label>
              <input
                name="identifier" type="text" placeholder="juan@cit.edu or 22-1234-567"
                value={formData.identifier} onChange={handleChange}
                disabled={loading} required
                className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">Password</label>
              <input
                name="password" type="password" placeholder="••••••••"
                value={formData.password} onChange={handleChange}
                disabled={loading} required
                className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
              />
            </div>

            <button
              type="submit" disabled={loading}
              className="w-full flex items-center justify-center gap-2 bg-[#001C98] hover:bg-[#0025B8] disabled:opacity-60 text-white font-semibold py-3 rounded-xl transition-all shadow-md hover:shadow-lg active:scale-[0.99] mt-2"
            >
              {loading ? (
                <><Loader2 className="w-4 h-4 animate-spin" /> Signing in...</>
              ) : (
                'Sign In'
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
