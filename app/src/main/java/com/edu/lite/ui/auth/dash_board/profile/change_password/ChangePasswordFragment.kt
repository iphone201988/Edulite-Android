package com.edu.lite.ui.auth.dash_board.profile.change_password

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
import com.edu.lite.data.model.GradeModelResponse
import com.edu.lite.databinding.FragmentChangePasswordBinding
import com.edu.lite.ui.auth.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_change_password
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
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
                        "changePassword" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: CommonApiResponse? = BindingUtils.parseJson(jsonData)
                                if (model?.success==true) {
                                    showSuccessToast(model.message.toString())
                                    findNavController().popBackStack()
                                } else {
                                    showErrorToast("Something went wrong")
                                }
                            }.onFailure { e ->
                                Log.e("changePassword", "Error: ${e.message}", e)
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

                // show or hide old password click
                R.id.ivHideOldPassword -> {
                    if (binding.etOldPassword.text.toString().trim().isNotEmpty()) {
                        showOrHideOldPassword()
                    }
                }
                // show or hide new password click
                R.id.ivHideNewPassword -> {
                    if (binding.etNewPassword.text.toString().trim().isNotEmpty()) {
                        showOrHideNewPassword()
                    }
                }
                // show or hide confirm password click
                R.id.ivHideRePassword -> {
                    if (binding.etReNewPassword.text.toString().trim().isNotEmpty()) {
                        showOrHideConfirmPassword()
                    }

                }

                R.id.btnChange -> {
                    if (validate()) {
                        val data = HashMap<String, Any>()
                        data["oldPassword"] = binding.etOldPassword.text.toString().trim()
                        data["newPassword"] = binding.etNewPassword.text.toString().trim()
                        viewModel.changePassword(Constants.CHANGE_PASSWORD, data)
                    }
                }
            }

        }
    }


    /*** show or old hide password **/
    private fun showOrHideOldPassword() {
        // Save the current typeface
        val typeface = binding.etOldPassword.typeface
        if (binding.etOldPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            binding.ivHideOldPassword.setImageResource(R.drawable.show_password)
            binding.etOldPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.ivHideOldPassword.setImageResource(R.drawable.hide_password)
            binding.etOldPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }


        // Reapply the saved typeface to maintain the font style
        binding.etOldPassword.typeface = typeface
        binding.etOldPassword.setSelection(binding.etOldPassword.length())
    }


    /*** show or hide new password **/
    private fun showOrHideNewPassword() {
        // Save the current typeface
        val typeface = binding.etNewPassword.typeface
        if (binding.etNewPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            binding.ivHideNewPassword.setImageResource(R.drawable.show_password)
            binding.etNewPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.ivHideNewPassword.setImageResource(R.drawable.hide_password)
            binding.etNewPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
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
            binding.etReNewPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }


        // Reapply the saved typeface to maintain the font style
        binding.etReNewPassword.typeface = typeface
        binding.etReNewPassword.setSelection(binding.etReNewPassword.length())
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val oldPassword = binding.etOldPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etReNewPassword.text.toString().trim()
        if (oldPassword.isEmpty()) {
            showInfoToast("Please enter password")
            return false
        } else if (oldPassword.length < 6) {
            showInfoToast("Password must be at least 6 characters")
            return false
        } else if (!oldPassword.any { it.isUpperCase() }) {
            showInfoToast("Password must contain at least one uppercase letter")
            return false
        }
        if (newPassword.isEmpty()) {
            showInfoToast("Please enter password")
            return false
        } else if (newPassword.length < 6) {
            showInfoToast("Password must be at least 6 characters")
            return false
        } else if (!newPassword.any { it.isUpperCase() }) {
            showInfoToast("Password must contain at least one uppercase letter")
            return false
        } else if (confirmPassword.isEmpty()) {
            showInfoToast("Please enter confirm password")
            return false
        } else if (newPassword != confirmPassword) {
            showInfoToast("Password and Confirm password do not match")
            return false
        }

        return true
    }

}