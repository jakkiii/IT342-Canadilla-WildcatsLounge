package edu.cit.canadilla.wildcatslounge.mobile.feature.cart.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.CartItemData

class CartAdapter(
    private val items: MutableList<CartItemData>,
    private val onQuantityChange: (CartItemData, Int) -> Unit,
    private val onRemove: (CartItemData) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun updateItems(newItems: List<CartItemData>) {
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
        private val tvPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        private val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        private val btnRemove: Button = itemView.findViewById(R.id.btnRemove)

        fun bind(item: CartItemData) {
            tvName.text = item.itemName
            tvMeta.text = item.servingType.name
            tvPrice.text = "P${item.lineTotal.toInt()}"
            tvQuantity.text = item.quantity.toString()

            btnMinus.setOnClickListener { onQuantityChange(item, item.quantity - 1) }
            btnPlus.setOnClickListener { onQuantityChange(item, item.quantity + 1) }
            btnRemove.setOnClickListener { onRemove(item) }
        }
    }
}

