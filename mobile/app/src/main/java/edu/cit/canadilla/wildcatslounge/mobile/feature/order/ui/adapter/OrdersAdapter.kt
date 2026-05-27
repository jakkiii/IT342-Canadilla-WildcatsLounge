package edu.cit.canadilla.wildcatslounge.mobile.feature.order.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.OrderData
import edu.cit.canadilla.wildcatslounge.mobile.core.util.OrderDisplay
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrdersAdapter(
	private val orders: MutableList<OrderData>,
	private val addonNames: Set<String>
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

	fun updateOrders(newOrders: List<OrderData>) {
		orders.clear()
		orders.addAll(newOrders)
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
		return OrderViewHolder(view)
	}

	override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
		holder.bind(orders[position])
	}

	override fun getItemCount(): Int = orders.size

	inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private val tvNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
		private val tvDate: TextView = itemView.findViewById(R.id.tvOrderDate)
		private val tvStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
		private val tvItems: TextView = itemView.findViewById(R.id.tvOrderItems)
		private val tvTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)

		fun bind(order: OrderData) {
			tvNumber.text = order.orderNumber
			tvDate.text = formatDate(order.createdAt)
			tvStatus.text = order.status.replaceFirstChar { it.uppercase() }

			val grouped = OrderDisplay.groupOrderItems(order.items, addonNames)
			tvItems.text = grouped.joinToString("\n") { group ->
				val meta = OrderDisplay.formatCustomizationMeta(
					group.main.servingType,
					group.main.sugarLevelPercent,
					group.main.customizationNotes
				)
				val addonLines = group.addons.joinToString(", ") { it.itemName }
				buildString {
					append("${group.main.quantity}x ${group.main.itemName}")
					if (meta != null) append(" ($meta)")
					if (addonLines.isNotEmpty()) append("\n  + $addonLines")
				}
			}
			tvTotal.text = "Total: ₱${"%.2f".format(order.totalAmount)}"
		}

		private fun formatDate(raw: String): String {
			return runCatching {
				val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
					timeZone = TimeZone.getTimeZone("UTC")
				}
				val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
				val date = parser.parse(raw.take(19)) ?: return raw
				formatter.format(date)
			}.getOrDefault(raw)
		}
	}
}
