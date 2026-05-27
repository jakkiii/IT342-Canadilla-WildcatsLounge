'use client';

import { useEffect } from 'react';
import { syncSessionCookiesFromStorage } from '@/lib/auth';

export default function SessionCookieSync() {
  useEffect(() => {
    syncSessionCookiesFromStorage();
  }, []);

  return null;
}
