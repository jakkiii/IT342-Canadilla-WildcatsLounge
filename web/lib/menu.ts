import type { MenuItem } from './api';

export type ServingType = 'HOT' | 'ICED' | 'BLENDED';
export const SUGAR_LEVELS = [0, 25, 50, 75, 100] as const;

/** Tab keys for the student menu UI. */
export type MenuCategoryKey = 'all' | 'coffee' | 'non-coffee' | 'treat';

/** Maps normalized API category values to a tab group. */
const CATEGORY_TO_TAB: Record<string, Exclude<MenuCategoryKey, 'all'>> = {
  coffee: 'coffee',
  'flavored-latte': 'coffee',
  'matcha-series': 'coffee',
  'coffee-add-on': 'coffee',
  beverages: 'non-coffee',
  'non-coffee': 'non-coffee',
  treat: 'treat',
  treats: 'treat',
};

export function normalizeCategory(raw: string | undefined): string {
  if (!raw) return '';
  return raw
    .toLowerCase()
    .trim()
    .replace(/_/g, '-')
    .replace(/\s+/g, '-');
}

export function categoryTabKey(raw: string | undefined): Exclude<MenuCategoryKey, 'all'> | null {
  const normalized = normalizeCategory(raw);
  return CATEGORY_TO_TAB[normalized] ?? null;
}

export function normalizeMenuItem(item: MenuItem): MenuItem {
  const rawCategory = item.categoryLabel || String(item.category);
  const normalized = normalizeCategory(rawCategory);
  return {
    ...item,
    category: normalized || 'other',
    categoryLabel: rawCategory.replace(/_/g, ' '),
    price: Number(item.price),
    isAvailable: item.isAvailable !== false,
  };
}

export function normalizeMenuItems(items: MenuItem[]): MenuItem[] {
  return items.map(normalizeMenuItem);
}

export function isAddonItem(item: MenuItem): boolean {
  return normalizeCategory(item.category) === 'coffee-add-on';
}

export function getAllowedServingTypes(item: MenuItem): ServingType[] {
  const allowed: ServingType[] = [];
  if (item.allowHot) allowed.push('HOT');
  if (item.allowIced) allowed.push('ICED');
  if (item.allowBlended) allowed.push('BLENDED');
  return allowed;
}

export function supportsAddons(item: MenuItem): boolean {
  return Boolean(item.allowAddons);
}

export function supportsSugarLevel(item: MenuItem): boolean {
  return Boolean(item.allowSugarLevel);
}

export function filterMenuByCategory(items: MenuItem[], category: MenuCategoryKey): MenuItem[] {
  const nonAddons = items.filter((item) => !isAddonItem(item));
  if (category === 'all') return nonAddons;
  return nonAddons.filter((item) => categoryTabKey(item.category) === category);
}

export function countMenuByTab(items: MenuItem[], tab: MenuCategoryKey): number {
  const nonAddons = items.filter((item) => !isAddonItem(item));
  if (tab === 'all') return nonAddons.length;
  return nonAddons.filter((item) => categoryTabKey(item.category) === tab).length;
}

export const MENU_CATEGORY_TABS = [
  { key: 'all' as const, label: 'All' },
  { key: 'coffee' as const, label: 'Coffees' },
  { key: 'non-coffee' as const, label: 'Drinks' },
  { key: 'treat' as const, label: 'Treats' },
];

const TAB_LABELS: Record<string, string> = {
  all: 'All',
  coffee: 'Coffees',
  'non-coffee': 'Drinks',
  treat: 'Treats',
};

export function getCategoryLabel(key: string): string {
  const tab = TAB_LABELS[key];
  if (tab) return tab;
  return key.replace(/-/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase());
}

export function getItemCategoryLabel(item: MenuItem): string {
  if (item.categoryLabel) return item.categoryLabel;
  return getCategoryLabel(item.category);
}
