package com.edu.lite.ui.dash_board.home.reading

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.QuizAnswerApiResponse
import com.edu.lite.data.model.QuizQuestionApiResponse
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
        // api call
        viewModel.quizQuestionApi(Constants.GET_READING)
        // click
        initOnClick()
        // observer
        initObserver()
    }

    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
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
                        "quizQuestionApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: QuizQuestionApiResponse? =
                                    BindingUtils.parseJson(jsonData)
                                if (model != null) {
                                } else showErrorToast(getString(R.string.something_went_wrong))
                            }.onFailure { e ->
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }

                        "postUserResponse" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: QuizAnswerApiResponse? =
                                    BindingUtils.parseJson(jsonData)
                                val question = model?.userResponse
                                if (question != null) {
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