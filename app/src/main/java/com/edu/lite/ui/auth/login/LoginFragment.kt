package com.edu.lite.ui.auth.login

import android.graphics.Color
import android.os.Build
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.SignupResponse
import com.edu.lite.databinding.FragmentLoginBinding
import com.edu.lite.ui.auth.AuthCommonVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.P)
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private var token = "123456"
    override fun getLayoutResource(): Int {
        return R.layout.fragment_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // observer
        initObserver()
        // text color change
        initOnTextColorChange()
        // get token firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            token = it.result
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
                        "loginApi" -> {
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

                                    if (loginData.isEmailVerified == false) {
                                        val action = LoginFragmentDirections.navigateToOtpFragment(
                                            email = loginData.email.toString(), type = 1
                                        )
                                        BindingUtils.navigateWithSlide(findNavController(), action)
                                    } else {
                                        findNavController().popBackStack(R.id.auth_navigation, true)
                                        BindingUtils.navigateWithSlide(
                                            findNavController(),
                                            LoginFragmentDirections.navigateToHomeFragment()
                                        )
                                    }

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
                R.id.tvForgot -> {
                    val action = LoginFragmentDirections.navigateToForgotFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }

                R.id.btnLogin -> {
                    if (validate()) {
                        val email = binding.etEmail.text.toString().trim()
                        val password = binding.etPassword.text.toString().trim()
                        val language = sharedPrefManager.getLanguage()
                        val data = HashMap<String, Any>()
                        data["email"] = email
                        data["password"] = password
                        data["deviceToken"] = token
                        data["language"] = language  //en:english,\ar:Arabic\fr:French
                        data["deviceType"] = "2"   // IOS: 1,ANDROID: 2, WEB: 3
                        viewModel.loginApi(Constants.LOGIN, data)
                    }
                }

                R.id.ivHidePassword -> {
                    if (binding.etPassword.text.toString().trim().isNotEmpty()) {
                        showOrHidePassword()
                    }

                }


            }
        }
    }


    /**
     * login field validation
     */
    private fun validate(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            showInfoToast(getString(R.string.please_enter_email))
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast(getString(R.string.please_enter_a_valid_email))
            return false
        } else if (password.isEmpty()) {
            showInfoToast(getString(R.string.please_enter_password))
            return false
        }
        return true
    }

    /**
     * text color change
     */
    fun initOnTextColorChange() {
        val fullText = getString(R.string.don_t_have_an_account_sign_up)
        val spannable = SpannableString(fullText)

        val signUpText = getString(R.string.sign_up)
        val startIndex = fullText.indexOf(signUpText)

        if (startIndex == -1) {
            // Text not found → avoid crash
            binding.tvSignup.text = fullText
            return
        }

        val endIndex = startIndex + signUpText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val action = LoginFragmentDirections.navigateToSignupFragment()
                BindingUtils.navigateWithSlide(findNavController(), action)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(requireActivity(), R.color.start_color)
                ds.isUnderlineText = false
            }
        }

        spannable.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ResourcesCompat.getFont(requireContext(), R.font.open_sans_bold)?.let {
            spannable.setSpan(
                TypefaceSpan(it),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvSignup.text = spannable
        binding.tvSignup.movementMethod = LinkMovementMethod.getInstance()
        binding.tvSignup.highlightColor = Color.TRANSPARENT
    }



    /*** show or confirm hide password **/
    private fun showOrHidePassword() {
        // Save the current typeface
        val typeface = binding.etPassword.typeface
        if (binding.etPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            binding.ivHidePassword.setImageResource(R.drawable.show_password)
            binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.ivHidePassword.setImageResource(R.drawable.hide_password)
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }


        // Reapply the saved typeface to maintain the font style
        binding.etPassword.typeface = typeface
        binding.etPassword.setSelection(binding.etPassword.length())
    }

}