import { useEffect, useState } from 'react';
import type { UserData } from './api';

const SESSION_COOKIE_MAX_AGE = 60 * 60 * 24 * 7;

export function getAccessToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('accessToken');
}

export function getUser(): UserData | null {
  if (typeof window === 'undefined') return null;
  const raw = localStorage.getItem('user');
  if (!raw) return null;
  try {
    return JSON.parse(raw) as UserData;
  } catch {
    return null;
  }
}

export function isStaff(): boolean {
  return getUser()?.role?.toLowerCase() === 'staff';
}

function setCookie(name: string, value: string, maxAge = SESSION_COOKIE_MAX_AGE) {
  if (typeof document === 'undefined') return;
  document.cookie = `${name}=${encodeURIComponent(value)}; path=/; max-age=${maxAge}; samesite=lax`;
}

function clearCookie(name: string) {
  if (typeof document === 'undefined') return;
  document.cookie = `${name}=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; samesite=lax`;
}

export function saveSession(user: UserData, accessToken: string, refreshToken: string) {
  localStorage.setItem('user', JSON.stringify(user));
  localStorage.setItem('accessToken', accessToken);
  localStorage.setItem('refreshToken', refreshToken);
  setCookie('wl_session', '1');
  setCookie('wl_role', user.role || 'student');
}

export function syncSessionCookiesFromStorage() {
  if (typeof window === 'undefined') return;
  const user = getUser();
  const token = getAccessToken();
  if (!user || !token) return;
  setCookie('wl_session', '1');
  setCookie('wl_role', user.role || 'student');
}

export function clearSession() {
  localStorage.removeItem('user');
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  clearCookie('wl_session');
  clearCookie('wl_role');
}

/** Read localStorage user after mount to avoid SSR/client hydration mismatches. */
export function useClientUser() {
  const [user, setUser] = useState<UserData | null>(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    setUser(getUser());
    setReady(true);
  }, []);

  return { user, ready };
}
