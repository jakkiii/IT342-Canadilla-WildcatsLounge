'use client';

import { useEffect } from 'react';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#FDFBF7] p-6">
      <div className="max-w-md w-full bg-white rounded-2xl border border-[#E5D3B3] p-8 text-center space-y-4">
        <h2 className="text-xl font-bold text-gray-900">Something went wrong</h2>
        <p className="text-sm text-gray-500">
          The page failed to load. Try again or return to the home page.
        </p>
        <div className="flex gap-3 justify-center">
          <button
            type="button"
            onClick={() => reset()}
            className="px-4 py-2 bg-[#001C98] text-white text-sm font-semibold rounded-xl hover:bg-[#0025B8]"
          >
            Try again
          </button>
          <a
            href="/"
            className="px-4 py-2 border border-[#E5D3B3] text-sm font-semibold rounded-xl hover:bg-gray-50"
          >
            Go home
          </a>
        </div>
      </div>
    </div>
  );
}
