package com.edu.lite.ui.auth.forgot

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.CommonApiResponse
import com.edu.lite.databinding.FragmentNewPasswordBinding
import com.edu.lite.ui.auth.AuthCommonVM
import com.edu.lite.ui.auth.languages.LanguageFragmentDirections
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewPasswordFragment : BaseFragment<FragmentNewPasswordBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private var email: String? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_new_password
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // Retrieve the email from the bundle
        arguments?.let { bundle ->
            email = bundle.getString("email")
        }

        // observer
        initObserver()
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
                        "resetPassword" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: CommonApiResponse? = BindingUtils.parseJson(jsonData)
                                if (model?.success==true) {
                                    showSuccessToast(model.message.toString())
                                    findNavController().popBackStack(R.id.auth_navigation, true)
                                    val action = NewPasswordFragmentDirections.navigateToLoginFragment()
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
                R.id.ivBackButton -> {
                    findNavController().popBackStack()
                }
                R.id.btnCreate -> {
                    if (validate()){
                        val language  = sharedPrefManager.getLanguage()
                        val data = HashMap<String, Any>()
                        data["email"] = email.toString()
                        data["newPassword"] = binding.etNewPassword.text.toString().trim()
                        data["language"] =language
                        viewModel.resetPassword(Constants.RESET_PASSWORD, data)
                    }

                }

                // show or hide password click
                R.id.ivHideNewPassword -> {
                    if (binding.etNewPassword.text.toString().trim().isNotEmpty()) {
                        showOrHidePassword()
                    }
                }
                // show or hide confirm password click
                R.id.ivHideRePassword -> {
                    if (binding.etReNewPassword.text.toString().trim().isNotEmpty()) {
                        showOrHideConfirmPassword()
                    }

                }

            }

        }
    }


    /*** show or confirm hide password **/
    private fun showOrHidePassword() {
        // Save the current typeface
        val typeface = binding.etNewPassword.typeface
        if (binding.etNewPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            binding.ivHideNewPassword.setImageResource(R.drawable.show_password)
            binding.etNewPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.ivHideNewPassword.setImageResource(R.drawable.hide_password)
            binding.etNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }


        // Reapply the saved typeface to maintain the font style
        binding.etNewPassword.typeface = typeface
        binding.etNewPassword.setSelection(binding.etNewPassword.length())
    }


    /*** show or hide confirm password **/
    private fun showOrHideConfirmPassword() {
        // Save the current typeface
        val typeface = binding.etReNewPassword.typeface
        if (binding.etReNewPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            binding.ivHideRePassword.setImageResource(R.drawable.show_password)
            binding.etReNewPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.ivHideRePassword.setImageResource(R.drawable.hide_password)
            binding.etReNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }


        // Reapply the saved typeface to maintain the font style
        binding.etReNewPassword.typeface = typeface
        binding.etReNewPassword.setSelection(binding.etReNewPassword.length())
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val password = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etReNewPassword.text.toString().trim()
        if (password.isEmpty()) {
            showInfoToast("Please enter password")
            return false
        } else if (password.length < 6) {
            showInfoToast("Password must be at least 6 characters")
            return false
        } else if (!password.any { it.isUpperCase() }) {
            showInfoToast("Password must contain at least one uppercase letter")
            return false
        } else if (confirmPassword.isEmpty()) {
            showInfoToast("Please enter confirm password")
            return false
        } else if (password != confirmPassword) {
            showInfoToast("Password and Confirm password do not match")
            return false
        }

        return true
    }

}