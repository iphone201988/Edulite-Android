package com.edu.lite.ui.auth.dash_board.profile.edit_profile

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
import com.edu.lite.databinding.FragmentEditProfileBinding
import com.edu.lite.ui.auth.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private lateinit var multipartImage: MultipartBody.Part
    override fun getLayoutResource(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // set data
        val userData = sharedPrefManager.getLoginData()
        userData.let {
            if (it?.name.isNullOrEmpty()) {
                binding.etName.setText("")
            } else {
                binding.etName.setText(it.name)
            }
            if (it?.email.isNullOrEmpty()) {
                binding.etEmail.setText("")
            } else {
                binding.etEmail.setText(it.email)
            }
//            if (it?.name.isNullOrEmpty()) {
//                binding.etDob.setText("")
//            } else {
//                binding.etDob.setText(it.name)
//            }
//            if (it?.name.isNullOrEmpty()) {
//                binding.etPhone.setText("")
//            } else {
//                binding.etPhone.setText(it.name)
//            }

            Glide.with(this).load(it?.profilePicture).placeholder(R.drawable.person_holder)
                .into(binding.ivUSer)
        }

        // observer
        initObserver()
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

                R.id.btnEdit -> {
                    val data = HashMap<String, RequestBody>()
                    data["name"] = binding.etName.text.toString().trim().toRequestBody()
                    data["phone"] = binding.etPhone.text.toString().trim().toRequestBody()
                    data["dob"] = binding.etDob.text.toString().trim().toRequestBody()
                    viewModel.updateProfileApi(Constants.UPDATE_PROFILE, data, multipartImage)
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
                        "updateProfileApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: CommonApiResponse? = BindingUtils.parseJson(jsonData)
                                if (model?.success == true) {
                                    showSuccessToast(model.message.toString())

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


}