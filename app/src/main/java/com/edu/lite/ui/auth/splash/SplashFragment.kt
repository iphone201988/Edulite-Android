package com.edu.lite.ui.auth.splash

import android.graphics.Color
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.databinding.FragmentSplashBinding
import com.edu.lite.ui.auth.AuthCommonVM
import com.edu.lite.ui.auth.dash_board.home.HomeFragmentDirections
import com.edu.lite.utils.BindingUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_splash
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // status bar color change
        BindingUtils.setStatusBarGradient(requireActivity())
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        // click
        initOnClick()

    }

    /**
     * click event handel
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.tvStartLearning -> {
                    val action = SplashFragmentDirections.navigateToLanguageFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)
                }
            }
        }
    }


}