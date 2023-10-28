package com.series.aster.launcher.ui.favorite

import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.ItemFavoriteBinding
import com.series.aster.launcher.listener.OnItemClickedListener

class FavoriteViewHolder(private val binding: ItemFavoriteBinding,
                         private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
                         private val touchHelper: ItemTouchHelper,
) :
    RecyclerView.ViewHolder(binding.root){

    init {
        binding.appFavoriteDragIcon.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(this)
            }
            false
        }
    }

    fun bind(appInfo: AppInfo) {
        binding.apply {
            appFavoriteName.text = appInfo.appName
            //appFavoriteDragIcon.visibility = View.GONE
            Log.d("Tag", "Draw Adapter: ${appInfo.appName}")
        }

        itemView.setOnClickListener {
            onAppClickedListener.onAppClicked(appInfo)
        }
    }
}