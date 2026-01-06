package com.edu.lite.ui.dash_board.home.creative_project.add

import android.view.View
import androidx.fragment.app.viewModels
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.databinding.FragmentAddCreativeProjectBinding
import com.edu.lite.ui.dash_board.home.HomeFragmentVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCreativeProjectFragment : BaseFragment<FragmentAddCreativeProjectBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_add_creative_project
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

    }

}