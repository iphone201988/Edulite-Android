package com.edu.lite.ui.auth.languages

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.databinding.FragmentLanguageBinding
import com.edu.lite.ui.auth.AuthCommonVM
import com.edu.lite.ui.auth.dash_board.home.play_learn.LetPlayLearnFragmentDirections
import com.edu.lite.utils.BindingUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding>() {

    private val viewModel: AuthCommonVM by viewModels()
    private val args: LanguageFragmentArgs by navArgs()
    var type = 0
    override fun getLayoutResource(): Int {
        return R.layout.fragment_language
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        sharedPrefManager.saveLanguage("en")
        binding.selectType = 1
        // click
        initOnClick()
        // get data bundle
        val title = args.title
        if (title == "Language") {
            binding.ivBackButton.visibility = View.VISIBLE
            binding.tvNext.text = getString(R.string.change)
            type = 1
        } else {
            type = 0
            binding.tvNext.text = getString(R.string.next)
            binding.ivBackButton.visibility = View.GONE
        }
    }

    /**
     * click event handel
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.tvNext -> {
                    if (type == 1) {
                        findNavController().popBackStack()
                    } else {
                        val action = LanguageFragmentDirections.navigateToLoginFragment()
                        BindingUtils.navigateWithSlide(findNavController(), action)

                    }

                }

                R.id.clEnglish -> {
                    sharedPrefManager.saveLanguage("en")
                    binding.selectType = 1
                }

                R.id.clFrench -> {
                    sharedPrefManager.saveLanguage("fr")
                    binding.selectType = 2
                }

                R.id.clArabic -> {
                    sharedPrefManager.saveLanguage("ar")
                    binding.selectType = 3
                }

                R.id.ivBackButton -> {
                    findNavController().popBackStack()
                }
            }
        }
    }
}