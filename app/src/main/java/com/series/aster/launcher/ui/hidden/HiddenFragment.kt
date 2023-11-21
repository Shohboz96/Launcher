package com.series.aster.launcher.ui.hidden

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.series.aster.launcher.R
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.FragmentHiddenBinding
import com.series.aster.launcher.helper.AppHelper
import com.series.aster.launcher.helper.FingerprintHelper
import com.series.aster.launcher.listener.OnItemClickedListener
import com.series.aster.launcher.ui.bottomsheetdialog.AppInfoBottomSheetFragment
import com.series.aster.launcher.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [HiddenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HiddenFragment : Fragment(), OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.OnAppLongClickedListener,
    OnItemClickedListener.BottomSheetDismissListener,
    OnItemClickedListener.OnAppStateClickListener,
    FingerprintHelper.Callback{
    private var _binding: FragmentHiddenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val hiddenAdapter: HiddenAdapter by lazy { HiddenAdapter(this, this) }

    private val viewModel: AppViewModel by viewModels()

    private lateinit var context: Context

    @Inject
    lateinit var fingerHelper: FingerprintHelper

    @Inject
    lateinit var appHelper: AppHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHiddenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appHelper.dayNightMod(requireContext(), binding.hiddenView)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()

        setupRecyclerView()
        observeHiddenApps()
    }

    private fun setupRecyclerView() {
        binding.hiddenAdapter.apply {
            adapter = hiddenAdapter
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(false)
        }

    }

    private fun observeHiddenApps() {
        viewModel.compareInstalledAppInfo()
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.hiddenApps.collect {
                hiddenAdapter.updateData(it)
                }
        }
    }

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock) {
            appHelper.launchApp(context, appInfo)
        } else {
            fingerHelper.startFingerprintAuth(appInfo,this)
        }
    }

    private fun showSelectedApp(appInfo: AppInfo) {
        val bottomSheetFragment = AppInfoBottomSheetFragment(appInfo)
        bottomSheetFragment.setOnBottomSheetDismissedListener(this)
        bottomSheetFragment.setOnAppStateClickListener(this)
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        binding.hiddenAdapter.scrollToPosition(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        observeHiddenApps()
    }

    override fun onAppLongClicked(appInfo: AppInfo) {
        showSelectedApp(appInfo)
    }

    override fun onBottomSheetDismissed() {
        TODO("Not yet implemented")
    }

    override fun onAppStateClicked(appInfo: AppInfo) {
        viewModel.update(appInfo)
    }

    override fun onAppClicked(appInfo: AppInfo) {
        observeBioAuthCheck(appInfo)
    }

    override fun onAuthenticationSucceeded(appInfo: AppInfo) {
        Toast.makeText(context, getString(R.string.authentication_succeeded), Toast.LENGTH_SHORT)
            .show()
        appHelper.launchApp(context, appInfo)
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(context, getString(R.string.authentication_failed), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?) {
        Toast.makeText(context, getString(R.string.authentication_error), Toast.LENGTH_SHORT)
            .show()
    }
}