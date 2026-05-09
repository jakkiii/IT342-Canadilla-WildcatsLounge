import Link from 'next/link';
import { Coffee } from 'lucide-react';

export default function AdminPage() {
  return (
    <div className="min-h-screen bg-[#FDFBF7] font-poppins flex items-center justify-center px-6">
      <div className="w-full max-w-xl bg-white border border-[#E5D3B3] rounded-2xl p-8 shadow-sm">
        <div className="flex items-center gap-3 mb-6">
          <div className="w-10 h-10 rounded-xl bg-[#001C98]/10 border border-[#001C98]/20 flex items-center justify-center">
            <Coffee className="w-5 h-5 text-[#001C98]" />
          </div>
          <div>
            <p className="text-xs text-[#001C98]/70">Wildcats Lounge</p>
            <h1 className="text-2xl font-bold text-[#10203B]">Staff Portal</h1>
          </div>
        </div>

        <p className="text-sm text-gray-600">
          The staff experience will be implemented here next. This space is ready for the admin tools,
          event management, and order operations.
        </p>

        <div className="mt-6 flex flex-wrap items-center gap-3">
          <Link
            href="/user"
            className="inline-flex items-center justify-center rounded-xl bg-[#001C98] text-white px-4 py-2 text-sm font-semibold hover:bg-[#0025B8] transition"
          >
            Go to User Portal
          </Link>
          <span className="text-xs text-gray-500">Coming soon</span>
        </div>
      </div>
    </div>
  );
}
