package com.habitbuilder.mission

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.habitbuilder.R
import com.habitbuilder.mission.data.MissionDetail
import java.util.Collections

class MissionAdapter(
    private val isEnabled:Boolean = false,
    private val missionClickedCallback: MissionViewHolder.MissionClickedCallback
): RecyclerView.Adapter<MissionViewHolder>(), ItemTouchHelperAdapter {
    var missionList: List<MissionDetail> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            if (isEnabled) R.layout.layout_mission_item else R.layout.layout_disabled_mission_item,
            parent,
            false
        )
        return MissionViewHolder(itemView, missionClickedCallback)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.onBind(isEnabled, missionList[position], position)
    }

    override fun getItemCount(): Int {
        return missionList.size
    }
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if(!isEnabled) return
        Collections.swap(missionList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int) {
    }
}
interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemSwiped(position: Int)
}
