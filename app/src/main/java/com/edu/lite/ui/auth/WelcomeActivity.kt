package com.edu.lite.ui.auth

import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.edu.lite.R
import com.edu.lite.base.BaseActivity
import com.edu.lite.base.BaseViewModel
import com.edu.lite.databinding.ActivityWelcomeBinding
import com.edu.lite.ui.dash_board.profile.download.ProfileDownloadFragment
import com.edu.lite.utils.Status
import com.edu.lite.utils.bottomnavigationview.CurvedModel
import com.edu.lite.utils.event.SingleRequestEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {

    private val viewModel: AuthCommonVM by viewModels()

    companion object {
        val observeActivity = SingleRequestEvent<Boolean>()
    }

    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.authNavigationHost) as NavHostFragment).navController
    }

    override fun getLayoutResource(): Int = R.layout.activity_welcome

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView() {
        setupNavGraph()
        setupBottomNavigation()
        observeDestinationChanges()
        // observer
        initObserver()
    }


    /** api response observer ***/
    private fun initObserver() {
        observeActivity.observe(this@WelcomeActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "checkInternet" -> {
                            runCatching {
                                navController.navigate(R.id.fragmentProfileDownload)

                            }.onFailure { e ->

                            }.also {
                                hideLoading()
                            }
                        }

                    }
                }

                Status.ERROR -> {
                    hideLoading()
                }

                else -> {

                }
            }
        }
    }

    /**
     * NavGraph must be set ONLY ONCE
     */
    private fun setupNavGraph() {
        val graph = navController.navInflater.inflate(R.navigation.auth_navigation)


        graph.setStartDestination(
            if (sharedPrefManager.getLoginData() != null) {
                R.id.fragmentHome
            } else {
                if (sharedPrefManager.getOnBoarding()=="true"){
                    R.id.fragmentLogin
                }
                else{
                    R.id.fragmentSplash
                }

            }
        )

        navController.graph = graph
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.isSaveEnabled = false
        val menuItems = arrayOf(
            CurvedModel(
                R.drawable.un_selected_home, R.drawable.un_selected_home, R.id.fragmentHome
            ), CurvedModel(
                R.drawable.un_selected_road, R.drawable.un_selected_road, R.id.fragmentRoadmap
            ),/* CurvedModel(
                R.drawable.un_selected_download,
                R.drawable.un_selected_download,
                R.id.fragmentDownload,
                getString(R.string.english)
            ),*/
            CurvedModel(
                R.drawable.un_selected_quiz, R.drawable.un_selected_quiz, R.id.fragmentQuiz
            ), CurvedModel(
                R.drawable.un_selected_profile, R.drawable.un_selected_profile, R.id.fragmentProfile
            )
        )

        binding.bottomNavigation.setMenuItems(menuItems, 0)
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun observeDestinationChanges() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.visibility = when (destination.id) {
                R.id.fragmentHome, R.id.fragmentRoadmap,
                    //     R.id.fragmentDownload,
                R.id.fragmentQuiz, R.id.fragmentProfile -> View.VISIBLE

                else -> View.GONE
            }
        }
    }
}
