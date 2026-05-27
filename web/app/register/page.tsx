'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { sendRegisterCode, verifyRegister, type RegisterData } from '@/lib/api';
import { saveSession } from '@/lib/auth';
import { Loader2, Coffee } from 'lucide-react';
import { VerificationCodeInput } from '@/components/ui/verification-code-input';

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
  const [verificationCode, setVerificationCode] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [sendingCode, setSendingCode] = useState(false);
  const [codeSent, setCodeSent] = useState(false);
  const [codeCooldown, setCodeCooldown] = useState(0);

  useEffect(() => {
    if (codeCooldown <= 0) return;
    const timer = window.setInterval(() => {
      setCodeCooldown((prev) => (prev <= 1 ? 0 : prev - 1));
    }, 1000);
    return () => window.clearInterval(timer);
  }, [codeCooldown]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (codeSent) {
      setCodeSent(false);
      setVerificationCode('');
      setCodeCooldown(0);
      setSuccess('');
    }
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
  };

  const validateRegistration = (requireCode: boolean) => {
    if (!formData.firstname || !formData.lastname || !formData.email || !formData.studentId || !formData.password) {
      return 'First name, last name, email, student ID and password are required';
    }

    if (!/^\d{2}-\d{4}-\d{3}$/.test(formData.studentId.trim())) {
      return 'Student ID must follow the format ##-####-###';
    }

    if (formData.password.length < 6) {
      return 'Password must be at least 6 characters';
    }

    if (formData.password !== confirmPassword) {
      return 'Passwords do not match';
    }

    if (requireCode && !/^\d{6}$/.test(verificationCode.trim())) {
      return 'Enter the 6-digit verification code sent to your email';
    }

    return '';
  };

  const handleSendCode = async () => {
    setError('');
    setSuccess('');

    const validationError = validateRegistration(false);
    if (validationError) {
      setError(validationError);
      return;
    }

    setSendingCode(true);
    try {
      const response = await sendRegisterCode({
        ...formData,
        email: formData.email.trim(),
        studentId: formData.studentId.trim(),
      });

      if (response.success) {
        setCodeSent(true);
        setVerificationCode('');
        setCodeCooldown(60);
        setSuccess('Verification code sent. Check your email and enter the 6-digit code below.');
      } else {
        setError(response.error || 'Could not send verification code');
      }
    } catch {
      setError('An unexpected error occurred');
    } finally {
      setSendingCode(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!codeSent) {
      setError('Send a verification code first');
      return;
    }

    const validationError = validateRegistration(true);
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);

    try {
      const response = await verifyRegister({
        ...formData,
        email: formData.email.trim(),
        studentId: formData.studentId.trim(),
        verificationCode: verificationCode.trim(),
      });

      if (response.success && response.data) {
        saveSession(response.data.user, response.data.accessToken, response.data.refreshToken);
        setSuccess('Registration successful! Redirecting...');
        setTimeout(() => {
          router.push('/app');
        }, 1500);
      } else {
        setError(response.error || 'Registration failed');
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
            Join the lounge.<br />
            <span className="text-blue-200">Start your journey.</span>
          </h1>
          <p className="text-white/70 text-sm mb-10">
            Verify your email, create your student account, and access Wildcats Lounge.
          </p>
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

          <h2 className="text-2xl font-bold text-gray-900 mb-1">Create your student account</h2>
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
                Student ID
              </label>
              <input
                name="studentId" type="text" placeholder="##-####-###"
                value={formData.studentId} onChange={handleChange}
                disabled={loading} required
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
                value={confirmPassword}
                onChange={(e) => {
                  if (codeSent) {
                    setCodeSent(false);
                    setVerificationCode('');
                    setCodeCooldown(0);
                    setSuccess('');
                  }
                  setConfirmPassword(e.target.value);
                  setError('');
                }}
                disabled={loading} required
                className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition placeholder-gray-400 disabled:opacity-50"
              />
            </div>

            <div className={`rounded-2xl border p-4 space-y-4 transition-all ${
              codeSent
                ? 'border-[#001C98]/15 bg-[#001C98]/[0.03] shadow-sm'
                : 'border-[#E5D3B3]/60 bg-white/80'
            }`}>
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm font-semibold text-gray-800">Email verification</p>
                  <p className="text-xs text-gray-500 mt-1 leading-relaxed">
                    {codeSent
                      ? `A 6-digit code was sent to ${formData.email.trim()}. Enter it below to finish creating your account.`
                      : 'We\'ll send a 6-digit code to your email before creating your account.'}
                  </p>
                </div>
                <button
                  type="button"
                  onClick={handleSendCode}
                  disabled={loading || sendingCode || codeCooldown > 0}
                  className="shrink-0 min-w-[96px] px-3 py-2 text-xs font-semibold rounded-xl bg-[#001C98] text-white shadow-sm disabled:opacity-60"
                >
                  {sendingCode ? 'Sending...' : codeCooldown > 0 ? `Resend in ${codeCooldown}s` : codeSent ? 'Resend code' : 'Send code'}
                </button>
              </div>

              {codeSent && (
                <div className="rounded-2xl border border-[#2563EB]/15 bg-white p-4 sm:p-5 space-y-4">
                  <div className="text-center space-y-1">
                    <p className="text-xl font-bold text-[#2563EB]">Enter 6-digit verification code</p>
                    <p className="text-xs text-gray-500">
                      Paste works too. We&apos;ll verify your email before creating the account.
                    </p>
                  </div>

                  <VerificationCodeInput
                    value={verificationCode}
                    onChange={(nextValue) => {
                      setVerificationCode(nextValue);
                      setError('');
                    }}
                    disabled={loading}
                    autoFocus
                    className="gap-2 sm:gap-3"
                  />

                  <p className="text-center text-[11px] text-gray-400">
                    Didn&apos;t get it? Check spam or wait for the resend timer to finish.
                  </p>
                </div>
              )}
            </div>

            <button
              type="submit" disabled={loading || !codeSent || verificationCode.length !== 6}
              className="w-full flex items-center justify-center gap-2 bg-[#001C98] hover:bg-[#0025B8] disabled:opacity-60 text-white font-semibold py-3 rounded-xl transition-all shadow-md hover:shadow-lg active:scale-[0.99] mt-2"
            >
              {loading ? (
                <><Loader2 className="w-4 h-4 animate-spin" /> Verifying...</>
              ) : (
                'Verify & Create Account'
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
