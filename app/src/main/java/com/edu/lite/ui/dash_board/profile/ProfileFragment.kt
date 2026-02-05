package com.edu.lite.ui.dash_board.profile

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.CommonApiResponse
import com.edu.lite.data.model.GetHomeQuestApi
import com.edu.lite.data.model.SignupResponse
import com.edu.lite.data.model.Stats
import com.edu.lite.databinding.FragmentProfileBinding
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.count
import kotlin.collections.orEmpty


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

        // set data
        val userData = sharedPrefManager.getLoginData()
        userData.let {
//            Glide.with(this).load(Constants.BASE_URL_IMAGE+it?.profilePicture).placeholder(R.drawable.person_holder)
//                .into(binding.ivUSer)
            if (it?.profilePicture.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(Constants.BASE_URL_IMAGE + it?.profilePicture)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(binding.ivUSer)
            } else {
                Glide.with(requireContext())
                    .load(Constants.BASE_URL_IMAGE + it?.profilePicture)
                    .placeholder(R.drawable.progress_drawable)
                    .error(R.drawable.user)
                    .into(binding.ivUSer)
            }
        }

        // api call
        val data = HashMap<String, Any>()
        val grade = sharedPrefManager.getLoginData()?.grade
        val todayDate = getCurrentDate()
        if (!grade.isNullOrEmpty()) {
            data["class"] = grade
            data["date"] = todayDate
            viewModel.getHomeApi(data, Constants.DAILY_QUEST)
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
                        "getHomeApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: GetHomeQuestApi? = BindingUtils.parseJson(jsonData)
                                val quests = model?.quests.orEmpty()
                                val completeCount = quests.count {
                                    it?.userProgress?.status == "completed"
                                }
                                binding.tvProblems.text = when {
                                    completeCount <= 0 -> "No quest found"
                                    completeCount == 1 -> "Finish 1 problem"
                                    else -> "Finish $completeCount problems"
                                }
                                setProgress(completed = completeCount, total = quests.size)
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(getString(R.string.something_went_wrong))
                            }.also {
                                viewModel.getProfileApi(Constants.GET_PROFILE)
                            }
                        }
                        "getProfileApi"->{
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: SignupResponse? = BindingUtils.parseJson(jsonData)
                                val loginData = model?.stats
                                if (loginData!=null) {
                                    binding.bean = loginData
                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure {e->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(getString(R.string.something_went_wrong))
                            }.also {
                                hideLoading()
                            }
                        }
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
                    binding.bean?.let {
                        BindingUtils.navigateWithSlide(
                            findNavController(),
                            ProfileFragmentDirections.navigateToBadgesFragment(it)
                        )
                    }

                }

                R.id.clRewards -> {
                    val action = ProfileFragmentDirections.navigateToRewardsFragment()
                    BindingUtils.navigateWithSlide(findNavController(), action)

                }
            }

        }
    }
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private fun calculateProgressPercent(completed: Int, total: Int): Float {
        if (total <= 0) return 0f
        return completed.toFloat() / total.toFloat()
    }
    private fun setProgress(completed: Int, total: Int) {
        val percent = calculateProgressPercent(completed, total)

        val params =
            binding.progressionGuideline.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = percent.coerceIn(0f, 1f)
        binding.progressionGuideline.layoutParams = params
    }


}