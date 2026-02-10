package com.edu.lite.ui.dash_board.home.reading

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.GetReadingApiResponse
import com.edu.lite.data.model.QuizAnswerApiResponse
import com.edu.lite.data.model.QuizQuestionApiResponse
import com.edu.lite.data.model.UpdateReadingStatusModel
import com.edu.lite.data.model.UpdateReadingStatusModelData
import com.edu.lite.databinding.FragmentReadingSessionBinding
import com.edu.lite.ui.dash_board.home.quiz.FeaturedQuizzesVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadingSessionFragment : BaseFragment<FragmentReadingSessionBinding>() {
    private val viewModel: FeaturedQuizzesVM by viewModels()
    private val args: ReadingSessionFragmentArgs by navArgs()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_reading_session
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        binding.tvReading.text = args.content
        // click
        initOnClick()
        // observer
        initObserver()
    }

    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> {
                    val data = HashMap<String, Any>()
                    data["readingId"]=args.quizId
                    data["status"]="in-progress"
                    viewModel.updateReadingResponse(Constants.UPDATE_READING_STATUS,data)
                }
                R.id.btnEdit->{
                    val data = HashMap<String, Any>()
                    data["readingId"]=args.quizId
                    data["status"]="completed"
                    viewModel.updateReadingResponse(Constants.UPDATE_READING_STATUS,data)
                }
            }
        }
    }

    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "updateReadingProgress" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: UpdateReadingStatusModel? =
                                    BindingUtils.parseJson(jsonData)
                                val response = model?.data
                                if (response != null) {
                                    requireActivity().onBackPressedDispatcher.onBackPressed()
                                } else {
                                    showErrorToast(getString(R.string.something_went_wrong))
                                }
                            }.onFailure { e ->
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