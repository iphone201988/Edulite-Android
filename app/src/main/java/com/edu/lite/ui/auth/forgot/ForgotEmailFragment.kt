package com.edu.lite.ui.auth.forgot

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.CommonApiResponse
import com.edu.lite.data.model.SignupResponse
import com.edu.lite.databinding.FragmentForgotEmailBinding
import com.edu.lite.ui.auth.AuthCommonVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotEmailFragment : BaseFragment<FragmentForgotEmailBinding>() {
    private val viewModel: AuthCommonVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_forgot_email
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
                        "forgotEmailApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: CommonApiResponse? = BindingUtils.parseJson(jsonData)
                                if (model?.success==true) {
                                    val action = ForgotEmailFragmentDirections.navigateToOtpFragment(
                                        email = binding.etEmail.text.toString().trim(),
                                        type = 2
                                    )
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
                R.id.btnForgot -> {
                    if (validate()){
                        val email = binding.etEmail.text.toString().trim()
                        val language  = sharedPrefManager.getLanguage()
                        val data = HashMap<String, Any>()
                        data["email"] = email
                        data["language"] = language  //en:english,\ar:Arabic\fr:French
                        viewModel.forgotEmailApi(Constants.FORGOT_EMAIL, data)
                    }
                }

                R.id.ivBackButton -> {
                    findNavController().popBackStack()
                }
            }
        }
    }


    /**
     * forgot email field validation
     */
    private fun validate(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            showInfoToast(getString(R.string.please_enter_email))
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast(getString(R.string.please_enter_a_valid_email))
            return false
        }
        return true
    }

}