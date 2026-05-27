package edu.cit.canadilla.wildcatslounge.mobile.core.util

import edu.cit.canadilla.wildcatslounge.mobile.core.model.MenuItemData
import edu.cit.canadilla.wildcatslounge.mobile.core.model.ServingType

object MenuHelpers {
	const val CATEGORY_ALL = "all"
	const val CATEGORY_COFFEE = "coffee"
	const val CATEGORY_DRINKS = "non-coffee"
	const val CATEGORY_TREAT = "treat"

	val SUGAR_LEVELS = listOf(0, 25, 50, 75, 100)

	private val categoryToTab = mapOf(
		"coffee" to CATEGORY_COFFEE,
		"flavored-latte" to CATEGORY_COFFEE,
		"matcha-series" to CATEGORY_COFFEE,
		"coffee-add-on" to CATEGORY_COFFEE,
		"beverages" to CATEGORY_DRINKS,
		"non-coffee" to CATEGORY_DRINKS,
		"treat" to CATEGORY_TREAT,
		"treats" to CATEGORY_TREAT
	)

	fun normalizeCategory(raw: String?): String {
		return raw.orEmpty()
			.lowercase()
			.trim()
			.replace('_', '-')
			.replace(Regex("\\s+"), "-")
	}

	fun categoryTabKey(raw: String?): String? = categoryToTab[normalizeCategory(raw)]

	fun isAddonItem(item: MenuItemData): Boolean =
		normalizeCategory(item.category) == "coffee-add-on"

	fun getAllowedServingTypes(item: MenuItemData): List<ServingType> {
		val allowed = mutableListOf<ServingType>()
		if (item.allowHot) allowed.add(ServingType.HOT)
		if (item.allowIced) allowed.add(ServingType.ICED)
		if (item.allowBlended) allowed.add(ServingType.BLENDED)
		return allowed
	}

	fun supportsAddons(item: MenuItemData): Boolean = item.allowAddons

	fun supportsSugarLevel(item: MenuItemData): Boolean = item.allowSugarLevel

	fun filterMenuByCategory(items: List<MenuItemData>, category: String): List<MenuItemData> {
		val nonAddons = items.filter { !isAddonItem(it) }
		if (category == CATEGORY_ALL) return nonAddons
		return nonAddons.filter { categoryTabKey(it.category) == category }
	}

	fun getAddonItems(items: List<MenuItemData>): List<MenuItemData> =
		items.filter { isAddonItem(it) && it.isAvailable }

	fun tabLabel(key: String): String = when (key) {
		CATEGORY_ALL -> "All"
		CATEGORY_COFFEE -> "Coffees"
		CATEGORY_DRINKS -> "Drinks"
		CATEGORY_TREAT -> "Treats"
		else -> key.replace('-', ' ').replaceFirstChar { it.uppercase() }
	}
}
