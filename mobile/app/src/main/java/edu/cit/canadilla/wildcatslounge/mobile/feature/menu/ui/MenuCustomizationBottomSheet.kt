package edu.cit.canadilla.wildcatslounge.mobile.feature.menu.ui



import android.os.Bundle

import android.view.LayoutInflater

import android.view.View

import android.view.ViewGroup

import android.widget.Button

import android.widget.EditText

import android.widget.LinearLayout

import android.widget.TextView

import androidx.core.view.isVisible

import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.google.android.material.button.MaterialButton

import com.google.android.material.chip.Chip

import com.google.android.material.chip.ChipGroup

import edu.cit.canadilla.wildcatslounge.mobile.R

import edu.cit.canadilla.wildcatslounge.mobile.core.model.AddCartItemRequest

import edu.cit.canadilla.wildcatslounge.mobile.core.model.MenuItemData

import edu.cit.canadilla.wildcatslounge.mobile.core.model.ServingType

import edu.cit.canadilla.wildcatslounge.mobile.core.util.MenuHelpers

import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyChoiceChipStyle

import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyOutlinedStyle

import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyPrimaryStyle

import edu.cit.canadilla.wildcatslounge.mobile.feature.cart.data.CartRepository

import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext



class MenuCustomizationBottomSheet : BottomSheetDialogFragment() {

	private val cartRepository = CartRepository()



	private var item: MenuItemData? = null

	private var addons: List<MenuItemData> = emptyList()

	private var onAdded: (() -> Unit)? = null



	private var quantity = 1

	private var selectedServing: ServingType? = null

	private var selectedSugar: Int? = null

	private val selectedAddonIds = mutableSetOf<Long>()



	override fun onCreateView(

		inflater: LayoutInflater,

		container: ViewGroup?,

		savedInstanceState: Bundle?

	): View = inflater.inflate(R.layout.bottom_sheet_add_to_cart, container, false)



	override fun onStart() {

		super.onStart()

		val dialog = dialog as? BottomSheetDialog ?: return

		val sheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) ?: return

		val behavior = BottomSheetBehavior.from(sheet)

		behavior.state = BottomSheetBehavior.STATE_EXPANDED

		behavior.skipCollapsed = true

	}



	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val menuItem = item ?: return dismiss()

		val tvName = view.findViewById<TextView>(R.id.tvItemName)

		val tvDesc = view.findViewById<TextView>(R.id.tvItemDesc)

		val tvPrice = view.findViewById<TextView>(R.id.tvItemPrice)

		val tvServingLabel = view.findViewById<TextView>(R.id.tvServingLabel)

		val servingGroup = view.findViewById<LinearLayout>(R.id.servingGroup)

		val tvSugarLabel = view.findViewById<TextView>(R.id.tvSugarLabel)

		val chipSugar = view.findViewById<ChipGroup>(R.id.chipSugar)

		val tvAddonsLabel = view.findViewById<TextView>(R.id.tvAddonsLabel)

		val chipAddons = view.findViewById<ChipGroup>(R.id.chipAddons)

		val tvQty = view.findViewById<TextView>(R.id.tvQty)

		val etNotes = view.findViewById<EditText>(R.id.etNotes)

		val tvError = view.findViewById<TextView>(R.id.tvSheetError)

		val tvTotal = view.findViewById<TextView>(R.id.tvTotal)

		val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddToCart)



		btnAdd.applyPrimaryStyle()



		tvName.text = menuItem.name

		tvDesc.text = menuItem.description.orEmpty()

		tvDesc.isVisible = !menuItem.description.isNullOrBlank()

		tvPrice.text = "₱${"%.2f".format(menuItem.price)}"



		val servingOptions = MenuHelpers.getAllowedServingTypes(menuItem)

		if (servingOptions.isNotEmpty()) {

			tvServingLabel.isVisible = true

			servingGroup.isVisible = true

			if (servingOptions.size == 1) {

				selectedServing = servingOptions.first()

			}

			servingGroup.removeAllViews()

			servingOptions.forEach { type ->

				val button = MaterialButton(requireContext()).apply {

					text = when (type) {

						ServingType.HOT -> "Hot"

						ServingType.ICED -> "Cold"

						ServingType.BLENDED -> "Blended"

						ServingType.NONE -> "Standard"

					}

					isAllCaps = false

					applyOutlinedStyle()

					setOnClickListener {

						selectedServing = type

						refreshServingButtons(servingGroup, servingOptions, type)

						updateTotal(tvTotal, menuItem)

					}

				}

				val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

				params.marginEnd = 8

				servingGroup.addView(button, params)

			}

			selectedServing?.let { refreshServingButtons(servingGroup, servingOptions, it) }

		}



		if (MenuHelpers.supportsSugarLevel(menuItem)) {

			tvSugarLabel.isVisible = true

			chipSugar.isVisible = true

			chipSugar.isSingleSelection = true

			chipSugar.isSelectionRequired = false

			chipSugar.removeAllViews()

			MenuHelpers.SUGAR_LEVELS.forEach { level ->

				val chip = Chip(requireContext(), null, R.style.WildcatsChoiceChip).apply {

					id = View.generateViewId()

					text = "$level%"

					tag = level

					isCheckable = true
					
					isClickable = true
					isEnabled = true

					applyChoiceChipStyle()
					
					setOnClickListener {
						selectedSugar = level
						chipSugar.check(id)
						updateTotal(tvTotal, menuItem)
					}

				}

				chipSugar.addView(chip)

			}

			chipSugar.setOnCheckedStateChangeListener { group, checkedIds ->

				if (checkedIds.isEmpty()) {

					selectedSugar = null

				} else {

					val chip = group.findViewById<Chip>(checkedIds.first())

					selectedSugar = chip.tag as? Int

				}

				updateTotal(tvTotal, menuItem)

			}

		}



		if (MenuHelpers.supportsAddons(menuItem) && addons.isNotEmpty()) {

			tvAddonsLabel.isVisible = true

			chipAddons.isVisible = true

			chipAddons.removeAllViews()

			addons.forEach { addon ->

				val chip = Chip(requireContext(), null, R.style.WildcatsChoiceChip).apply {

					id = View.generateViewId()

					text = "${addon.name} (+₱${addon.price.toInt()})"

					tag = addon.id

					isCheckable = true

					isClickable = true
					isEnabled = true

					applyChoiceChipStyle()

					setOnClickListener {
						val nextChecked = !isChecked
						isChecked = nextChecked
						// Force Material chip to redraw with the new checked state.
						refreshDrawableState()
						if (nextChecked) selectedAddonIds.add(addon.id) else selectedAddonIds.remove(addon.id)
						updateTotal(tvTotal, menuItem)
					}

				}

				chipAddons.addView(chip)

			}

		}



		view.findViewById<MaterialButton>(R.id.btnQtyMinus).apply {

			applyOutlinedStyle()

			setOnClickListener {

				quantity = (quantity - 1).coerceAtLeast(1)

				tvQty.text = quantity.toString()

				updateTotal(tvTotal, menuItem)

			}

		}

		view.findViewById<MaterialButton>(R.id.btnQtyPlus).apply {

			applyOutlinedStyle()

			setOnClickListener {

				quantity += 1

				tvQty.text = quantity.toString()

				updateTotal(tvTotal, menuItem)

			}

		}



		updateTotal(tvTotal, menuItem)



		btnAdd.setOnClickListener {

			tvError.isVisible = false

			val servingOptionsRequired = MenuHelpers.getAllowedServingTypes(menuItem)

			if (servingOptionsRequired.isNotEmpty() && selectedServing == null) {

				tvError.text = "Please select a serving type."

				tvError.isVisible = true

				return@setOnClickListener

			}

			if (MenuHelpers.supportsSugarLevel(menuItem) && selectedSugar == null) {

				tvError.text = "Please select a sugar level."

				tvError.isVisible = true

				return@setOnClickListener

			}



			btnAdd.isEnabled = false

			CoroutineScope(Dispatchers.Main).launch {

				val request = AddCartItemRequest(

					menuItemId = menuItem.id,

					quantity = quantity,

					servingType = selectedServing,

					customizationNotes = etNotes.text.toString().trim().take(50).ifBlank { null },

					sugarLevelPercent = selectedSugar,

					addonIds = selectedAddonIds.takeIf { it.isNotEmpty() }?.toList()

				)

				val response = withContext(Dispatchers.IO) {

					cartRepository.addCartItem(request)

				}

				btnAdd.isEnabled = true

				if (response.success) {

					onAdded?.invoke()

					dismiss()

				} else {

					tvError.text = response.error ?: "Could not add to cart."

					tvError.isVisible = true

					view.findViewById<View>(R.id.sheetScroll).post {

						view.findViewById<View>(R.id.sheetScroll).scrollTo(0, tvError.top)

					}

				}

			}

		}

	}



	private fun refreshServingButtons(

		group: LinearLayout,

		options: List<ServingType>,

		selected: ServingType

	) {

		for (i in 0 until group.childCount) {

			val button = group.getChildAt(i) as? Button ?: continue

			val type = options.getOrNull(i) ?: continue

			if (type == selected) {

				button.applyPrimaryStyle()

			} else {

				button.applyOutlinedStyle()

			}

		}

	}



	private fun updateTotal(tvTotal: TextView, menuItem: MenuItemData) {

		val addonTotal = addons.filter { selectedAddonIds.contains(it.id) }

			.sumOf { it.price } * quantity

		val total = menuItem.price * quantity + addonTotal

		tvTotal.text = "Total: ₱${"%.2f".format(total)}"

	}



	companion object {

		fun newInstance(

			item: MenuItemData,

			addons: List<MenuItemData>,

			onAdded: () -> Unit

		): MenuCustomizationBottomSheet {

			return MenuCustomizationBottomSheet().apply {

				this.item = item

				this.addons = addons

				this.onAdded = onAdded

			}

		}

	}

}

