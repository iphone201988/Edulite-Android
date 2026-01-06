package com.edu.lite.ui.dash_board.profile

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.CommonApiResponse
import com.edu.lite.databinding.FragmentProfileBinding
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // observer
        initObserver()
        // set data
        val userData = sharedPrefManager.getLoginData()
        userData.let {
            Glide.with(this).load(Constants.BASE_URL_IMAGE+it?.profilePicture).placeholder(R.drawable.person_holder)
                .into(binding.ivUSer)
        }

        // det progress
        BindingUtils.setProgress(binding.progressionGuideline, 20)
    }


    /** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "logoutApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: CommonApiResponse? = BindingUtils.parseJson(jsonData)
                                if (model?.success == true) {
                                    showSuccessToast(model.message.toString())
                                    sharedPrefManager.clearAllExceptLanguage()
                                    findNavController().popBackStack(R.id.auth_navigation, true)

                                    val action = ProfileFragmentDirections.navigateToLoginFragment()
                                    BindingUtils.navigateWithSlide(findNavController(), action)

                                } else {
                                      showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }

                        "deleteApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: CommonApiResponse? = BindingUtils.parseJson(jsonData)
                                if (model?.success == true) {
                                    showSuccessToast(model.message.toString())
                                    sharedPrefManager.clearAllExceptLanguage()
                                    findNavController().popBackStack(R.id.auth_navigation, true)

                                    val action = ProfileFragmentDirections.navigateToLoginFragment()
                                    BindingUtils.navigateWithSlide(findNavController(), action)

                                } else {
                                      showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }


    /**
     * click event handel
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivSettings -> {
                    val action = ProfileFragmentDirections.navigateToSettingsFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

                R.id.clBadges1 -> {
                    val action = ProfileFragmentDirections.navigateToBadgesFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

                R.id.clRewards -> {
                    val action = ProfileFragmentDirections.navigateToRewardsFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }
            }

        }
    }


}