package com.series.aster.launcher.ui.hidden

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.ItemHiddenBinding
import com.series.aster.launcher.listener.OnItemClickedListener

class HiddenViewHolder(private val binding: ItemHiddenBinding,
                       private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
                       private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {
            binding.apply {
                appHiddenName.text = appInfo.appName
                Log.d("Tag", "Draw Adapter: ${appInfo.appName}")
            }

            itemView.setOnClickListener {
                onAppClickedListener.onAppClicked(appInfo)
            }

            itemView.setOnLongClickListener {
                onAppLongClickedListener.onAppLongClicked(appInfo)
                true
            }
    }
}