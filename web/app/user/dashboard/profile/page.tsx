'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { getMenuItems, type MenuItemData, type UserData } from '@/lib/api';
import { CheckCircle2, Heart } from 'lucide-react';

type StoredProfile = {
  description: string;
  favoriteMenuItemIds: number[];
};

const getStoredUser = (): UserData | null => {
  if (typeof window === 'undefined') return null;
  const stored = localStorage.getItem('user');
  if (!stored) return null;
  try {
    return JSON.parse(stored) as UserData;
  } catch {
    return null;
  }
};

const getStoredProfile = (userId: number): StoredProfile => {
  const stored = localStorage.getItem(`userProfile:${userId}`);
  if (!stored) {
    return { description: '', favoriteMenuItemIds: [] };
  }
  try {
    const parsed = JSON.parse(stored) as StoredProfile;
    return {
      description: parsed.description || '',
      favoriteMenuItemIds: Array.isArray(parsed.favoriteMenuItemIds) ? parsed.favoriteMenuItemIds : [],
    };
  } catch {
    return { description: '', favoriteMenuItemIds: [] };
  }
};

export default function ProfilePage() {
  const router = useRouter();
  const [user, setUser] = useState<UserData | null>(null);
  const [firstname, setFirstname] = useState('');
  const [lastname, setLastname] = useState('');
  const [description, setDescription] = useState('');
  const [favoriteMenuItemIds, setFavoriteMenuItemIds] = useState<number[]>([]);
  const [menuItems, setMenuItems] = useState<MenuItemData[]>([]);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const maxDescriptionLength = 255;

  useEffect(() => {
    const storedUser = getStoredUser();
    if (!storedUser?.id) {
      router.push('/user/login');
      return;
    }

    setUser(storedUser);
    setFirstname(storedUser.firstname || '');
    setLastname(storedUser.lastname || '');

    const storedProfile = getStoredProfile(storedUser.id);
    setDescription(storedProfile.description);
    setFavoriteMenuItemIds(storedProfile.favoriteMenuItemIds);

    const loadMenu = async () => {
      const menuRes = await getMenuItems();
      if (menuRes.success && menuRes.data) {
        setMenuItems(menuRes.data);
      } else {
        setError(menuRes.error || 'Failed to load menu items.');
      }
    };

    void loadMenu();
  }, [router]);

  const toggleFavorite = (menuItemId: number) => {
    setFavoriteMenuItemIds((previous) =>
      previous.includes(menuItemId)
        ? previous.filter((id) => id !== menuItemId)
        : [...previous, menuItemId]
    );
  };

  const handleSave = () => {
    if (!user) return;

    setSaving(true);
    setError('');
    setSuccess('');

    const storedProfile: StoredProfile = {
      description: description.trim(),
      favoriteMenuItemIds,
    };

    localStorage.setItem(`userProfile:${user.id}`, JSON.stringify(storedProfile));

    setSaving(false);
    setSuccess('Profile updated successfully.');
  };

  return (
    <div className="space-y-6">
      {error && (
        <div className="rounded-xl border border-[#EF4444]/25 bg-[#EF4444]/10 text-[#EF4444] px-4 py-3 text-sm font-medium">
          {error}
        </div>
      )}

      {success && (
        <div className="rounded-xl border border-[#10B981]/25 bg-[#10B981]/10 text-[#0E8A62] px-4 py-3 text-sm font-medium flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4" />
          {success}
        </div>
      )}

      <div className="bg-white border border-[#E5D3B3] rounded-2xl p-6 shadow-sm">
        <h2 className="text-lg font-bold text-[#10203B] mb-4">Profile Details</h2>
        <div className="grid gap-4 md:grid-cols-2">
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1.5">First Name</label>
            <input
              value={firstname}
              readOnly
              className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-gray-50 text-gray-500"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1.5">Last Name</label>
            <input
              value={lastname}
              readOnly
              className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-gray-50 text-gray-500"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1.5">Email</label>
            <input
              value={user?.email || ''}
              readOnly
              className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-gray-50 text-gray-500"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1.5">ID Code</label>
            <input
              value={user?.studentId || 'Not provided'}
              readOnly
              className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-gray-50 text-gray-500"
            />
          </div>
        </div>

        <div className="mt-4">
          <label className="block text-xs font-semibold text-gray-700 mb-1.5">Description</label>
          <textarea
            value={description}
            onChange={(event) => setDescription(event.target.value.slice(0, maxDescriptionLength))}
            maxLength={maxDescriptionLength}
            rows={3}
            placeholder="Tell us what you love about Wildcats Lounge"
            className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#001C98]/30 focus:border-[#001C98] transition"
          />
          <div className="mt-2 text-xs text-gray-500 text-right">
            {description.length}/{maxDescriptionLength}
          </div>
        </div>
      </div>

      <div className="bg-white border border-[#E5D3B3] rounded-2xl p-6 shadow-sm">
        <div className="flex items-center gap-2 mb-4">
          <Heart className="w-5 h-5 text-[#001C98]" />
          <h2 className="text-lg font-bold text-[#10203B]">Favorite Menu Items</h2>
        </div>
        <p className="text-sm text-gray-500 mb-4">
          Select your favorites from the menu so they are easy to find later.
        </p>

        {menuItems.length === 0 ? (
          <p className="text-sm text-gray-500">Menu items are loading.</p>
        ) : (
          <div className="flex flex-wrap gap-2">
            {menuItems.map((item) => {
              const isSelected = favoriteMenuItemIds.includes(item.id);
              return (
                <button
                  key={item.id}
                  onClick={() => toggleFavorite(item.id)}
                  className={`px-3 py-1.5 rounded-full text-sm font-semibold border transition ${
                    isSelected
                      ? 'bg-[#001C98] text-white border-[#001C98]'
                      : 'bg-white text-[#001C98] border-[#C9D2FF] hover:bg-[#EEF1FF]'
                  }`}
                >
                  {item.name}
                </button>
              );
            })}
          </div>
        )}

        <div className="mt-5">
          <button
            onClick={handleSave}
            disabled={saving}
            className="inline-flex items-center justify-center rounded-xl bg-[#001C98] text-white px-5 py-2.5 text-sm font-semibold hover:bg-[#0025B8] disabled:opacity-60 transition"
          >
            {saving ? 'Saving...' : 'Save Profile'}
          </button>
        </div>
      </div>
    </div>
  );
}
