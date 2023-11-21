package com.series.aster.launcher.ui.favorite

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.ItemFavoriteBinding
import com.series.aster.launcher.listener.OnItemClickedListener
import com.series.aster.launcher.listener.OnItemMoveListener

class FavoriteAdapter(private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
) : ListAdapter<AppInfo, RecyclerView.ViewHolder>(DiffCallback()) ,OnItemMoveListener.OnItemActionListener{

    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding, onAppClickedListener, touchHelper)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        when (holder) {
            is FavoriteViewHolder -> {
                holder.bind(currentItem)
            }
        }
    }

    class DiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem == newItem
    }

    fun setItemTouchHelper(touchHelper: ItemTouchHelper) {
        this.touchHelper = touchHelper
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int): Boolean {
        Log.d("Tag", "List Adapter$newPosition")
        return false
    }

    override fun onViewSwiped(position: Int) {
        Log.d("Tag", "onViewMoved")
    }

    fun updateData(newData: List<AppInfo>) {
        notifyItemChanged(newData.size)
        submitList(newData)
        Log.d("Tag", "Collected Home Adapter : $newData")
    }
}