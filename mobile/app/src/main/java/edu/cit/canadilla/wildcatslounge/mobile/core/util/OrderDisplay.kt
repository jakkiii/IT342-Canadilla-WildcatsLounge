package edu.cit.canadilla.wildcatslounge.mobile.core.util

import edu.cit.canadilla.wildcatslounge.mobile.core.model.CartItemData
import edu.cit.canadilla.wildcatslounge.mobile.core.model.GroupedCartItem
import edu.cit.canadilla.wildcatslounge.mobile.core.model.GroupedOrderItem
import edu.cit.canadilla.wildcatslounge.mobile.core.model.MenuItemData
import edu.cit.canadilla.wildcatslounge.mobile.core.model.OrderItemData

object OrderDisplay {
	private fun normalizeName(value: String?): String = (value ?: "").trim().lowercase()

	fun createAddonNameSet(menuItems: List<MenuItemData>): Set<String> =
		menuItems.filter { MenuHelpers.isAddonItem(it) }
			.map { normalizeName(it.name) }
			.toSet()

	fun formatCustomizationMeta(
		servingType: String?,
		sugarLevelPercent: Int?,
		customizationNotes: String?
	): String? {
		val details = mutableListOf<String>()
		if (!servingType.isNullOrBlank() && servingType != "NONE") {
			details.add(servingType.lowercase().replaceFirstChar { it.uppercase() })
		}
		if (sugarLevelPercent != null) {
			details.add("$sugarLevelPercent% sugar")
		}
		val notes = customizationNotes?.trim()
		if (!notes.isNullOrBlank()) {
			details.add(notes)
		}
		return details.takeIf { it.isNotEmpty() }?.joinToString(" — ")
	}

	fun groupCartItems(items: List<CartItemData>, addonNames: Set<String>): List<GroupedCartItem> {
		val grouped = mutableListOf<GroupedCartItem>()
		val byId = mutableMapOf<Long, GroupedCartItem>()
		var currentMain: GroupedCartItem? = null

		for (item in items) {
			if (item.parentItemId != null) continue
			val isAddon = addonNames.contains(normalizeName(item.itemName))
			val entry = GroupedCartItem(item, mutableListOf())
			grouped.add(entry)
			byId[item.id] = entry
			if (!isAddon) currentMain = entry
		}

		for (item in items) {
			val parentId = item.parentItemId
			if (parentId != null) {
				byId[parentId]?.addons?.add(item)
				continue
			}
			if (byId.containsKey(item.id)) continue

			val isAddon = addonNames.contains(normalizeName(item.itemName))
			if (isAddon && currentMain != null) {
				currentMain.addons.add(item)
				continue
			}
			val entry = GroupedCartItem(item, mutableListOf())
			grouped.add(entry)
			byId[item.id] = entry
			if (!isAddon) currentMain = entry
		}

		return grouped
	}

	fun groupOrderItems(items: List<OrderItemData>, addonNames: Set<String>): List<GroupedOrderItem> {
		val grouped = mutableListOf<GroupedOrderItem>()
		val byId = mutableMapOf<Long, GroupedOrderItem>()
		var currentMain: GroupedOrderItem? = null

		for (item in items) {
			if (item.parentItemId != null) continue
			val isAddon = addonNames.contains(normalizeName(item.itemName))
			val entry = GroupedOrderItem(item, mutableListOf())
			grouped.add(entry)
			byId[item.id] = entry
			if (!isAddon) currentMain = entry
		}

		for (item in items) {
			val parentId = item.parentItemId
			if (parentId != null) {
				byId[parentId]?.addons?.add(item)
				continue
			}
			if (byId.containsKey(item.id)) continue

			val isAddon = addonNames.contains(normalizeName(item.itemName))
			if (isAddon && currentMain != null) {
				currentMain.addons.add(item)
				continue
			}
			val entry = GroupedOrderItem(item, mutableListOf())
			grouped.add(entry)
			byId[item.id] = entry
			if (!isAddon) currentMain = entry
		}

		return grouped
	}
}
