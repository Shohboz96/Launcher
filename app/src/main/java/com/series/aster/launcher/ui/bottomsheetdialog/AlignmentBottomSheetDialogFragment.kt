package com.series.aster.launcher.ui.bottomsheetdialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.series.aster.launcher.R
import com.series.aster.launcher.databinding.BottomsheetdialogAlignmentSettingsBinding
import com.series.aster.launcher.databinding.BottomsheetdialogColorSettingsBinding
import com.series.aster.launcher.helper.AppHelper
import com.series.aster.launcher.helper.BottomDialogHelper
import com.series.aster.launcher.helper.PreferenceHelper
import com.series.aster.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlignmentBottomSheetDialogFragment(context: Context) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetdialogAlignmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var bottomDialogHelper: BottomDialogHelper

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    private var selectedAlignment: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetdialogAlignmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeClickListener()
    }

    private fun initView() {
        bottomDialogHelper.setupDialogStyle(dialog)

        binding.selectDateTextSize.apply {
            text = appHelper.gravityToString(preferenceHelper.homeDateAlignment)
        }

        binding.selectTimeTextSize.apply {
            text = appHelper.gravityToString(preferenceHelper.homeTimeAlignment)
        }

        binding.selectAppTextSize.apply {
            text = appHelper.gravityToString(preferenceHelper.homeAppAlignment)
        }
    }

    private fun observeClickListener(){
        binding.bottomAlignmentDateView.setOnClickListener {
            selectedAlignment = REQUEST_KEY_DATE_ALIGNMENT
            showListDialog(selectedAlignment)
        }

        binding.bottomAlignmentTimeView.setOnClickListener {
            selectedAlignment = REQUEST_KEY_TIME_ALIGNMENT
            showListDialog(selectedAlignment)
        }

        binding.bottomAlignmentAppView.setOnClickListener {
            selectedAlignment = REQUEST_KEY_APP_ALIGNMENT
            showListDialog(selectedAlignment)
        }
    }


    private fun showListDialog(selectedAlignment: String) {
        val items = resources.getStringArray(R.array.alignment_options)

        val dialog = MaterialAlertDialogBuilder(requireContext())

        dialog.setTitle(DIALOG_TITLE)
        dialog.setItems(items) { _, which ->
            val selectedItem = items[which]
            val gravity = appHelper.getGravityFromSelectedItem(selectedItem)

            when (selectedAlignment) {
                REQUEST_KEY_APP_ALIGNMENT -> {
                    setAlignment(
                        selectedAlignment,
                        selectedItem,
                        gravity,
                        binding.selectAppTextSize
                    )
                }

                REQUEST_KEY_TIME_ALIGNMENT -> {
                    setAlignment(
                        selectedAlignment,
                        selectedItem,
                        gravity,
                        binding.selectTimeTextSize
                    )
                }

                REQUEST_KEY_DATE_ALIGNMENT -> {
                    setAlignment(
                        selectedAlignment,
                        selectedItem,
                        gravity,
                        binding.selectDateTextSize
                    )
                }
            }
        }
        dialog.show()
    }

    private fun setAlignment(
        alignmentType: String,
        selectedItem: String,
        gravity: Int,
        textView: TextView
    ) {
        val alignmentPreference: (Int) -> Unit
        val alignmentGetter: () -> Int

        when (alignmentType) {
            REQUEST_KEY_APP_ALIGNMENT -> {
                alignmentPreference = { preferenceViewModel.setHomeAppAlignment(it) }
                alignmentGetter = { preferenceHelper.homeAppAlignment }
            }

            REQUEST_KEY_TIME_ALIGNMENT -> {
                alignmentPreference = { preferenceViewModel.setHomeTimeAppAlignment(it) }
                alignmentGetter = { preferenceHelper.homeTimeAlignment }
            }

            REQUEST_KEY_DATE_ALIGNMENT -> {
                alignmentPreference = { preferenceViewModel.setHomeDateAlignment(it) }
                alignmentGetter = { preferenceHelper.homeDateAlignment }
            }

            else -> return
        }

        alignmentPreference(gravity)
        textView.text = appHelper.gravityToString(alignmentGetter())
    }

    companion object {
        private const val DIALOG_TITLE = "Select Alignment"
        private const val REQUEST_KEY_DATE_ALIGNMENT = "REQUEST_DATE_Alignment"
        private const val REQUEST_KEY_TIME_ALIGNMENT = "REQUEST_TIME_Alignment"
        private const val REQUEST_KEY_APP_ALIGNMENT = "REQUEST_APP_Alignment"
    }
}