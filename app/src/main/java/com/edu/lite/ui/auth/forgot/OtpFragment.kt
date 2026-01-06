package com.edu.lite.ui.auth.forgot

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.CommonApiResponse
import com.edu.lite.data.model.SignupResponse
import com.edu.lite.databinding.FragmentOtpBinding
import com.edu.lite.ui.auth.AuthCommonVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OtpFragment : BaseFragment<FragmentOtpBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private lateinit var otpETs: Array<AppCompatEditText?>
    private var email: String? = null
    private var type: Int = 0
    private var isOtpComplete = false
    private val args: OtpFragmentArgs by navArgs()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_otp
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // view
        initView()
        // observer
        initObserver()
        // Retrieve the email from the bundle
        args.let { bundle ->
            email = bundle.email
            type = bundle.type
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

                R.id.btnVerify -> {
                    if (validate()) {
                        verifyAccountApi()
                    }
                }

                R.id.tvResendOtp -> {
                    val language = sharedPrefManager.getLanguage()
                    val data = HashMap<String, Any>()
                    data["email"] = email.toString()
                    data["type"] = type
                    data["language"] = language
                    viewModel.resendOtp(Constants.RESEND_OTP, data)
                }
            }

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
                        "codeVerificationApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: SignupResponse? = BindingUtils.parseJson(jsonData)
                                val loginData = model?.user
                                if (loginData != null) {
                                    loginData.let { it1 ->
                                        sharedPrefManager.setLoginData(it1)
                                    }
                                    loginData.token.let {
                                        sharedPrefManager.setToken(it.toString())
                                    }
                                    when (type) {
                                        2 -> {
                                            val action =
                                                OtpFragmentDirections.navigateToNewPasswordFragment(
                                                    email = email.toString()
                                                )
                                            BindingUtils.navigateWithSlide(
                                                findNavController(),
                                                action
                                            )
                                        }

                                        1 -> {
                                            val action =
                                                OtpFragmentDirections.navigateToChooseGradeFragment()
                                            BindingUtils.navigateWithSlide(
                                                findNavController(),
                                                action
                                            )
                                        }
                                    }
                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
                                showErrorToast(e.message.toString())
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }


                        "resendOtp" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: CommonApiResponse? = BindingUtils.parseJson(jsonData)
                                if (model?.message?.isNotEmpty() == true) {
                                    showSuccessToast(model.message.toString())
                                }
                            }.onFailure { e ->
                                showErrorToast(e.message.toString())
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

    /** verifyAccount api call **/
    private fun verifyAccountApi() {
        try {
            val otpData =
                "${binding.otpET1.text}" + "${binding.otpET2.text}" + "${binding.otpET3.text}" + "${binding.otpET4.text}"
            val data = HashMap<String, Any>()
            if (otpData.isNotEmpty()) {
                val language = sharedPrefManager.getLanguage()
                data["email"] = email.toString()
                data["otp"] = otpData.toString()
                data["type"] = type
                data["language"] = language
                viewModel.codeVerificationApi(Constants.USER_VERIFY_OTP, data)
            }

        } catch (e: Exception) {
            Log.e("error", "verifyAccount: $e")
        }
    }

    /*** view ***/
    private fun initView() {
        otpETs = arrayOf(
            binding.otpET1,
            binding.otpET2,
            binding.otpET3,
            binding.otpET4,
        )
        otpETs.forEachIndexed { index, editText ->
            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && index != otpETs.size - 1) {
                        otpETs[index + 1]?.requestFocus()
                    }

                    // Check if all OTP fields are filled
                    isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text?.isEmpty() == true && index != 0) {
                        otpETs[index - 1]?.apply {
                            text?.clear()
                            requestFocus()
                        }
                    }
                }
                // Check if all OTP fields are filled
                isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                false
            }
        }
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val first = binding.otpET1.text.toString().trim()
        val second = binding.otpET2.text.toString().trim()
        val third = binding.otpET3.text.toString().trim()
        val four = binding.otpET4.text.toString().trim()

        if (first.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (second.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (third.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (four.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        }
        return true
    }
}