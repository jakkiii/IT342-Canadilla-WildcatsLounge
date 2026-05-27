'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

/** Staff uses the main /login page (credentials pre-filled there). */
export default function AdminLoginRedirect() {
  const router = useRouter();
  useEffect(() => {
    router.replace('/login');
  }, [router]);
  return null;
}
