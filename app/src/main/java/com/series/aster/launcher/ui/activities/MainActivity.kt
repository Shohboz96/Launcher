package com.series.aster.launcher.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.series.aster.launcher.R
import com.series.aster.launcher.databinding.ActivityMainBinding
import com.series.aster.launcher.helper.AppHelper
import com.series.aster.launcher.helper.PreferenceHelper
import com.series.aster.launcher.ui.drawer.DrawFragment
import com.series.aster.launcher.ui.viewpager.ViewPagerAdapter
import com.series.aster.launcher.viewmodel.AppViewModel
import com.series.aster.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: AppViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    private val viewPagerAdapter: ViewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager, lifecycle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeDependencies()
        setupNavController()
        setupViewPagerAdapter()
    }

    private fun initializeDependencies() {

        preferenceViewModel.setShowStatusBar(preferenceHelper.showStatusBar)
        preferenceViewModel.setFirstLaunch(preferenceHelper.firstLaunch)

        window.addFlags(FLAG_LAYOUT_NO_LIMITS)

    }

    private fun setupDataBase() {
        lifecycleScope.launch {
            viewModel.initializeInstalledAppInfo(this@MainActivity)
        }
        //GlobalScope.launch {  }
        preferenceHelper.firstLaunch = false
    }

    private fun observeUI() {
        preferenceViewModel.setShowStatusBar(preferenceHelper.showStatusBar)
        preferenceViewModel.showStatusBarLiveData.observe(this) {
            if (it) appHelper.showStatusBar(this.window)
            else appHelper.hideStatusBar(this.window) }
    }

    private fun setupNavController() {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupViewPagerAdapter() {
        binding.pager.apply {
            adapter = viewPagerAdapter
            offscreenPageLimit = 1
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        setupDataBase()
        observeUI()
    }

    override fun onStop() {
        backToHomeScreen()
        super.onStop()
    }

    override fun onUserLeaveHint() {
        backToHomeScreen()
        super.onUserLeaveHint()
    }

    override fun onBackPressed() {
        val currentItem = binding.pager.currentItem
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]

        if (currentFragment is DrawFragment) {
            super.onBackPressed()
        } else {
            binding.pager.currentItem = currentItem - 1
        }
    }

    private fun backToHomeScreen() {
        if (navController.currentDestination?.id != R.id.HomeFragment)
            navController.popBackStack(R.id.HomeFragment, false)
    }

}