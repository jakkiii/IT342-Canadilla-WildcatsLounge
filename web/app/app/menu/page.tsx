'use client';

import { useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import { getMenu, addToCart, type MenuItem } from '@/lib/api';
import {
  filterMenuByCategory,
  countMenuByTab,
  getCategoryLabel,
  getItemCategoryLabel,
  categoryTabKey,
  MENU_CATEGORY_TABS,
  normalizeMenuItems,
  isAddonItem,
  getAllowedServingTypes,
  supportsAddons,
  supportsSugarLevel,
  SUGAR_LEVELS,
  type MenuCategoryKey,
  type ServingType,
} from '@/lib/menu';
import {
  Loader2,
  Plus,
  Minus,
  Coffee,
  CupSoda,
  Cookie,
  LayoutGrid,
  X,
  Check,
  ShoppingCart,
} from 'lucide-react';
import { PageHeader, LoadingState, StudentCard } from '@/components/student/StudentUI';
import { cn } from '@/lib/utils';
import type { LucideIcon } from 'lucide-react';

const categoryIcons: Record<string, LucideIcon> = {
  all: LayoutGrid,
  coffee: Coffee,
  'non-coffee': CupSoda,
  treat: Cookie,
};

const categoryAccent: Record<string, string> = {
  coffee: 'border-l-amber-500',
  'non-coffee': 'border-l-sky-500',
  treat: 'border-l-[#10B981]',
};

const tabAccent: Record<string, string> = {
  all: 'from-[#001C98]/10 to-[#E5D3B3]/20 border-[#001C98]/20',
  coffee: 'from-amber-600/10 to-amber-800/5 border-amber-200/60',
  'non-coffee': 'from-sky-500/10 to-sky-700/5 border-sky-200/60',
  treat: 'from-[#10B981]/10 to-[#10B981]/5 border-[#10B981]/20',
};

interface ModalProps {
  item: MenuItem;
  addons: MenuItem[];
  onCancel: () => void;
  onDone: (
    item: MenuItem,
    payload: {
      addonIds: number[];
      note: string;
      qty: number;
      servingType?: ServingType;
      sugarLevelPercent?: number;
    }
  ) => Promise<void>;
  adding: boolean;
  errorMsg: string;
}

function AddToCartModal({ item, addons, onCancel, onDone, adding, errorMsg }: ModalProps) {
  const servingOptions = getAllowedServingTypes(item);
  const itemSupportsAddons = supportsAddons(item);
  const itemSupportsSugarLevel = supportsSugarLevel(item);
  const [qty, setQty] = useState(1);
  const [selectedAddons, setSelectedAddons] = useState<MenuItem[]>([]);
  const [note, setNote] = useState('');
  const [servingType, setServingType] = useState<ServingType | null>(null);
  const [servingError, setServingError] = useState(false);
  const [sugarLevelPercent, setSugarLevelPercent] = useState<number | null>(null);
  const [sugarError, setSugarError] = useState(false);

  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = ''; };
  }, []);

  useEffect(() => {
    if (servingOptions.length === 1) {
      setServingType(servingOptions[0]);
      setServingError(false);
      return;
    }
    if (servingOptions.length === 0) {
      setServingType(null);
    }
  }, [item.id]);

  const toggleAddon = (addon: MenuItem) =>
    setSelectedAddons((prev) =>
      prev.some((a) => a.id === addon.id) ? prev.filter((a) => a.id !== addon.id) : [...prev, addon]
    );

  const handleDone = () => {
    if (servingOptions.length > 0 && !servingType) {
      setServingError(true);
      return;
    }
    setServingError(false);

    if (itemSupportsSugarLevel && sugarLevelPercent == null) {
      setSugarError(true);
      return;
    }
    setSugarError(false);

    onDone(item, {
      addonIds: selectedAddons.map((addon) => addon.id),
      note,
      qty,
      servingType: servingType ?? undefined,
      sugarLevelPercent: sugarLevelPercent ?? undefined,
    });
  };

  const addonTotal = selectedAddons.reduce((s, a) => s + Number(a.price), 0) * qty;
  const total = Number(item.price) * qty + addonTotal;

  return createPortal(
    <div
      className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
      onClick={(e) => e.target === e.currentTarget && onCancel()}
    >
      <div className="bg-white rounded-2xl w-full max-w-md max-h-[88vh] overflow-y-auto shadow-2xl animate-slide-up">
        {/* Header */}
        <div className="sticky top-0 bg-white/95 backdrop-blur-sm px-6 pt-6 pb-4 border-b border-gray-100 flex justify-between items-start gap-3 rounded-t-2xl">
          <div className="flex-1 min-w-0">
            <h2 className="font-bold text-gray-900 text-lg leading-tight">{item.name}</h2>
            {item.description && (
              <p className="text-xs text-gray-500 mt-0.5 leading-relaxed">{item.description}</p>
            )}
            <p className="text-[#001C98] font-bold text-base mt-1">
              ₱{Number(item.price).toFixed(2)}
            </p>
          </div>
          <button
            onClick={onCancel}
            className="shrink-0 p-1.5 rounded-xl hover:bg-gray-100 transition text-gray-400"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="px-6 py-5 space-y-5">
          {/* Serving type */}
          {servingOptions.length > 0 && (
            <div>
              <div className="flex items-center justify-between mb-3">
                <p className="text-sm font-semibold text-gray-800">
                  Serving Type <span className="text-[#EF4444]">*</span>
                </p>
                {servingError && (
                  <span className="text-[11px] text-[#EF4444] font-medium animate-fade-in">Please select one</span>
                )}
              </div>
              <div className={cn('grid gap-2', servingOptions.length === 2 ? 'grid-cols-2' : 'grid-cols-3')}>
                {([
                  { value: 'HOT', label: 'Hot', icon: '☕' },
                  { value: 'ICED', label: 'Cold', icon: '🧊' },
                  { value: 'BLENDED', label: 'Blended', icon: '🥤' },
                ] as { value: ServingType; label: string; icon: string }[])
                  .filter(({ value }) => servingOptions.includes(value))
                  .map(({ value, label, icon }) => (
                    <button
                      key={value}
                      onClick={() => {
                        setServingType(value);
                        setServingError(false);
                      }}
                      className={cn(
                        'flex items-center justify-center gap-1.5 py-3 rounded-xl border-2 font-semibold text-sm transition-all duration-150',
                        servingType === value
                          ? 'border-[#001C98] bg-[#001C98] text-white shadow-md shadow-[#001C98]/20'
                          : servingError
                            ? 'border-[#EF4444]/60 bg-[#EF4444]/5 text-gray-600 hover:border-[#001C98]/30'
                            : 'border-[#E5D3B3]/60 bg-[#FDFBF7] text-gray-600 hover:border-[#001C98]/30'
                      )}
                    >
                      <span>{icon}</span>
                      {label}
                    </button>
                  ))}
              </div>
            </div>
          )}

          {/* Quantity */}
          <div>
            <p className="text-sm font-semibold text-gray-800 mb-3">Quantity</p>
            <div className="flex items-center gap-4">
              <button
                onClick={() => setQty((q) => Math.max(1, q - 1))}
                className="w-9 h-9 rounded-xl border border-[#E5D3B3]/60 bg-[#FDFBF7] flex items-center justify-center hover:bg-white transition"
              >
                <Minus className="w-4 h-4 text-gray-600" />
              </button>
              <span className="font-bold text-gray-900 w-6 text-center text-lg">{qty}</span>
              <button
                onClick={() => setQty((q) => q + 1)}
                className="w-9 h-9 rounded-xl border border-[#E5D3B3]/60 bg-[#FDFBF7] flex items-center justify-center hover:bg-white transition"
              >
                <Plus className="w-4 h-4 text-gray-600" />
              </button>
            </div>
          </div>

          {/* Sugar level */}
          {itemSupportsSugarLevel && (
            <div>
              <div className="flex items-center justify-between mb-3">
                <p className="text-sm font-semibold text-gray-800">
                  Sugar Level <span className="text-[#EF4444]">*</span>
                </p>
                {sugarError && (
                  <span className="text-[11px] text-[#EF4444] font-medium animate-fade-in">Please select one</span>
                )}
              </div>
              <div className="grid grid-cols-5 gap-2">
                {SUGAR_LEVELS.map((level) => (
                  <button
                    key={level}
                    onClick={() => {
                      setSugarLevelPercent(level);
                      setSugarError(false);
                    }}
                    className={cn(
                      'py-2.5 rounded-xl border text-sm font-semibold transition-all duration-150',
                      sugarLevelPercent === level
                        ? 'border-[#001C98] bg-[#001C98] text-white shadow-md shadow-[#001C98]/20'
                        : sugarError
                          ? 'border-[#EF4444]/60 bg-[#EF4444]/5 text-gray-600'
                          : 'border-[#E5D3B3]/60 bg-[#FDFBF7] text-gray-600 hover:border-[#001C98]/30'
                    )}
                  >
                    {level}%
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Add-ons */}
          {itemSupportsAddons && addons.length > 0 && (
            <div>
              <p className="text-sm font-semibold text-gray-800 mb-3">Add-ons</p>
              <div className="space-y-2">
                {addons.map((addon) => {
                  const selected = selectedAddons.some((a) => a.id === addon.id);
                  return (
                    <button
                      key={addon.id}
                      onClick={() => toggleAddon(addon)}
                      className={cn(
                        'w-full flex items-center justify-between px-3.5 py-3 rounded-xl border text-left transition-all duration-150',
                        selected
                          ? 'border-[#001C98] bg-[#001C98]/5'
                          : 'border-[#E5D3B3]/60 bg-[#FDFBF7]/50 hover:border-[#001C98]/25'
                      )}
                    >
                      <div className="flex items-center gap-2.5">
                        <div
                          className={cn(
                            'w-4 h-4 rounded border-2 flex items-center justify-center transition-all',
                            selected ? 'border-[#001C98] bg-[#001C98]' : 'border-gray-300'
                          )}
                        >
                          {selected && (
                            <Check className="w-2.5 h-2.5 text-white" strokeWidth={3} />
                          )}
                        </div>
                        <span
                          className={cn(
                            'text-sm font-medium',
                            selected ? 'text-[#001C98]' : 'text-gray-700'
                          )}
                        >
                          {addon.name}
                        </span>
                        {addon.description && (
                          <span className="text-[10px] text-gray-400">{addon.description}</span>
                        )}
                      </div>
                      <span
                        className={cn(
                          'text-sm font-semibold shrink-0',
                          selected ? 'text-[#001C98]' : 'text-gray-500'
                        )}
                      >
                        +₱{Number(addon.price).toFixed(2)}
                      </span>
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {/* Note */}
          <div>
            <p className="text-sm font-semibold text-gray-800 mb-2">Special Instructions</p>
            <textarea
              placeholder="e.g. no straw, extra napkin, less ice"
              value={note}
              maxLength={50}
              onChange={(e) => setNote(e.target.value.slice(0, 50))}
              rows={2}
              className="w-full px-3.5 py-2.5 text-sm rounded-xl border border-[#E5D3B3]/60 bg-[#FDFBF7]/50 focus:outline-none focus:ring-2 focus:ring-[#001C98]/20 focus:border-[#001C98]/30 transition resize-none placeholder:text-gray-400"
            />
          </div>

          {/* Total */}
          <div className="flex justify-between items-center bg-[#FDFBF7] rounded-xl px-4 py-3 border border-[#E5D3B3]/40">
            <span className="text-sm text-gray-500 font-medium">
              Total
              {itemSupportsAddons && selectedAddons.length > 0 && (
                <span className="ml-1 text-[11px] text-gray-400">
                  (incl. {selectedAddons.length} add-on{selectedAddons.length > 1 ? 's' : ''})
                </span>
              )}
            </span>
            <span className="font-bold text-[#001C98] text-lg">₱{total.toFixed(2)}</span>
          </div>

          {errorMsg && (
            <p className="text-sm text-[#EF4444] bg-[#EF4444]/8 border border-[#EF4444]/20 rounded-xl px-3.5 py-2.5">
              {errorMsg}
            </p>
          )}

          {/* Actions */}
          <div className="flex gap-3 pb-1">
            <button
              onClick={onCancel}
              disabled={adding}
              className="flex-1 py-3 rounded-xl border border-[#E5D3B3]/60 text-gray-600 font-semibold hover:bg-gray-50 transition disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              onClick={handleDone}
              disabled={adding}
              className="flex-[2] py-3 bg-[#001C98] hover:bg-[#0025B8] text-white font-semibold rounded-xl disabled:opacity-60 transition flex items-center justify-center gap-2 shadow-sm shadow-[#001C98]/20 active:scale-[0.99]"
            >
              {adding ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <>
                  <ShoppingCart className="w-4 h-4" /> Done
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>,
    document.body
  );
}

export default function MenuPage() {
  const [menu, setMenu] = useState<MenuItem[]>([]);
  const [category, setCategory] = useState<MenuCategoryKey>('all');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedItem, setSelectedItem] = useState<MenuItem | null>(null);
  const [adding, setAdding] = useState(false);
  const [addError, setAddError] = useState('');
  const [toast, setToast] = useState('');
  const toastTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  const loadMenu = async () => {
    setLoading(true);
    setError('');
    const res = await getMenu();
    if (res.success && res.data) {
      setMenu(normalizeMenuItems(res.data));
    } else {
      setMenu([]);
      setError(res.error || 'Could not load menu. Make sure the backend is running.');
    }
    setLoading(false);
  };

  useEffect(() => {
    loadMenu();
    return () => {
      if (toastTimer.current) clearTimeout(toastTimer.current);
    };
  }, []);

  const addons = menu.filter(isAddonItem);
  const filtered = filterMenuByCategory(menu, category);

  const showToast = (msg: string) => {
    setToast(msg);
    if (toastTimer.current) clearTimeout(toastTimer.current);
    toastTimer.current = setTimeout(() => setToast(''), 3500);
  };

  const handleDone = async (
    item: MenuItem,
    payload: {
      addonIds: number[];
      note: string;
      qty: number;
      servingType?: ServingType;
      sugarLevelPercent?: number;
    }
  ) => {
    setAdding(true);
    setAddError('');

    const res = await addToCart(item.id, payload.qty, {
      customizationNotes: payload.note.trim().slice(0, 50) || undefined,
      servingType: payload.servingType,
      sugarLevelPercent: payload.sugarLevelPercent,
      addonIds: payload.addonIds,
    });
    if (!res.success) {
      setAddError(res.error || 'Failed to add to cart. Please try again.');
      setAdding(false);
      return;
    }

    setAdding(false);
    setSelectedItem(null);
    showToast(`${item.name} added to cart!`);
  };

  if (loading) return <LoadingState message="Loading menu..." />;

  return (
    <div className="space-y-5 animate-fade-in">
      <PageHeader title="Menu" subtitle="Pick your favorites — customize them your way." />

      {/* Toast notification */}
      {toast && (
        <div className="fixed top-5 left-1/2 -translate-x-1/2 z-50 flex items-center gap-2.5 px-5 py-3 bg-[#001C98] text-white text-sm font-semibold rounded-2xl shadow-xl shadow-[#001C98]/30 animate-fade-in pointer-events-none">
          <Check className="w-4 h-4 text-green-300 shrink-0" strokeWidth={3} />
          {toast} &nbsp;
          <span className="text-white/70 font-normal text-xs">Check it out in your cart.</span>
        </div>
      )}

      {error && (
        <div className="text-sm p-3.5 rounded-xl bg-[#EF4444]/10 text-[#EF4444] border border-[#EF4444]/20 flex items-center justify-between gap-3">
          <span>{error}</span>
          <button onClick={loadMenu} className="text-xs font-semibold underline shrink-0">
            Retry
          </button>
        </div>
      )}

      {/* Category tabs */}
      <div className="flex gap-2 overflow-x-auto pb-1 -mx-1 px-1">
        {MENU_CATEGORY_TABS.map((c) => {
          const Icon = categoryIcons[c.key] || LayoutGrid;
          const active = category === c.key;
          const count = countMenuByTab(menu, c.key);
          return (
            <button
              key={c.key}
              onClick={() => setCategory(c.key)}
              className={cn(
                'flex items-center gap-2 px-4 py-2.5 rounded-full text-sm font-semibold whitespace-nowrap transition-all duration-200',
                active
                  ? 'bg-[#001C98] text-white shadow-md shadow-[#001C98]/25'
                  : 'bg-white/80 border border-[#E5D3B3]/60 text-gray-600 hover:border-[#001C98]/20'
              )}
            >
              <Icon className="w-4 h-4" />
              {c.label}
              <span
                className={cn(
                  'text-[10px] font-bold px-1.5 py-0.5 rounded-full min-w-[1.25rem] text-center',
                  active ? 'bg-white/20 text-white' : 'bg-gray-100 text-gray-500'
                )}
              >
                {count}
              </span>
            </button>
          );
        })}
      </div>

      {!error && (
        <div
          className={cn(
            'rounded-xl px-4 py-2.5 text-xs font-medium text-gray-500 bg-gradient-to-r border',
            tabAccent[category] || tabAccent.all
          )}
        >
          {filtered.length} item{filtered.length !== 1 ? 's' : ''}
          {category === 'all' ? ' available' : ` in ${getCategoryLabel(category)}`}
        </div>
      )}

      {/* Menu grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-3">
        {filtered.map((item) => (
          (() => {
            const available = item.isAvailable !== false;
            return (
          <StudentCard
            key={item.id}
            hover
            className={cn(
              'p-4 border-l-4 flex flex-col gap-3 transition',
              !available && 'opacity-60 grayscale cursor-not-allowed',
              categoryAccent[categoryTabKey(item.category) ?? ''] || 'border-l-[#001C98]'
            )}
          >
            <div className="flex justify-between items-start gap-3">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <h3 className="font-bold text-gray-900">{item.name}</h3>
                  {category === 'all' && (
                    <span className="text-[10px] font-semibold uppercase tracking-wide px-2 py-0.5 rounded-full bg-gray-100 text-gray-500">
                      {getItemCategoryLabel(item)}
                    </span>
                  )}
                </div>
                {item.description && (
                  <p className="text-xs text-gray-500 mt-1 leading-relaxed">{item.description}</p>
                )}
              </div>
              <p className="font-bold text-[#001C98] text-lg shrink-0">
                ₱{Number(item.price).toFixed(2)}
              </p>
            </div>
            <button
              disabled={!available}
              onClick={() => {
                if (!available) return;
                setAddError('');
                setSelectedItem(item);
              }}
              className={cn(
                'flex items-center justify-center gap-2 w-full py-2.5 text-sm font-semibold rounded-xl transition shadow-sm active:scale-[0.99]',
                available
                  ? 'bg-[#001C98] hover:bg-[#0025B8] text-white shadow-[#001C98]/20'
                  : 'bg-gray-200 text-gray-500 cursor-not-allowed shadow-transparent'
              )}
            >
              <Plus className="w-4 h-4" /> {available ? 'Add to Cart' : 'Unavailable'}
            </button>
          </StudentCard>
            );
          })()
        ))}
        {!error && filtered.length === 0 && (
          <div className="col-span-full text-center py-12 text-gray-400">
            <Coffee className="w-10 h-10 mx-auto mb-3 opacity-40" />
            <p className="text-sm">
              {menu.filter((i) => !isAddonItem(i)).length === 0
                ? 'No menu items available right now.'
                : `No items in ${getCategoryLabel(category)}.`}
            </p>
          </div>
        )}
      </div>

      {/* Add-to-Cart Modal */}
      {selectedItem && (
        <AddToCartModal
          item={selectedItem}
          addons={addons}
          onCancel={() => {
            if (!adding) setSelectedItem(null);
          }}
          onDone={handleDone}
          adding={adding}
          errorMsg={addError}
        />
      )}
    </div>
  );
}
