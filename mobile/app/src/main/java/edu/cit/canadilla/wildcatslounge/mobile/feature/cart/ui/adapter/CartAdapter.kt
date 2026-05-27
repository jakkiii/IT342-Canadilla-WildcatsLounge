package edu.cit.canadilla.wildcatslounge.mobile.feature.cart.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.GroupedCartItem
import edu.cit.canadilla.wildcatslounge.mobile.core.util.OrderDisplay
import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyOutlinedStyle

class CartAdapter(
	private val items: MutableList<GroupedCartItem>,
	private val onQuantityChange: (GroupedCartItem, Int) -> Unit,
	private val onRemove: (GroupedCartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

	fun updateItems(newItems: List<GroupedCartItem>) {
		items.clear()
		items.addAll(newItems)
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
		return CartViewHolder(view)
	}

	override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
		holder.bind(items[position])
	}

	override fun getItemCount(): Int = items.size

	inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private val tvName: TextView = itemView.findViewById(R.id.tvCartName)
		private val tvMeta: TextView = itemView.findViewById(R.id.tvCartMeta)
		private val tvAddons: TextView = itemView.findViewById(R.id.tvCartAddons)
		private val tvPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
		private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
		private val btnMinus: MaterialButton = itemView.findViewById(R.id.btnMinus)
		private val btnPlus: MaterialButton = itemView.findViewById(R.id.btnPlus)
		private val btnRemove: MaterialButton = itemView.findViewById(R.id.btnRemove)

		init {
			btnMinus.applyOutlinedStyle()
			btnPlus.applyOutlinedStyle()
			btnRemove.applyOutlinedStyle()
		}

		fun bind(group: GroupedCartItem) {
			val item = group.main
			tvName.text = item.itemName
			val meta = OrderDisplay.formatCustomizationMeta(
				item.servingType,
				item.sugarLevelPercent,
				item.customizationNotes
			)
			tvMeta.text = meta ?: "Standard preparation"
			tvMeta.visibility = View.VISIBLE

			if (group.addons.isNotEmpty()) {
				tvAddons.visibility = View.VISIBLE
				tvAddons.text = group.addons.joinToString("\n") { "+ ${it.itemName}" }
			} else {
				tvAddons.visibility = View.GONE
			}

			val addonTotal = group.addons.sumOf { it.lineTotal }
			tvPrice.text = "₱${"%.2f".format(item.lineTotal + addonTotal)}"
			tvQuantity.text = item.quantity.toString()

			btnMinus.setOnClickListener { onQuantityChange(group, item.quantity - 1) }
			btnPlus.setOnClickListener { onQuantityChange(group, item.quantity + 1) }
			btnRemove.setOnClickListener { onRemove(group) }
		}
	}
}
