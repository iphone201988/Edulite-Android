package com.edu.lite.ui.auth

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.edu.lite.R
import com.edu.lite.base.BaseActivity
import com.edu.lite.base.BaseViewModel
import com.edu.lite.databinding.ActivityWelcomeBinding
import com.edu.lite.utils.bottomnavigationview.CurvedModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    private val viewModel: AuthCommonVM by viewModels()

    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.authNavigationHost) as NavHostFragment).navController
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_welcome
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.auth_navigation).apply {
                        if (sharedPrefManager.getLoginData()!=null){
                            setStartDestination(R.id.fragmentHome)
                        }else{
                            setStartDestination(R.id.fragmentSplash)
                        }


                    }
            }
        }


        val menuItems = arrayOf(
            CurvedModel(
                R.drawable.un_selected_home,
                R.drawable.un_selected_home,
                R.id.fragmentHome,
                getString(R.string.english)
            ),
            CurvedModel(
                R.drawable.un_selected_download,
                R.drawable.un_selected_download,
                R.id.fragmentDownload,
                getString(R.string.english)
            ),
            CurvedModel(
                R.drawable.un_selected_quiz,
                R.drawable.un_selected_quiz,
                R.id.fragmentQuiz,
                getString(R.string.english)
            ),
            CurvedModel(
                R.drawable.un_selected_profile,
                R.drawable.un_selected_profile,
                R.id.fragmentProfile,
                getString(R.string.english)
            ),

            )
        binding.bottomNavigation.setMenuItems(menuItems, 0)

        binding.bottomNavigation.setupWithNavController(navController)



        // add bottom sheet backstack handel
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentHome -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                  //  binding.bottomNavigation.menu.findItem(R.id.fragmentFriends).isChecked = true
                }

                R.id.fragmentQuiz -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                  //  binding..menu.findItem(R.id.chatFragment).isChecked = true
                }

                R.id.fragmentDownload -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                  //  binding.bottomNavigation.menu.findItem(R.id.settingsFragment).isChecked = true
                }

                R.id.fragmentProfile -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                 //   binding.bottomNavigation.menu.findItem(R.id.settingsFragment).isChecked = true
                }

                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }

    }
}