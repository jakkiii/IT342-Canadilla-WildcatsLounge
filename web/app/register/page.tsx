'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { register, type RegisterData } from '@/lib/api';
import { Loader2, Coffee, Check } from 'lucide-react';

export default function RegisterPage() {
  const router = useRouter();
  const [formData, setFormData] = useState<RegisterData>({
    firstname: '',
    lastname: '',
    email: '',
    password: '',
    studentId: '',
  });
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validation
    if (!formData.firstname || !formData.lastname || !formData.email || !formData.password) {
      setError('First name, last name, email and password are required');
      return;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    if (formData.password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);

    try {
      const response = await register(formData);

      if (response.success) {
        setSuccess('Registration successful! Redirecting to login...');
        setTimeout(() => {
          router.push('/login');
        }, 2000);
      } else {
        setError(response.error || 'Registration failed');
      }
    } catch {
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
            Join the lounge.<br />
            <span className="text-blue-200">Start your journey.</span>
          </h1>
          <p className="text-white/70 text-sm mb-10">
            Create your account and access Wildcats Lounge today.
          </p>
          <div className="space-y-3">
            {['Secure email validation', 'Password encrypted with BCrypt', 'JWT-secured sessions', 'Role-based access (Student / Staff)', 'Spring Boot REST API backend'].map((f) => (
              <div key={f} className="flex items-start gap-3">
                <div className="mt-0.5 w-5 h-5 rounded-full bg-[#10B981]/30 flex items-center justify-center flex-shrink-0 border border-[#10B981]/40">
                  <Check className="w-3 h-3 text-[#10B981]" />
                </div>
                <span className="text-white/85 text-sm">{f}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* ── RIGHT PANEL ── */}
      <div className="w-full lg:w-1/2 flex items-center justify-center bg-[#FDFBF7] px-6 py-10 lg:px-16 overflow-y-auto">
        <div className="w-full max-w-md">
          {/* Mobile logo */}
          <div className="flex lg:hidden items-center gap-2 mb-8">
            <div className="w-9 h-9 rounded-xl bg-[#001C98] flex items-center justify-center">
              <Coffee className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-bold text-[#001C98]">Wildcats Lounge</span>
          </div>

          <h2 className="text-2xl font-bold text-gray-900 mb-1">Create your account</h2>
          <p className="text-gray-500 text-sm mb-6">
            Already have an account?{' '}
            <Link href="/login" className="text-[#001C98] font-semibold hover:underline">Sign in</Link>
          </p>

          {error && (
            <div className="bg-[#EF4444]/10 text-[#EF4444] text-sm p-3 rounded-xl border border-[#EF4444]/20 mb-4">
              {error}
            </div>
          )}
          {success && (
            <div className="bg-[#10B981]/10 text-[#10B981] text-sm p-3 rounded-xl border border-[#10B981]/20 mb-4">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1.5">First Name</label>
                <input
                  name="firstname" type="text" placeholder="Juan"
                  value={formData.firstname} onChange={handleChange}
                  disabled={loading} required
                  className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1.5">Last Name</label>
                <input
                  name="lastname" type="text" placeholder="Dela Cruz"
                  value={formData.lastname} onChange={handleChange}
                  disabled={loading} required
                  className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">Email</label>
              <input
                name="email" type="email" placeholder="juan@cit.edu"
                value={formData.email} onChange={handleChange}
                disabled={loading} required
                className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">
                Student ID <span className="text-gray-400 font-normal">(optional — leave blank if staff)</span>
              </label>
              <input
                name="studentId" type="text" placeholder="##-####-###"
                value={formData.studentId} onChange={handleChange}
                disabled={loading}
                className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">Password</label>
              <input
                name="password" type="password" placeholder="At least 6 characters"
                value={formData.password} onChange={handleChange}
                disabled={loading} required
                className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">Confirm Password</label>
              <input
                name="confirmPassword" type="password" placeholder="••••••••"
                value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)}
                disabled={loading} required
                className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
              />
            </div>

            <button
              type="submit" disabled={loading}
              className="w-full flex items-center justify-center gap-2 bg-[#001C98] hover:bg-[#0025B8] disabled:opacity-60 text-white font-semibold py-3 rounded-xl transition-all shadow-md hover:shadow-lg active:scale-[0.99] mt-2"
            >
              {loading ? (
                <><Loader2 className="w-4 h-4 animate-spin" /> Creating account...</>
              ) : (
                'Create Account'
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
