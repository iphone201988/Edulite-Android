package com.edu.lite.ui.auth.dash_board.profile.subscription

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.databinding.FragmentSubscriptionBinding
import com.edu.lite.ui.auth.dash_board.profile.ProfileFragmentVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubscriptionFragment : BaseFragment<FragmentSubscriptionBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_subscription
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
    }
    /**
     * click event handel
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> {
                    findNavController().popBackStack()
                }
            }

        }
    }


}