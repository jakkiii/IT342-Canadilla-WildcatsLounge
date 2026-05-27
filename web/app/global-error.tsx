'use client';

export default function GlobalError({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <html lang="en">
      <body className="min-h-screen flex items-center justify-center bg-[#FDFBF7] p-6 font-sans">
        <div className="max-w-md w-full bg-white rounded-2xl border border-[#E5D3B3] p-8 text-center space-y-4">
          <h2 className="text-xl font-bold text-gray-900">Something went wrong</h2>
          <p className="text-sm text-gray-500">The app hit an unexpected error.</p>
          <button
            type="button"
            onClick={() => reset()}
            className="px-4 py-2 bg-[#001C98] text-white text-sm font-semibold rounded-xl hover:bg-[#0025B8]"
          >
            Try again
          </button>
        </div>
      </body>
    </html>
  );
}
