package com.edu.lite.ui.auth.signup

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.autofill.UserData
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.CountryModel
import com.edu.lite.data.model.SignupResponse
import com.edu.lite.databinding.DialogFilterBinding
import com.edu.lite.databinding.FragmentSignupBinding
import com.edu.lite.databinding.ItemCountryBinding
import com.edu.lite.ui.auth.AuthCommonVM
import com.edu.lite.ui.auth.forgot.OtpFragmentDirections
import com.edu.lite.ui.auth.login.LoginFragmentDirections
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.jvm.java


@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private var token = "123456"
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var countryList = mutableListOf<CountryModel>()
    private lateinit var countryAdapter: SimpleRecyclerViewAdapter<CountryModel, ItemCountryBinding>
    private lateinit var showFilterDialog: BaseCustomDialog<DialogFilterBinding>
    var countryCode: String = "+91"
    override fun getLayoutResource(): Int {
        return R.layout.fragment_signup
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // text color change
        initOnTextColorChange()
        // observer
        initObserver()
        // country code picker
        setCountryCodePicker()
        loadSvgImage(Constants.DEFAULT_FLAG_LINK)
        // get token firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            token = it.result
            Log.d("gfdffd", "onCreateView: $token")
        }
    }


    /**
     * click event handel
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.btnSignup -> {
                    if (validate()) {
                        val email = binding.etEmail.text.toString().trim()
                        val password = binding.etPassword.text.toString().trim()
                        val dob = binding.etDob.text.toString().trim()
                        val phoneNumber = binding.etPhone.text.toString().trim()
                        val fullName = binding.etName.text.toString().trim()
                        val language  = sharedPrefManager.getLanguage()
                        val data = HashMap<String, Any>()
                        data["name"] = fullName
                        data["email"] = email
                        data["password"] = password
                        data["phone"] = phoneNumber
                        data["dob"] = dob
                        data["countryCode"] = countryCode
                        data["deviceToken"] = token
                        data["language"] = language  //en:english,\ar:Arabic\fr:French
                        data["deviceType"] = "2"   // IOS: 1,ANDROID: 2, WEB: 3
                        data["role"] = "1"
                         viewModel.createAccount(Constants.REGISTER, data)
                    }
                }

                R.id.ivHidePassword -> {
                    if (binding.etPassword.text.toString().trim().isNotEmpty()) {
                        showOrHidePassword()
                    }

                }

                R.id.tvPhone -> {
                    counterPickerDialog()
                }

                R.id.etDob -> {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePickerDialog = DatePickerDialog(
                        requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                            calendar.set(selectedYear, selectedMonth, selectedDay)
                            val formattedDate = dateFormatter.format(calendar.time)
                            binding.etDob.setText(formattedDate)
                        }, year, month, day
                    )

                    val maxDateCal = Calendar.getInstance()
                    maxDateCal.add(Calendar.YEAR, -18) // must be 18 years old
                    datePickerDialog.datePicker.maxDate = maxDateCal.timeInMillis

                    val minDateCal = Calendar.getInstance()
                    minDateCal.add(Calendar.YEAR, -100) // optional: no older than 100 years ago
                    datePickerDialog.datePicker.minDate = minDateCal.timeInMillis

                    datePickerDialog.show()
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
                        "createAccount" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: SignupResponse? = BindingUtils.parseJson(jsonData)
                                val loginData = model?.user
                                if (loginData!=null) {
                                    loginData.let { it1 ->
                                        sharedPrefManager.setLoginData(it1)
                                    }
                                    loginData.token.let {
                                        sharedPrefManager.setToken(it.toString())
                                    }
                                    val action = SignupFragmentDirections.navigateToOtpFragment(
                                        email = binding.etEmail.text.toString(),
                                        type = 1
                                    )
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
     * counter picker dialog
     */
    private fun counterPickerDialog() {
        showFilterDialog = BaseCustomDialog(requireContext(), R.layout.dialog_filter) {

        }
        // adapter
        getCountryAdapter()
        // Search listener
        showFilterDialog.binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterCountries(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        showFilterDialog.show()
        showFilterDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    /**
     * get country adapter
     */
    private fun getCountryAdapter() {
        countryAdapter = SimpleRecyclerViewAdapter(R.layout.item_country, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.llCountry -> {
                    loadSvgImage(m.image)
                    countryCode = m.countryCode.replace("[^\\d+]".toRegex(), "")
                    showFilterDialog.dismiss()

                }
            }
            for (i in countryAdapter.list.indices) {
                countryList[i].isSelected = i == pos
            }
            countryAdapter.list = countryList
            countryAdapter.notifyDataSetChanged()
        }
        showFilterDialog.binding.rvCountry.adapter = countryAdapter

        countryAdapter.list = countryList

    }

    /**
     * filter countries
     */
    private fun filterCountries(query: String) {
        val filteredList = if (query.isEmpty()) {
            countryList // show all
        } else {
            countryList.filter {
                it.name.contains(query, ignoreCase = true) || it.countryCode.contains(
                    query,
                    ignoreCase = true
                )
            }
        }
        countryAdapter.list = filteredList.toMutableList()
        countryAdapter.notifyDataSetChanged()
    }

    /**
     * set country code picker
     */
    private fun setCountryCodePicker() {
        val inputStream = resources.openRawResource(R.raw.country_country_code)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val gson = Gson()
        countryList = mutableListOf()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val country = gson.fromJson(jsonObject.toString(), CountryModel::class.java)
            countryList.add(country)
        }
    }


    /**
     * signup field validation
     */
    private fun validate(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val dob = binding.etDob.text.toString().trim()
        val phoneNumber = binding.etPhone.text.toString().trim()
        val fullName = binding.etName.text.toString().trim()

        if (fullName.isEmpty()) {
            showInfoToast(getString(R.string.please_enter_name))
            return false
        }
        if (email.isEmpty()) {
            showInfoToast(getString(R.string.please_enter_email))
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast(getString(R.string.please_enter_a_valid_email))
            return false
        } else if (dob.isEmpty()) {
            showInfoToast(getString(R.string.please_enter_date_of_birth))
            return false
        } else if (phoneNumber.isEmpty()) {
            showInfoToast(getString(R.string.please_enter_phone_number))
            return false
        } else if (password.isEmpty()) {
            showInfoToast(getString(R.string.please_enter_password))
            return false
        } else if (password.length < 6) {
            showInfoToast(getString(R.string.password_must_be_at_least_6_characters))
            return false
        } else if (!password.any { it.isUpperCase() }) {
            showInfoToast(getString(R.string.password_must_contain_at_least_one_uppercase_letter))
            return false
        }

        return true
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
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }


        // Reapply the saved typeface to maintain the font style
        binding.etPassword.typeface = typeface
        binding.etPassword.setSelection(binding.etPassword.length())
    }


    /**
     * text color change
     */
    fun initOnTextColorChange() {
        val text = getString(R.string.already_have_an_account_login)
        val spannable = SpannableString(text)

        val startIndex = text.indexOf(getString(R.string.login))
        val endIndex = startIndex + getString(R.string.login).length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val action = SignupFragmentDirections.navigateToLoginFragment()
                BindingUtils.navigateWithSlide(findNavController(), action)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(), R.color.start_color)
                ds.isUnderlineText = false
            }
        }

        spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans_bold)
        typeface?.let {
            spannable.setSpan(
                TypefaceSpan(it), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvSignup.text = spannable
        binding.tvSignup.movementMethod = LinkMovementMethod.getInstance()
        binding.tvSignup.highlightColor = Color.TRANSPARENT
    }

    /**
     * load svg image
     */
    private fun loadSvgImage(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val svgString = response.body?.string()
                    svgString?.let {
                        val svg = SVG.getFromString(it)
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            try {
                                val drawable = PictureDrawable(svg.renderToPicture())
                                binding.ivCountryIcon.setImageDrawable(drawable)
                            } catch (e: SVGParseException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        })
    }
}
