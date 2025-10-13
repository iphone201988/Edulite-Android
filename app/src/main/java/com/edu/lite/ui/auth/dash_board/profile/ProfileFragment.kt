package com.edu.lite.ui.auth.dash_board.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.CommonApiResponse
import com.edu.lite.data.model.ProfileModel
import com.edu.lite.databinding.DialogDeleteLogoutBinding
import com.edu.lite.databinding.FragmentProfileBinding
import com.edu.lite.databinding.RvProfileItemBinding
import com.edu.lite.ui.auth.forgot.NewPasswordFragmentDirections
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private lateinit var profileAdapter: SimpleRecyclerViewAdapter<ProfileModel, RvProfileItemBinding>
    private var apiType = 1
    private var logoutDeleteDialog: BaseCustomDialog<DialogDeleteLogoutBinding>? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initOnProfileAdapter()
        // dialog
        initDialog()
        // observer
        initObserver()
        // set data
        val userData = sharedPrefManager.getLoginData()
        userData.let {
            if (it?.name.isNullOrEmpty()){
                binding.tvName.text = ""
            }else{
                binding.tvName.text = it.name
            }
            Glide.with(this).load(it?.profilePicture).placeholder(R.drawable.person_holder)
                .into(binding.ivUSer)
        }
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
                                if (model?.success==true) {
                                    showSuccessToast(model.message.toString())
                                    sharedPrefManager.clear()
                                    findNavController().popBackStack(R.id.auth_navigation, true)

                                    val action = ProfileFragmentDirections.navigateToLoginFragment()
                                    BindingUtils.navigateWithSlide(findNavController(), action)

                                } else {
                                    showErrorToast("Something went wrong")
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
                                if (model?.success==true) {
                                    showSuccessToast(model.message.toString())
                                    sharedPrefManager.clear()
                                    findNavController().popBackStack(R.id.auth_navigation, true)

                                    val action = ProfileFragmentDirections.navigateToLoginFragment()
                                    BindingUtils.navigateWithSlide(findNavController(), action)

                                } else {
                                    showErrorToast("Something went wrong")
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


            }

        }
    }


    /**
     * user profile adapter
     */
    private fun initOnProfileAdapter() {
        profileAdapter = SimpleRecyclerViewAdapter(R.layout.rv_profile_item, BR.bean) { v, m, pos ->
            when (m.title) {
                "Edit Profile" -> {
                    val action = ProfileFragmentDirections.navigateToEditProfileFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

                "Notification" -> {
                    val action = ProfileFragmentDirections.navigateToNotificationFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

                "Language" -> {

                    val action = ProfileFragmentDirections.navigateToLanguageFragment(title = "Language")
                    BindingUtils.navigateWithSlide(findNavController(), action)
                }

                "Downloads" -> {
                    val action = ProfileFragmentDirections.navigateToProfileDownloadFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

                "Subscription" -> {
                    val action = ProfileFragmentDirections.navigateToSubscriptionFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

                "Change Password" -> {
                    val action = ProfileFragmentDirections.navigateToChangePasswordFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

                "Logout" -> {
                    apiType = 2
                    showDialogItem(1)
                }

                "Delete Profile" -> {
                    apiType = 1
                    showDialogItem(2)
                }
            }
        }

        binding.rvProfile.adapter = profileAdapter
        profileAdapter.list = DummyList.profileModelList()

    }


    /** dialog **/
    private fun initDialog() {
        logoutDeleteDialog = BaseCustomDialog(requireContext(), R.layout.dialog_delete_logout) {
            when (it?.id) {
                R.id.tvCancel -> {
                    logoutDeleteDialog?.dismiss()
                }

                R.id.tvLogout -> {
                    if (apiType == 2) {
                        val data = HashMap<String, Any>()
                        viewModel.logoutApi(Constants.LOGOUT, data)
                    } else if (apiType == 1) {
                        val data = HashMap<String, Any>()
                        viewModel.deleteApi(Constants.DELETE, data)
                    }
                    logoutDeleteDialog?.dismiss()

                }
            }

        }
        logoutDeleteDialog?.setCancelable(false)
    }


    /**dialog text change for logout and delete**/
    private fun showDialogItem(type: Int) {
        if (type == 2) {
            logoutDeleteDialog?.binding?.apply {
                tvTitle.text = getString(R.string.delete_account)
                tvSubHeading.text = getString(R.string.are_you_sure_to_delete_account)
                tvLogout.text = getString(R.string.delete)
            }

        } else {
            logoutDeleteDialog?.binding?.apply {
                tvTitle.text = getString(R.string.logout)
                tvSubHeading.text = getString(R.string.are_you_sure_to_logout)
                tvLogout.text = getString(R.string.logout)
            }

        }
        logoutDeleteDialog?.show()

    }


}