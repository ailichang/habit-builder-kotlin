package com.habitbuilder.settings.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.habitbuilder.R

class FaqAdapter: ListAdapter<FaqItem, FaqViewHolder>(DiffCallback()){
    private lateinit var expandableView: View
    private lateinit var arrowIconView: ImageView
    private var expandedPosition = -1
    private var previousExpandedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        return FaqViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_faq_item, parent, false))
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.onBind(getItem(holder.bindingAdapterPosition))
        expandableView = holder.itemView.findViewById(R.id.faq_expandable_view)
        arrowIconView = holder.itemView.findViewById(R.id.faq_arrow_icon)

        val isExpanded = holder.bindingAdapterPosition == expandedPosition
        expandableView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        arrowIconView.setImageResource(if (isExpanded) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24)
        holder.itemView.isActivated = isExpanded
        if (isExpanded) {
            previousExpandedPosition = holder.bindingAdapterPosition
        }
        holder.itemView.setOnClickListener { v ->
            expandedPosition = if (isExpanded) -1 else holder.bindingAdapterPosition
            TransitionManager.beginDelayedTransition(
                (v as RelativeLayout),
                AutoTransition()
            )
            notifyItemChanged(previousExpandedPosition) //collapse other open cards, only allow one card to expand
            notifyItemChanged(position)
        }
    }

    class DiffCallback:DiffUtil.ItemCallback<FaqItem>(){
        override fun areItemsTheSame(oldItem: FaqItem, newItem: FaqItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FaqItem, newItem: FaqItem): Boolean {
            return oldItem == newItem
        }
    }
}