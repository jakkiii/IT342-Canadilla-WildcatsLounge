import type { CartItem, MenuItem, OrderItem } from './api';
import { isAddonItem, normalizeMenuItems } from './menu';

type DisplayItem = {
  id: number;
  itemName: string;
  customizationNotes?: string;
  parentItemId?: number | null;
  servingType?: string | null;
  sugarLevelPercent?: number | null;
};

export type GroupedDisplayItem<T extends DisplayItem> = T & {
  addons: T[];
  standaloneAddon?: boolean;
};

function normalizeName(value: string | undefined): string {
  return (value || '').trim().toLowerCase();
}

export function createAddonNameSet(menuItems: MenuItem[]): Set<string> {
  return new Set(
    normalizeMenuItems(menuItems)
      .filter(isAddonItem)
      .map((item) => normalizeName(item.name))
  );
}

export function formatCustomizationMeta(item: DisplayItem): string | null {
  const details: string[] = [];
  if (item.servingType && item.servingType !== 'NONE') {
    details.push(item.servingType.charAt(0) + item.servingType.slice(1).toLowerCase());
  }
  if (typeof item.sugarLevelPercent === 'number') {
    details.push(`${item.sugarLevelPercent}% sugar`);
  }
  if (item.customizationNotes?.trim()) {
    details.push(item.customizationNotes.trim());
  }
  return details.length > 0 ? details.join(' — ') : null;
}

export function groupOrderItems(orderItems: OrderItem[], addonNames: Set<string>): GroupedDisplayItem<OrderItem>[] {
  return groupDisplayItems(orderItems, addonNames);
}

export function groupCartItems(cartItems: CartItem[], addonNames: Set<string>): GroupedDisplayItem<CartItem>[] {
  return groupDisplayItems(cartItems, addonNames);
}

function groupDisplayItems<T extends DisplayItem>(items: T[], addonNames: Set<string>): GroupedDisplayItem<T>[] {
  const grouped: GroupedDisplayItem<T>[] = [];
  const byId = new Map<number, GroupedDisplayItem<T>>();
  let currentMainItem: GroupedDisplayItem<T> | null = null;

  for (const item of items) {
    if (item.parentItemId != null) {
      continue;
    }

    const isAddon = addonNames.has(normalizeName(item.itemName));
    const nextItem: GroupedDisplayItem<T> = {
      ...item,
      addons: [],
      standaloneAddon: isAddon,
    };
    grouped.push(nextItem);
    byId.set(item.id, nextItem);
    currentMainItem = isAddon ? currentMainItem : nextItem;
  }

  for (const item of items) {
    if (item.parentItemId != null) {
      const parent = byId.get(item.parentItemId);
      if (parent) {
        parent.addons.push(item);
        continue;
      }
    }

    if (item.parentItemId == null && byId.has(item.id)) {
      continue;
    }

    const isAddon = addonNames.has(normalizeName(item.itemName));
    if (isAddon && currentMainItem) {
      currentMainItem.addons.push(item);
      continue;
    }

    const nextItem: GroupedDisplayItem<T> = {
      ...item,
      addons: [],
      standaloneAddon: isAddon,
    };
    grouped.push(nextItem);
    if (item.parentItemId == null) {
      byId.set(item.id, nextItem);
      currentMainItem = isAddon ? currentMainItem : nextItem;
    }
  }

  return grouped;
}
