import Link from 'next/link';
import { Coffee, UserPlus, LogIn } from 'lucide-react';

export default function HomePage() {
  return (
    <div className="min-h-screen flex flex-col lg:flex-row font-poppins">

      {/* ── LEFT PANEL — animated gradient ── */}
      <div className="hidden lg:flex lg:w-1/2 relative overflow-hidden flex-col items-center justify-center p-14 gradient-animated">
        {/* Decorative blobs */}
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

          <h1 className="text-5xl font-extrabold leading-tight mb-4">
            Your campus<br />
            hangout,<br />
            <span className="text-blue-200">reimagined.</span>
          </h1>
          <p className="text-white/70 text-sm">
            A secure, modern platform for CIT-U Technologians and Staff.
          </p>
        </div>
      </div>

      {/* ── RIGHT PANEL ── */}
      <div className="w-full lg:w-1/2 flex items-center justify-center bg-[#FDFBF7] px-8 py-16 lg:px-16">
        <div className="w-full max-w-sm">
          {/* Mobile logo */}
          <div className="flex lg:hidden items-center gap-2 mb-10">
            <div className="w-9 h-9 rounded-xl bg-[#001C98] flex items-center justify-center">
              <Coffee className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-bold text-[#001C98]">Wildcats Lounge</span>
          </div>

          <h2 className="text-3xl font-bold text-[#001C98] mb-2">Get Started</h2>
          <p className="text-gray-500 text-sm mb-10">
            Join Wildcats Lounge or sign in to your account.
          </p>

          <div className="space-y-4">
            <Link href="/register" className="block">
              <button className="w-full flex items-center justify-center gap-2 bg-[#001C98] hover:bg-[#0025B8] text-white font-semibold py-3.5 px-6 rounded-xl transition-all shadow-md hover:shadow-lg active:scale-[0.99]">
                <UserPlus className="w-5 h-5" />
                Create an Account
              </button>
            </Link>
            <Link href="/login" className="block">
              <button className="w-full flex items-center justify-center gap-2 bg-white hover:bg-[#E5D3B3]/40 text-[#001C98] font-semibold py-3.5 px-6 rounded-xl border-2 border-[#001C98]/20 hover:border-[#001C98]/50 transition-all">
                <LogIn className="w-5 h-5" />
                Sign In
              </button>
            </Link>
          </div>


        </div>
      </div>
    </div>
  );
}
