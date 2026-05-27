'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import {
  staffGetMenu,
  staffToggleMenu,
  staffGetIngredients,
  staffRestockIngredient,
  type MenuItem,
  type Ingredient,
} from '@/lib/api';
import { Loader2, RefreshCw, Package, UtensilsCrossed, X, AlertTriangle } from 'lucide-react';

type Tab = 'menu' | 'stock';

type BlockedModal = {
  itemName: string;
  ingredients: string[];
};

function normalizeMenu(items: MenuItem[]): MenuItem[] {
  return items.map((item) => ({
    ...item,
    category: item.categoryLabel || String(item.category).toLowerCase().replace(/_/g, ' '),
    price: Number(item.price),
  }));
}

function isItemAvailable(item: MenuItem) {
  const stockOk = item.inventorySufficient !== false;
  return Boolean(item.isAvailable && stockOk);
}

function TabSkeleton() {
  return (
    <div className="space-y-3 animate-pulse">
      <div className="h-10 bg-[#E5D3B3]/40 rounded-xl" />
      <div className="h-48 bg-[#E5D3B3]/30 rounded-2xl" />
      <div className="h-48 bg-[#E5D3B3]/30 rounded-2xl" />
    </div>
  );
}

function InventoryBlockedModal({
  modal,
  onClose,
}: {
  modal: BlockedModal;
  onClose: () => void;
}) {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return createPortal(
    <div
      className="fixed inset-0 z-[10000] flex items-center justify-center bg-black/50 p-4"
      onClick={(e) => e.target === e.currentTarget && onClose()}
      role="dialog"
      aria-modal="true"
      aria-labelledby="inventory-blocked-title"
    >
      <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 animate-fade-in">
        <div className="flex items-start justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="w-11 h-11 rounded-xl bg-[#EF4444]/10 flex items-center justify-center shrink-0">
              <AlertTriangle className="w-6 h-6 text-[#EF4444]" />
            </div>
            <h2 id="inventory-blocked-title" className="text-lg font-bold text-gray-900">
              Cannot mark as available
            </h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-500"
            aria-label="Close"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <p className="text-sm text-gray-600 mt-4 leading-relaxed">
          <span className="font-semibold text-gray-900">{modal.itemName}</span> cannot be marked
          available because required ingredients are missing or out of stock. Restock them in the{' '}
          <span className="font-semibold">Stock</span> tab, then try again.
        </p>

        {modal.ingredients.length > 0 && (
          <ul className="mt-3 space-y-1.5 text-sm text-gray-700 bg-[#FDFBF7] border border-[#E5D3B3]/60 rounded-xl p-3 max-h-40 overflow-y-auto">
            {modal.ingredients.map((name) => (
              <li key={name} className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-[#EF4444] shrink-0" />
                {name}
              </li>
            ))}
          </ul>
        )}

        <button
          type="button"
          onClick={onClose}
          className="mt-6 w-full py-3 bg-[#001C98] hover:bg-[#0025B8] text-white font-semibold rounded-xl transition"
        >
          Got it
        </button>
      </div>
    </div>,
    document.body
  );
}

export default function AdminMenuPage() {
  const [tab, setTab] = useState<Tab>('menu');
  const [menu, setMenu] = useState<MenuItem[]>([]);
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [menuLoading, setMenuLoading] = useState(true);
  const [stockLoading, setStockLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [togglingId, setTogglingId] = useState<number | null>(null);
  const [error, setError] = useState('');
  const [blockedModal, setBlockedModal] = useState<BlockedModal | null>(null);
  const [restockAmounts, setRestockAmounts] = useState<Record<number, string>>({});
  const [restocking, setRestocking] = useState<number | null>(null);
  const menuLoaded = useRef(false);
  const stockLoaded = useRef(false);

  const loadMenu = useCallback(async (silent = false) => {
    if (!silent) setMenuLoading(true);
    const menuRes = await staffGetMenu();
    if (menuRes.success && menuRes.data) {
      setMenu(normalizeMenu(menuRes.data));
      menuLoaded.current = true;
    } else if (!menuLoaded.current) {
      setError(menuRes.error || 'Failed to load menu');
    }
    setMenuLoading(false);
  }, []);

  const loadStock = useCallback(async (silent = false) => {
    if (!silent) setStockLoading(true);
    const ingRes = await staffGetIngredients();
    if (ingRes.success && ingRes.data) {
      setIngredients(ingRes.data);
      stockLoaded.current = true;
    } else if (!stockLoaded.current) {
      setError(ingRes.error || 'Failed to load inventory');
    }
    setStockLoading(false);
  }, []);

  useEffect(() => {
    setError('');
    Promise.all([loadMenu(), loadStock()]);
  }, [loadMenu, loadStock]);

  const refresh = async () => {
    setRefreshing(true);
    setError('');
    if (tab === 'menu') {
      await loadMenu(true);
    } else {
      await loadStock(true);
    }
    setRefreshing(false);
  };

  const showInventoryBlocked = (item: MenuItem, apiMessage?: string) => {
    const fromApi =
      apiMessage?.match(/ingredients:\s*(.+)$/i)?.[1]?.split(',').map((s) => s.trim()) ?? [];
    setBlockedModal({
      itemName: item.name,
      ingredients: fromApi.length > 0 ? fromApi : ['One or more required ingredients are out of stock'],
    });
  };

  const markAvailable = async (item: MenuItem) => {
    if (item.inventorySufficient === false) {
      showInventoryBlocked(item);
      return;
    }

    setTogglingId(item.id);
    setError('');
    const res = await staffToggleMenu(item.id, true);
    if (res.success) {
      await loadMenu(true);
    } else {
      const msg = res.error || '';
      if (msg.toLowerCase().includes('cannot mark') || msg.toLowerCase().includes('ingredient')) {
        showInventoryBlocked(item, msg);
      } else {
        setError(msg || 'Could not update availability');
      }
    }
    setTogglingId(null);
  };

  const markUnavailable = async (item: MenuItem) => {
    setTogglingId(item.id);
    setError('');
    const res = await staffToggleMenu(item.id, false);
    if (res.success) {
      await loadMenu(true);
    } else {
      setError(res.error || 'Could not update availability');
    }
    setTogglingId(null);
  };

  const restock = async (id: number) => {
    const amount = parseFloat(restockAmounts[id] || '0');
    if (!amount || amount <= 0) {
      setError('Enter a valid restock amount');
      return;
    }
    setRestocking(id);
    setError('');
    const res = await staffRestockIngredient(id, amount);
    if (res.success) {
      setRestockAmounts((prev) => ({ ...prev, [id]: '' }));
      await loadStock(true);
      await loadMenu(true);
    } else {
      setError(res.error || 'Restock failed');
    }
    setRestocking(null);
  };

  const availableItems = menu.filter(isItemAvailable);
  const unavailableItems = menu.filter((item) => !isItemAvailable(item));
  const tabLoading = tab === 'menu' ? menuLoading : stockLoading;

  const renderMenuTable = (items: MenuItem[], emptyMessage: string) => (
    <div className="bg-white rounded-2xl border border-[#E5D3B3] overflow-hidden">
      <table className="w-full text-sm">
        <thead className="bg-[#E5D3B3]/30">
          <tr>
            <th className="text-left px-4 py-3">Item</th>
            <th className="text-left px-4 py-3">Category</th>
            <th className="text-left px-4 py-3">Price</th>
            <th className="text-left px-4 py-3">Status</th>
            <th className="text-right px-4 py-3">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {items.map((item) => {
            const staffOff = !item.isAvailable;
            const outOfStock = item.inventorySufficient === false;
            const busy = togglingId === item.id;

            return (
              <tr key={item.id} className="hover:bg-gray-50/50">
                <td className="px-4 py-3 font-medium">{item.name}</td>
                <td className="px-4 py-3 capitalize">{item.category}</td>
                <td className="px-4 py-3">₱{Number(item.price).toFixed(2)}</td>
                <td className="px-4 py-3">
                  <span
                    className={`text-xs font-bold px-2 py-1 rounded-full ${
                      isItemAvailable(item)
                        ? 'bg-[#10B981]/15 text-[#10B981]'
                        : 'bg-[#EF4444]/15 text-[#EF4444]'
                    }`}
                  >
                    {isItemAvailable(item) ? 'Available' : 'Unavailable'}
                  </span>
                  {staffOff && !outOfStock && (
                    <p className="text-[10px] text-gray-400 mt-1">Turned off by staff</p>
                  )}
                  {outOfStock && (
                    <p className="text-[10px] text-gray-400 mt-1">Out of ingredients</p>
                  )}
                </td>
                <td className="px-4 py-3 text-right">
                  {staffOff ? (
                    <button
                      type="button"
                      disabled={busy}
                      onClick={() => markAvailable(item)}
                      className="text-xs font-semibold text-[#001C98] hover:underline disabled:opacity-50"
                    >
                      {busy ? 'Updating…' : 'Mark available'}
                    </button>
                  ) : isItemAvailable(item) ? (
                    <button
                      type="button"
                      disabled={busy}
                      onClick={() => markUnavailable(item)}
                      className="text-xs font-semibold text-[#001C98] hover:underline disabled:opacity-50"
                    >
                      {busy ? 'Updating…' : 'Mark unavailable'}
                    </button>
                  ) : (
                    <span className="text-[10px] text-gray-400">Restock ingredients first</span>
                  )}
                </td>
              </tr>
            );
          })}
          {items.length === 0 && (
            <tr>
              <td colSpan={5} className="text-center py-8 text-gray-500">
                {emptyMessage}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );

  return (
    <div className="space-y-6">
      {blockedModal && (
        <InventoryBlockedModal modal={blockedModal} onClose={() => setBlockedModal(null)} />
      )}

      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Menu & Stock</h1>
          <p className="text-sm text-gray-500 mt-1">
            {tab === 'menu'
              ? 'View and toggle menu item availability for students.'
              : 'Manage ingredient inventory. Restocking updates what can be served.'}
          </p>
        </div>
        <button
          onClick={refresh}
          disabled={refreshing || tabLoading}
          className="p-2 rounded-lg hover:bg-gray-100 self-start disabled:opacity-50"
          aria-label="Refresh"
        >
          <RefreshCw className={`w-4 h-4 ${refreshing ? 'animate-spin' : ''}`} />
        </button>
      </div>

      <div className="flex gap-1 p-1 bg-[#E5D3B3]/30 rounded-xl w-fit">
        <button
          type="button"
          onClick={() => setTab('menu')}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-semibold transition ${
            tab === 'menu' ? 'bg-white text-[#001C98] shadow-sm' : 'text-gray-600 hover:text-gray-900'
          }`}
        >
          <UtensilsCrossed className="w-4 h-4" />
          Menu
          {menuLoading && tab !== 'menu' && (
            <Loader2 className="w-3 h-3 animate-spin text-gray-400" />
          )}
        </button>
        <button
          type="button"
          onClick={() => setTab('stock')}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-semibold transition ${
            tab === 'stock' ? 'bg-white text-[#001C98] shadow-sm' : 'text-gray-600 hover:text-gray-900'
          }`}
        >
          <Package className="w-4 h-4" />
          Stock
          {stockLoading && tab !== 'stock' && (
            <Loader2 className="w-3 h-3 animate-spin text-gray-400" />
          )}
        </button>
      </div>

      {error && (
        <div className="text-sm p-3 rounded-xl bg-[#EF4444]/10 text-[#EF4444] border border-[#EF4444]/20">
          {error}
        </div>
      )}

      {tab === 'menu' && (
        <>
          {menuLoading ? (
            <TabSkeleton />
          ) : (
            <div className="space-y-8">
              <section>
                <h2 className="text-lg font-bold text-gray-900 mb-1 flex items-center gap-2">
                  <span className="w-2 h-2 rounded-full bg-[#10B981]" />
                  Available ({availableItems.length})
                </h2>
                <p className="text-xs text-gray-500 mb-3">Items students can order right now.</p>
                {renderMenuTable(availableItems, 'No available items.')}
              </section>

              <section>
                <h2 className="text-lg font-bold text-gray-900 mb-1 flex items-center gap-2">
                  <span className="w-2 h-2 rounded-full bg-[#EF4444]" />
                  Unavailable ({unavailableItems.length})
                </h2>
                <p className="text-xs text-gray-500 mb-3">
                  Turned off by staff or blocked when ingredients run out.
                </p>
                {renderMenuTable(unavailableItems, 'No unavailable items.')}
              </section>

              {menu.length === 0 && (
                <p className="text-center text-gray-500 py-6">
                  No menu items found. Restart the backend to seed the menu.
                </p>
              )}
            </div>
          )}
        </>
      )}

      {tab === 'stock' && (
        <>
          {stockLoading ? (
            <TabSkeleton />
          ) : (
            <div>
              <div className="flex items-center gap-2 mb-4">
                <Package className="w-5 h-5 text-[#001C98]" />
                <h2 className="text-lg font-bold text-gray-900">Ingredient Inventory</h2>
              </div>
              <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
                {ingredients.map((ing) => (
                  <div
                    key={ing.id}
                    className={`bg-white rounded-xl border p-4 ${
                      ing.outOfStock
                        ? 'border-[#EF4444]/40'
                        : ing.lowStock
                          ? 'border-[#F59E0B]/40'
                          : 'border-[#E5D3B3]'
                    }`}
                  >
                    <div className="flex justify-between items-start mb-2">
                      <p className="font-semibold text-sm">{ing.name}</p>
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                          ing.outOfStock
                            ? 'bg-[#EF4444]/15 text-[#EF4444]'
                            : ing.lowStock
                              ? 'bg-[#F59E0B]/15 text-[#F59E0B]'
                              : 'bg-[#10B981]/15 text-[#10B981]'
                        }`}
                      >
                        {ing.outOfStock ? 'OUT' : ing.lowStock ? 'LOW' : 'OK'}
                      </span>
                    </div>
                    <p className="text-2xl font-bold text-[#001C98] mb-1">
                      {Number(ing.quantityOnHand).toLocaleString(undefined, {
                        maximumFractionDigits: 0,
                      })}
                      <span className="text-xs font-normal text-gray-500 ml-1">{ing.unit}</span>
                    </p>
                    <p className="text-xs text-gray-400 mb-3">
                      Low at {ing.lowStockThreshold} {ing.unit}
                    </p>
                    <div className="flex gap-2">
                      <input
                        type="number"
                        min="1"
                        placeholder="Add qty"
                        value={restockAmounts[ing.id] || ''}
                        onChange={(e) =>
                          setRestockAmounts((prev) => ({ ...prev, [ing.id]: e.target.value }))
                        }
                        className="flex-1 px-2 py-1.5 text-xs border rounded-lg"
                      />
                      <button
                        onClick={() => restock(ing.id)}
                        disabled={restocking === ing.id}
                        className="px-3 py-1.5 bg-[#001C98] text-white text-xs font-semibold rounded-lg disabled:opacity-60"
                      >
                        {restocking === ing.id ? '...' : 'Add'}
                      </button>
                    </div>
                  </div>
                ))}
                {ingredients.length === 0 && (
                  <p className="col-span-full text-center text-gray-500 py-10">
                    No ingredients found. Restart the backend to seed inventory.
                  </p>
                )}
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
