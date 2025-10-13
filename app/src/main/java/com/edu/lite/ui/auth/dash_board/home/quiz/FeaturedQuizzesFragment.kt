package com.edu.lite.ui.auth.dash_board.home.quiz

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.FeaturedApiResponse
import com.edu.lite.data.model.FeaturedQuizze
import com.edu.lite.data.model.FeaturedQuizzesModel
import com.edu.lite.data.model.GradeByIdResponse
import com.edu.lite.databinding.FragmentFeaturedQuizzesBinding
import com.edu.lite.databinding.RvFeaturedQuizzesItemBinding
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.DummyList
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeaturedQuizzesFragment : BaseFragment<FragmentFeaturedQuizzesBinding>() {
    private val viewModel: FeaturedQuizzesVM by viewModels()
    private val args: FeaturedQuizzesFragmentArgs by navArgs()
    private lateinit var featuredQuizzesAdapter: SimpleRecyclerViewAdapter<FeaturedQuizze, RvFeaturedQuizzesItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_featured_quizzes
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnFeatureQuizzesAdapter()
        // click
        initOnClick()
        // api call
        val data = HashMap<String, String>()
        data["subject"] = args.subject
        data["type"] = args.featuredType
        viewModel.getFeaturesApi(data, Constants.TEST_QUIZ_DATA)

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
                        "getFeaturesApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: FeaturedApiResponse? = BindingUtils.parseJson(jsonData)
                                val grade = model?.quizzes
                                if (grade != null) {
                                    featuredQuizzesAdapter.list = grade
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
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> {
                    findNavController().popBackStack()
                }
            }

        }
    }

    /**
     * featured Quizzes adapter
     */
    private fun initOnFeatureQuizzesAdapter() {
        featuredQuizzesAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_featured_quizzes_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.clFeatures -> {
                        val action =
                            FeaturedQuizzesFragmentDirections.navigateToQuizQuestionFragment(quizId = m._id.toString())
                        BindingUtils.navigateWithSlide(findNavController(), action)
                    }

                }
            }

        binding.rvFeaturedQuizzes.adapter = featuredQuizzesAdapter

    }
}

