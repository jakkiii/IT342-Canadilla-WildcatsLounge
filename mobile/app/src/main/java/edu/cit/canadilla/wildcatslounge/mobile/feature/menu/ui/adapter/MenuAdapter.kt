package edu.cit.canadilla.wildcatslounge.mobile.feature.menu.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.MenuItemData
import edu.cit.canadilla.wildcatslounge.mobile.core.util.MenuHelpers
import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyPrimaryStyle

class MenuAdapter(
	private val items: MutableList<MenuItemData>,
	private val onCustomize: (MenuItemData) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

	fun updateItems(newItems: List<MenuItemData>) {
		items.clear()
		items.addAll(newItems)
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
		return MenuViewHolder(view)
	}

	override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
		holder.bind(items[position])
	}

	override fun getItemCount(): Int = items.size

	inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private val ivIcon: ImageView = itemView.findViewById(R.id.ivMenuIcon)
		private val tvName: TextView = itemView.findViewById(R.id.tvMenuName)
		private val tvPrice: TextView = itemView.findViewById(R.id.tvMenuPrice)
		private val tvDesc: TextView = itemView.findViewById(R.id.tvMenuDesc)
		private val btnAdd: MaterialButton = itemView.findViewById(R.id.btnAdd)
		private val servingGroup: ViewGroup = itemView.findViewById(R.id.servingGroup)

		fun bind(item: MenuItemData) {
			servingGroup.visibility = View.GONE
			val tab = MenuHelpers.categoryTabKey(item.category)
			ivIcon.setImageResource(
				when (tab) {
					MenuHelpers.CATEGORY_COFFEE -> R.drawable.ic_local_cafe
					MenuHelpers.CATEGORY_DRINKS -> R.drawable.ic_drink
					MenuHelpers.CATEGORY_TREAT -> R.drawable.ic_treat
					else -> R.drawable.ic_local_cafe
				}
			)
			tvName.text = item.name
			tvPrice.text = "₱${"%.2f".format(item.price)}"
			tvDesc.text = item.description.orEmpty()
			tvDesc.visibility = if (item.description.isNullOrBlank()) View.GONE else View.VISIBLE

			btnAdd.applyPrimaryStyle()
			btnAdd.isEnabled = item.isAvailable
			btnAdd.text = if (item.isAvailable) "Add" else "Unavailable"
			itemView.alpha = if (item.isAvailable) 1.0f else 0.55f
			val openCustomizer = View.OnClickListener {
				if (item.isAvailable) onCustomize(item)
			}
			btnAdd.setOnClickListener(openCustomizer)
			itemView.setOnClickListener(openCustomizer)
		}
	}
}
