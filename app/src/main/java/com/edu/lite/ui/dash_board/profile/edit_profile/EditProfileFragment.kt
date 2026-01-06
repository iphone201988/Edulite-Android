package com.edu.lite.ui.dash_board.profile.edit_profile

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.SignupResponse
import com.edu.lite.data.model.UploadProfileAPi
import com.edu.lite.databinding.FragmentEditProfileBinding
import com.edu.lite.databinding.VideoImagePickerDialogBoxBinding
import com.edu.lite.ui.dash_board.profile.ProfileFragmentVM
import com.edu.lite.utils.AppUtils
import com.edu.lite.utils.BaseCustomDialog
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartPart: MultipartBody.Part? = null
    private var profileImage: String? = null
    private var dob: String? = null
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
            if (it?.name.isNullOrEmpty()) {
                binding.etDob.setText("")
            } else {
                binding.etDob.setText(it.dob?.toDisplayDob().orEmpty())
            }
            if (it?.name.isNullOrEmpty()) {
                binding.etPhone.setText("")
            } else {
                binding.etPhone.setText(it.phone)
            }


            Glide.with(this).load(Constants.BASE_URL_IMAGE+it?.profilePicture).placeholder(R.drawable.person_holder)
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
                    val data = HashMap<String, Any>()
                    data["name"] = binding.etName.text.toString().trim()
                    if (!dob.isNullOrEmpty()) {
                        data["dob"] = binding.etDob.text.toString().trim()
                    }

                    if (!profileImage.isNullOrEmpty()) {
                        data["profilePicture"] = profileImage!!
                    }
                    viewModel.updateProfileApi(Constants.UPDATE_PROFILE, data)
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
                            dob = formattedDate
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

                R.id.ivCircle, R.id.ivUSer, R.id.ivCamera -> {
                    imageDialog()
                }
            }

        }
    }


    /**** image pick dialog  handel ***/
    private fun imageDialog() {
        imageDialog = BaseCustomDialog(requireActivity(), R.layout.video_image_picker_dialog_box) {
            when (it.id) {
                R.id.tvCamera, R.id.imageCamera -> {
                    if (!BindingUtils.hasPermissions(
                            requireActivity(), BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher1.launch(BindingUtils.permissions)
                    } else {
                        // camera
                        openCamera()
                    }
                    imageDialog!!.dismiss()
                }

                R.id.imageGallery, R.id.tvGallery -> {
                    if (!BindingUtils.hasPermissions(
                            requireActivity(), BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher.launch(BindingUtils.permissions)

                    } else {
                        openGallery()
                    }
                    imageDialog!!.dismiss()
                }

            }
        }
        imageDialog!!.create()
        imageDialog!!.show()

    }


    private fun openGallery() {
        photoPicker.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                Glide.with(this).load(it).into(binding.ivUSer)
                multipartPart = convertMultipartPart(requireContext(), it)
                viewModel.uploadProfile(Constants.UPLOAD, multipartPart)
            }
        }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) == null) {
            showErrorToast("Camera not available")
            return
        }

        photoFile2 = AppUtils.createImageFile1(requireActivity())
        photoURI = FileProvider.getUriForFile(
            requireActivity(), "${requireActivity().packageName}.fileProvider", photoFile2!!
        )

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        try {
            resultLauncherCamera.launch(cameraIntent)
        } catch (e: Exception) {
            showErrorToast("Unable to open camera")
        }
    }

    private val resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && photoURI != null) {
                Glide.with(this).load(photoURI).into(binding.ivUSer)
                multipartPart = convertMultipartPart(requireContext(), photoURI!!)
                viewModel.uploadProfile(Constants.UPLOAD, multipartPart)
            }
        }


    /**** Gallery permission  ***/
    private var allGranted = false
    private val permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            for (it in permissions.entries) {
                it.key
                val isGranted = it.value
                allGranted = isGranted
            }
            when {
                allGranted -> {
                    openGallery()
                }

                else -> {
                    showInfoToast("Permission Denied")
                }
            }
        }


    private val permissionResultLauncher1: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                openCamera()
            } else {
                showInfoToast("Permission Denied")
            }
        }


    /*** convert image in multipart ***/
    private fun convertMultipartPart(context: Context, imageUri: Uri): MultipartBody.Part? {
        return try {
            // Open input stream from content resolver
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null

            // Create a temp file to copy data
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            // Create multipart from the temp file
            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

        } catch (e: Exception) {
            e.printStackTrace()
            null
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
                                val model: SignupResponse? = BindingUtils.parseJson(jsonData)
                                val loginData = model?.user
                                if (loginData != null) {
                                    loginData.let { it1 ->
                                        sharedPrefManager.setLoginData(it1)
                                    }
                                    showSuccessToast(model.message.toString())
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }

                        "uploadProfile" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: UploadProfileAPi? = BindingUtils.parseJson(jsonData)
                                val imageUrl = model?.url
                                if (imageUrl != null) {
                                    profileImage = imageUrl
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

    fun String.toDisplayDob(): String {
        return try {
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val outputFormat = SimpleDateFormat(
                "dd MMM yyyy", Locale.US
            )

            val date = inputFormat.parse(this)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

}