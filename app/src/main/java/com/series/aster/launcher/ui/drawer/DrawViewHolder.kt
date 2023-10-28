package com.series.aster.launcher.ui.drawer

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.ItemDrawBinding
import com.series.aster.launcher.listener.OnItemClickedListener

class DrawViewHolder(private val binding: ItemDrawBinding,
                     private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
                     private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {
        binding.apply {
            appDrawName.text = appInfo.appName
            appDrawIcon.visibility = View.GONE
            Log.d("Tag", "Draw Adapter: ${appInfo.appName + appInfo.id}")
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