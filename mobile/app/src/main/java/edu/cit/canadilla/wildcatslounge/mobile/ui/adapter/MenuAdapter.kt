package edu.cit.canadilla.wildcatslounge.mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.model.MenuItemData
import edu.cit.canadilla.wildcatslounge.mobile.model.ServingType

class MenuAdapter(
    private val items: MutableList<MenuItemData>,
    private val onAdd: (MenuItemData, ServingType) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val selectedServing = mutableMapOf<Long, ServingType>()

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
        private val tvName: TextView = itemView.findViewById(R.id.tvMenuName)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvMenuDesc)
        private val btnAdd: Button = itemView.findViewById(R.id.btnAdd)
        private val servingGroup: LinearLayout = itemView.findViewById(R.id.servingGroup)

        fun bind(item: MenuItemData) {
            tvName.text = item.name
            tvDesc.text = item.description

            val options = mutableListOf<Pair<String, ServingType>>()
            item.hotPrice?.let { options.add("Hot P${it.toInt()}" to ServingType.HOT) }
            item.icedPrice?.let { options.add("Iced P${it.toInt()}" to ServingType.ICED) }
            item.blendedPrice?.let { options.add("Blend P${it.toInt()}" to ServingType.BLENDED) }
            if (options.isEmpty()) {
                options.add("P${item.price.toInt()}" to ServingType.NONE)
            }

            servingGroup.removeAllViews()
            options.forEachIndexed { index, pair ->
                val button = Button(itemView.context).apply {
                    text = pair.first
                    textSize = 12f
                    isAllCaps = false
                    setBackgroundResource(R.drawable.bg_input)
                    setTextColor(itemView.context.getColor(R.color.wildcats_blue))
                    val padding = (itemView.context.resources.displayMetrics.density * 8).toInt()
                    setPadding(padding, padding / 2, padding, padding / 2)
                }
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                if (index > 0) params.marginStart = 8
                button.layoutParams = params

                val selected = selectedServing[item.id] ?: pair.second
                if (selected == pair.second) {
                    button.setBackgroundResource(R.drawable.bg_primary_button)
                    button.setTextColor(itemView.context.getColor(R.color.white))
                }

                button.setOnClickListener {
                    selectedServing[item.id] = pair.second
                    notifyItemChanged(bindingAdapterPosition)
                }

                servingGroup.addView(button)
            }

            btnAdd.setOnClickListener {
                val selected = selectedServing[item.id] ?: options.first().second
                onAdd(item, selected)
            }
        }
    }
}
